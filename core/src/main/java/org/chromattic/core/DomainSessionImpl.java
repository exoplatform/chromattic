/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.chromattic.core;

import org.chromattic.api.NoSuchNodeException;
import org.chromattic.api.Status;
import org.chromattic.api.DuplicateNameException;
import org.chromattic.api.NameConflictResolution;
import org.chromattic.core.jcr.type.MixinTypeInfo;
import org.chromattic.core.jcr.type.PrimaryTypeInfo;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import static org.chromattic.common.JCR.qualify;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DomainSessionImpl extends DomainSession {

  /** . */
  final Domain domain;

  /** . */
  private Map<String, EntityContext> contexts;

  public DomainSessionImpl(Domain domain, SessionWrapper sessionWrapper) {
    super(domain, sessionWrapper);

    //
    this.domain = domain;
    this.contexts = new HashMap<String, EntityContext>();
  }

  protected void _setLocalName(EntityContext ctx, String localName) throws RepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    switch (ctx.getStatus()) {
      case TRANSIENT:
        ((TransientEntityContextState)ctx.state).setLocalName(localName);
        break;
      case PERSISTENT:
        Node parentNode = ctx.getNode().getParent();
        String name = ctx.getNode().getName();
        int index = name.indexOf(':');
        String prefix = index == -1 ? null : name.substring(0, index);
        _move(ThrowableFactory.ISE, ctx, parentNode, prefix, localName);
        break;
      default:
        throw new IllegalStateException("Removed node cannot have its name updated");
    }
  }

  @Override
  protected String _getLocalName(EntityContext ctx) throws RepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    switch (ctx.getStatus()) {
      default:
        return ctx.state.getLocalName();
      case PERSISTENT:
        Node node = ctx.state.getNode();
        Node parentNode = node.getParent();
        String name = node.getName();
        int index = name.indexOf(':');
        String localName = index == -1 ? name : name.substring(index + 1);
        return domain.decodeName(parentNode, localName, NameKind.OBJECT);
    }
  }

  @Override
  protected <E> E _findByPath(Class<E> clazz, String path) throws RepositoryException {
    Node node = sessionWrapper.getNode(path);
    if (node != null) {
      return _findByNode(clazz, node);
    }
    return null;
  }

  protected <O> O _findByPath(EntityContext ctx, Class<O> clazz, String relPath) throws RepositoryException {
    Node origin;
    if (ctx != null) {
      origin = ctx.state.getNode();
    } else {
      origin = _getRoot();
    }
    Node node = sessionWrapper.getNode(origin, relPath);
    if (node != null) {
      return _findByNode(clazz, node);
    }
    return null;
  }

  protected <T1 extends Throwable> void _persist(ThrowableFactory<T1> nullLocaleNameTF, EntityContext ctx, String prefix, String localName) throws T1, RepositoryException {
    if (ctx == null) {
      throw new NullPointerException("No null object context accepted");
    }
    if (localName == null) {
      String msg = "No relative path specified";
      throw nullLocaleNameTF.newThrowable(msg);
    }

    //
    if (ctx.getStatus() != Status.TRANSIENT) {
      String msg = "Attempt to persist non transient object " + ctx;
      log.error(msg);
      throw new IllegalArgumentException(msg);
    }

    //
    log.trace("Setting context {} for insertion", ctx);
    log.trace("Adding node for context {} and node type {}", ctx, ctx.mapper);

    //
    _persist(_getRoot(), prefix, localName, ctx);
  }

  protected <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> void _persist(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      ThrowableFactory<T3> nullLocaleNameTF,
      ObjectContext srcCtx,
      String prefix,
      String localName,
      EntityContext dstCtx) throws
      T1, T2, T3, NullPointerException, RepositoryException {
    if (srcCtx == null) {
      String msg = "Cannot insert context " + dstCtx + " as a child of a null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (dstCtx.getStatus() != Status.TRANSIENT) {
      String msg = "Attempt to insert non transient context " + dstCtx + " as child of " + srcCtx;
      log.error(msg);
      throw dstStateTF.newThrowable();
    }
    if (localName == null) {
      String msg = "Attempt to insert context " + dstCtx + " with no name to " + srcCtx;
      log.error(msg);
      throw nullLocaleNameTF.newThrowable(msg);
    }
    if (srcCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to insert context " + dstCtx + " as child of non persistent context " + srcCtx;
      log.error(msg);
      throw srcStateTF.newThrowable(msg);
    }

    //
    Node parentNode = srcCtx.getEntity().state.getNode();

    //
    _persist(parentNode, prefix, localName, dstCtx);
  }

  private void _persist(Node srcNode, String prefix, String localName, EntityContext dstCtx) throws RepositoryException {
    ObjectMapper mapper = dstCtx.mapper;

    //
    localName = domain.encodeName(srcNode, localName, NameKind.OBJECT);

    //
    String name = qualify(prefix, localName);

    //
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NodeType parentNodeType = srcNode.getPrimaryNodeType();
    ObjectMapper parentTypeMapper = domain.getTypeMapper(parentNodeType.getName());
    if (parentTypeMapper != null) {
      onDuplicate = parentTypeMapper.getOnDuplicate();
    }

    // Check insertion capability
    // julien : that should likely instead use JCR failure for better performance
    Node previousNode = sessionWrapper.getNode(srcNode, name);
    if (previousNode != null) {
      log.trace("Found existing child with same name {}", name);
      if (onDuplicate == NameConflictResolution.FAIL) {
        String msg = "Attempt to insert context " + dstCtx + " as an existing child with name " + name + " child of node " + srcNode.getPath();
        log.error(msg);
        throw new DuplicateNameException(msg);
      } else {
        log.trace("About to remove same name {} child with id {}", previousNode.getPath(), previousNode.getName());
        remove(previousNode);
      }
    }

    //
    String primaryNodeTypeName = mapper.getNodeTypeName();
    log.trace("Setting context {} for insertion", dstCtx);
    log.trace("Adding node for context {} and node type {} as child of node {}", dstCtx, primaryNodeTypeName, srcNode.getPath());

    //
    Node dstNode = sessionWrapper.addNode(srcNode, name, primaryNodeTypeName, Collections.<String>emptyList());

    // If the node is not referenceable, make it so
    if (!domain.nodeInfoManager.isReferenceable(dstNode)) {
      dstNode.addMixin("mix:referenceable");
    }

    //
    nodeAdded(dstNode, dstCtx);

    //
    log.trace("Added context {} for path {}", dstCtx, dstCtx.getId(), dstNode.getPath());
  }

  @Override
  protected EntityContext _copy(EntityContext srcCtx, String prefix, String localName) throws RepositoryException {
    return _copy(getRoot(), srcCtx, qualify(prefix, localName));
  }

  @Override
  protected EntityContext _copy(EntityContext parentCtx, EntityContext srcCtx, String prefix, String localName) throws RepositoryException {
    if (parentCtx == null) {
      throw new NullPointerException();
    }
    if (parentCtx.getStatus() == Status.PERSISTENT) {
      throw new IllegalArgumentException("Parent object is not persistent");
    }
    return _copy(parentCtx.getNode(), srcCtx, qualify(prefix, localName));
  }

  private EntityContext _copy(Node parentNode, EntityContext srcCtx, String name) throws RepositoryException {
    if (srcCtx == null) {
      throw new NullPointerException();
    }
    if (name == null) {
      throw new NullPointerException();
    }
    if (srcCtx.getStatus() != Status.PERSISTENT) {
      throw new IllegalArgumentException("Copied object is not persistent");
    }

    //
    EntityContext dstCtx = (EntityContext)_create(srcCtx.mapper.getObjectClass(), null);

    //
    String prefix;
    String localName;
    int index = name.indexOf(':');
    if (index == -1) {
      prefix = null;
      localName = name;
    } else {
      prefix = name.substring(0, index);
      localName = name.substring(index + 1);
    }

    //
    _persist(parentNode, prefix, localName, dstCtx);

    //
    Node dstNode = dstCtx.getNode();

    // Copy mixins
    for (NodeType mixinNodeType : srcCtx.getNode().getMixinNodeTypes()) {
      dstNode.addMixin(mixinNodeType.getName());
    }

    // Copy node state
    for (Iterator<Property> i = sessionWrapper.getProperties(srcCtx.getNode());i.hasNext();) {
      Property p = i.next();
      PropertyDefinition def = p.getDefinition();
      if (def.isProtected()) {
        // We skip protected state
      } else {
        if (def.isMultiple()) {
          Value[] values = p.getValues();
          dstNode.setProperty(p.getName(), values);
        } else {
          Value value = p.getValue();
          dstNode.setProperty(p.getName(), value);
        }
      }
    }

    // Copy children
    for (Iterator<Node> i = sessionWrapper.getChildren(srcCtx.getNode());i.hasNext();) {
      Node n = i.next();
      EntityContext c = _getEntity(n);
      if (c == null) {
        throw new UnsupportedOperationException();
      } else {
        _copy(dstNode, c, n.getName());
      }
    }

    //
    return dstCtx;
  }

  @Override
  protected void _addMixin(EntityContext entityCtx, EmbeddedContext mixinCtx) throws RepositoryException {
    if (entityCtx == null) {
      throw new NullPointerException();
    }
    if (mixinCtx == null) {
      throw new NullPointerException();
    }

    // Maybe they are already wired
    if (mixinCtx.relatedEntity != null) {
      if (mixinCtx.relatedEntity != entityCtx) {
        throw new IllegalArgumentException();
      }
    } else {
      EmbeddedContext previousMixinCtx = (EmbeddedContext)entityCtx.getAttribute(mixinCtx.mapper);
      if (previousMixinCtx != null) {
        if (previousMixinCtx != mixinCtx) {
          throw new IllegalStateException();
        }
      } else {

        //
        String mixinTypeName = mixinCtx.mapper.getNodeTypeName();
        Node node = entityCtx.state.getNode();

        //
        if (!sessionWrapper.canAddMixin(node, mixinTypeName)) {
          throw new IllegalArgumentException("Cannot add mixin " + mixinCtx + " to context " + entityCtx);
        }

        // Add mixin
        sessionWrapper.addMixin(node, mixinTypeName);

        //
        NodeType mixinType = sessionWrapper.getNodeType(mixinTypeName);
        MixinTypeInfo mixinTypeInfo = domain.nodeInfoManager.getMixinTypeInfo(mixinType);

        // Perform wiring
        entityCtx.setAttribute(mixinCtx.mapper, mixinCtx);
        mixinCtx.relatedEntity = entityCtx;
        mixinCtx.typeInfo = mixinTypeInfo;
      }
    }
  }

  @Override
  protected void _removeMixin(EntityContext entityCtx, Class<?> mixinType) throws RepositoryException {
    if (entityCtx == null) {
      throw new NullPointerException();
    }
    if (mixinType == null) {
      throw new NullPointerException();
    }

    // That's a necessary evil
    ObjectMapper<EmbeddedContext> mapper = (ObjectMapper<EmbeddedContext>)domain.getTypeMapper(mixinType);

    //
    if (mapper != null) {
      String mixinTypeName = mapper.getNodeTypeName();
      Node node = entityCtx.state.getNode();
      sessionWrapper.removeMixin(node, mixinTypeName);

      // Remove from session as well
      EmbeddedContext mixinCtx = (EmbeddedContext)entityCtx.setAttribute(mapper, null);
      if (mixinCtx != null) {
        mixinCtx.relatedEntity = null;
      }
    }
  }

  @Override
  protected EmbeddedContext _getEmbedded(EntityContext entityCtx, Class<?> embeddedType) throws RepositoryException {
    if (entityCtx == null) {
      throw new NullPointerException();
    }
    if (embeddedType == null) {
      throw new NullPointerException();
    }

    // That's a necessary evil
    ObjectMapper<EmbeddedContext> mapper = (ObjectMapper<EmbeddedContext>)domain.getTypeMapper(embeddedType);

    //
    EmbeddedContext embeddedCtx = null;
    if (mapper != null) {
      embeddedCtx = (EmbeddedContext)entityCtx.getAttribute(mapper);

      //
      if (embeddedCtx == null) {
        Node node = entityCtx.state.getNode();
        if (mapper.getKind() == NodeTypeKind.MIXIN) {
          String mixinTypeName = mapper.getNodeTypeName();
          if (sessionWrapper.haxMixin(node, mixinTypeName)) {
            NodeType mixinType = sessionWrapper.getNodeType(mixinTypeName);
            MixinTypeInfo mixinTypeInfo = domain.nodeInfoManager.getMixinTypeInfo(mixinType);

            //
            embeddedCtx = new EmbeddedContext(mapper, this);
            entityCtx.setAttribute(embeddedCtx.mapper, embeddedCtx);
            embeddedCtx.relatedEntity = entityCtx;
            embeddedCtx.typeInfo = mixinTypeInfo;
          }
        } else {
          PrimaryTypeInfo typeInfo = entityCtx.state.getTypeInfo();
          PrimaryTypeInfo superTI = (PrimaryTypeInfo)typeInfo.getSuperType(mapper.getNodeTypeName());
          if (superTI != null) {
            embeddedCtx = new EmbeddedContext(mapper, this);
            entityCtx.setAttribute(embeddedCtx.mapper, embeddedCtx);
            embeddedCtx.relatedEntity = entityCtx;
            embeddedCtx.typeInfo = superTI;
          }
        }
      }
    }

    //
    return embeddedCtx;
  }

  @Override
  protected <T1 extends Throwable, T2 extends Throwable> void _move(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      EntityContext srcCtx,
      ObjectContext dstCtx,
      String dstPrefix,
      String dstLocalName) throws
    T1, T2, NullPointerException, RepositoryException {
    if (dstCtx == null) {
      String msg = "Cannot move to null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (dstCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to move child " + srcCtx + " to a non persistent context " + dstCtx;
      log.error(msg);
      throw dstStateTF.newThrowable(msg);
    }

    //
    Node dstNode = dstCtx.getEntity().state.getNode();

    //
    _move(srcStateTF, srcCtx, dstNode, dstPrefix, dstLocalName);
  }

  private <T1 extends Throwable> void _move(
      ThrowableFactory<T1> srcStateTF,
      EntityContext srcCtx,
      Node dstNode,
      String dstPrefix,
      String dstLocalName) throws T1, NullPointerException, RepositoryException {
    if (srcCtx == null) {
      String msg = "Cannot move null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (srcCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to move non persistent context " + srcCtx + " as child of " + dstNode.getPath();
      log.error(msg);
      throw srcStateTF.newThrowable(msg);
    }

    //
    dstLocalName = domain.encodeName(dstNode, dstLocalName, NameKind.OBJECT);

    //
    String dstName = qualify(dstPrefix, dstLocalName);

    //
    Node srcNode = srcCtx.state.getNode();

    //
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NodeType parentNodeType = dstNode.getPrimaryNodeType();
    ObjectMapper parentTypeMapper = domain.getTypeMapper(parentNodeType.getName());
    if (parentTypeMapper != null) {
      onDuplicate = parentTypeMapper.getOnDuplicate();
    }

    // Check insertion capability
    Node previousNode = sessionWrapper.getNode(dstNode, dstName);
    if (previousNode != null) {
      log.trace("Found existing child with same name {}", dstName);
      if (srcNode.getParent() == dstNode) {
        // We do nothing as it's a noop
      } else {
        if (onDuplicate == NameConflictResolution.FAIL) {
          String msg = "Attempt to move context " + dstNode.getPath() + " as an existing child with name " + dstName + " child of node " + dstNode.getPath();
          log.error(msg);
          throw new DuplicateNameException(msg);
        } else {
          log.trace("About to remove same name {} child with id {}", previousNode.getPath(), previousNode.getName());
          //previousNode.remove();
          throw new UnsupportedOperationException("Do that properly");
        }
      }
    } else {
      sessionWrapper.move(srcNode, dstNode, dstName);
    }

    // Generate some kind of event ????
  }

  protected void _orderBefore(ObjectContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) throws RepositoryException {

    if (parentCtx == null) {
      throw new NullPointerException();
    }
    if (srcCtx == null) {
      throw new NullPointerException();
    }

    //
    Node parentNode = parentCtx.getEntity().state.getNode();
    Node srcNode = srcCtx.state.getNode();
    Node dstNode = dstCtx != null ? dstCtx.state.getNode() : null;

    //
    sessionWrapper.orderBefore(parentNode, srcNode, dstNode);
  }

  protected ObjectContext _create(Class<?> clazz, String localName) throws NullPointerException, IllegalArgumentException, RepositoryException {
    if (clazz == null) {
      throw new NullPointerException();
    }

    //
    ObjectMapper<?> typeMapper = domain.getTypeMapper(clazz);
    if (typeMapper == null) {
      throw new IllegalArgumentException("The type " + clazz.getName() + " is not mapped");
    }

    //
    if (typeMapper.getKind() == NodeTypeKind.PRIMARY && typeMapper.isAbstract()) {
      throw new IllegalArgumentException("The type " + clazz.getName() + " is abstract");
    }

    //
    TransientEntityContextState state = new TransientEntityContextState(this);

    //
    ObjectContext octx;
    if (typeMapper.getKind() == NodeTypeKind.PRIMARY) {
      EntityContext ctx = new EntityContext((ObjectMapper<EntityContext>)typeMapper, state);

      //
      if (localName != null) {
        ctx.setLocalName(localName);
      }

      //
      broadcaster.created(ctx.getObject());

      //
      octx = ctx;
    } else {
      if (localName != null) {
        throw new IllegalArgumentException("Cannot create a mixin type with a name");
      }
      octx = new EmbeddedContext((ObjectMapper<EmbeddedContext>)typeMapper, this);
    }
    return octx;
  }

  protected <O> O _findById(Class<O> clazz, String id) throws RepositoryException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (id == null) {
      throw new NullPointerException();
    }

    // Attempt to load the object
    log.trace("About to load node with id {} and class {}", id, clazz.getName());
    Node node = sessionWrapper.getNodeByUUID(id);
    if (node != null) {
      return _findByNode(clazz, node);
    } else {
      log.trace("Could not find node with id {}", id, clazz.getName());
    }
    return null;
  }

  protected <O> O _findByNode(Class<O> clazz, Node node) throws RepositoryException {
    if (clazz == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = _getEntity(node);

    //
    if (ctx == null) {
      return null;
    } else {
      return cast(ctx, clazz);
    }
  }

  protected EntityContext _getEntity(Node node) throws RepositoryException {
    if (node == null) {
      throw new NullPointerException();
    }

    //
    if (!domain.nodeInfoManager.isReferenceable(node)) {
      log.trace("Cannot map non referenceable node {} to a chromattic object", node.getPath());
      return null;
    }

    // Attempt to get the object
    return nodeRead(node);
  }

  protected void _save() throws RepositoryException {
    sessionWrapper.save();
  }

  protected void _remove(EntityContext context) throws RepositoryException {
    if (context == null) {
      throw new NullPointerException();
    }
    switch (context.state.getStatus()) {
      case TRANSIENT:
        throw new IllegalStateException("Cannot remove transient node");
      case PERSISTENT:
        Node node = context.state.getNode();
        remove(node);
        break;
      case REMOVED:
        throw new IllegalStateException("Cannot remove removed node");
      default:
        throw new AssertionError();
    }
  }

  private static class Removed {

    private final String id;
    private final String path;
    private final String localName;
    private final EntityContext ctx;

    private Removed(String id, String path, String localName, EntityContext ctx) {
      this.id = id;
      this.path = path;
      this.localName = localName;
      this.ctx = ctx;
    }
  }

  private void remove(Node node) throws RepositoryException {
    List<Removed> removeds = new LinkedList<Removed>();
    String pathToRemove = node.getPath();
    for (Map.Entry<String, EntityContext> ctxEntry : contexts.entrySet()) {
      EntityContext ctx = ctxEntry.getValue();
      Node ctxNode = ctx.state.getNode();
      if (ctxNode.getPath().startsWith(pathToRemove)) {
        removeds.add(new Removed(ctx.getId(), ctx.getPath(), ctx.getLocalName(), ctx));
      }
    }

    // Perform removal
    sessionWrapper.remove(node);

    //
    Collection<EntityContext> ctxs = contexts.values();

    //
    for (Removed removed : removeds) {

      String path = removed.path;
      log.trace("Removing context for path {}", path);
      removed.ctx.state = new RemovedEntityContextState(this, path, removed.localName, removed.ctx.getTypeInfo());
      ctxs.remove(removed.ctx);
      broadcaster.removed(removed.id, removed.path, removed.localName, removed.ctx.getObject());
      log.trace("Removed context {} for path {}", removed.ctx, path);
    }
  }

  protected EntityContext _getReferenced(ObjectContext referentCtx, String name, LinkType linkType) throws RepositoryException {
    if (referentCtx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException();
    }
    Node referent = referentCtx.getEntity().state.getNode();
    Node referenced = sessionWrapper.getReferenced(referent, name, linkType);
    if (referenced != null) {
      return _getEntity(referenced);
    } else {
      return null;
    }
  }

  protected boolean _setReferenced(ObjectContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws RepositoryException {
    if (referentCtx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException("Cannot create a relationship with a non persisted context " + this);
    }

    //
    Node referent = referentCtx.getEntity().state.getNode();

    // Then create
    if (referencedCtx != null) {
      if (referencedCtx.getStatus() != Status.PERSISTENT) {
        throw new IllegalStateException();
      }

      // Should do some type checking probably!!!!

      //
      Node referenced = referencedCtx.state.getNode();

      //
      Node previouslyReferenced = sessionWrapper.setReferenced(referent, name, referenced, linkType);

      // OK the nodes are referenceable, they always have an UUID
      if (previouslyReferenced != null) {
        String previousReferencedId = previouslyReferenced.getUUID();
        String referencedId = referenced.getUUID();
        return !referencedId.equals(previousReferencedId);
      } else {
        return true;
      }
    } else {
      return null != sessionWrapper.setReferenced(referent, name, null, linkType);
    }
  }

  protected <T> Iterator<T> _getReferents(EntityContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws RepositoryException {
    Node referenced = referencedCtx.getEntity().state.getNode();
    Iterator<Node> referents = sessionWrapper.getReferents(referenced, name, linkType);
    return new ReferentCollectionIterator<T>(this, referents, filterClass, name);
  }

  protected void _removeChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException {
    localName = domain.encodeName(ctx, localName, NameKind.OBJECT);
    String name = qualify(prefix, localName);
    Node node = ctx.getEntity().state.getNode();
    Node childNode = sessionWrapper.getNode(node, name);
    if (childNode != null) {
      remove(childNode);
    }
  }

  protected EntityContext _getChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException {
    localName = domain.encodeName(ctx, localName, NameKind.OBJECT);
    String name = qualify(prefix, localName);
    Node node = ctx.getEntity().state.getNode();
    log.trace("About to load the name child {} of context {}", name, this);
    Node child = sessionWrapper.getChild(node, name);
    if (child != null) {
      log.trace("Loaded named child {} of context {} with path {}", name, this, child.getPath());
      return _getEntity(child);
    } else {
      log.trace("No child named {} to load for context {}", name, this);
      return null;
    }
  }

  protected boolean _hasChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException {
    localName = domain.encodeName(ctx, localName, NameKind.OBJECT);
    String name = qualify(prefix, localName);
    Node node = ctx.getEntity().state.getNode();
    return sessionWrapper.hasChild(node, name);
  }

  protected <T> Iterator<T> _getChildren(ObjectContext ctx, Class<T> filterClass) throws RepositoryException {
    Node node = ctx.getEntity().state.getNode();
    Iterator<Node> iterator = sessionWrapper.getChildren(node);
    return new ChildCollectionIterator<T>(this, iterator, filterClass);
  }

  protected EntityContext _getParent(EntityContext ctx) throws RepositoryException {
    if (ctx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException();
    }
    Node node = ctx.state.getNode();
    Node parent = sessionWrapper.getParent(node);
    return _getEntity(parent);
  }

  protected Node _getRoot() throws RepositoryException {
    Session session = sessionWrapper.getSession();
    List<String> pathSegments = domain.rootNodePathSegments;
    Node current = session.getRootNode();
    String rootNodeType = domain.rootNodeType;
    boolean created = false;
    if (!pathSegments.isEmpty()) {
      // We use that kind of loop to avoid object creation
      for (int i = 0;i < pathSegments.size();i++) {
        String pathSegment = pathSegments.get(i);
        if (current.hasNode(pathSegment)) {
          current = current.getNode(pathSegment);
        } else {
          if (domain.rootCreateMode == Domain.NO_CREATE_MODE) {
            throw new NoSuchNodeException("No existing root node " + domain.rootNodePath);
          } else {
            if (rootNodeType != null) {
              current = current.addNode(pathSegment, rootNodeType);
            } else {
              current = current.addNode(pathSegment);
            }
            created = true;
          }
        }
      }
    }
    if (created) {
      if (domain.rootCreateMode == Domain.CREATE_MODE) {
        // Find first persistent ancestor
        Node toSave = current;
        while (toSave.isNew()) {
          toSave = toSave.getParent();
        }
        // And save it
        toSave.save();
      }
    }
    return current;
  }

  private <O> O cast(EntityContext ctx, Class<O> clazz) {
    Object object = ctx.object;
    if (clazz.isInstance(object)) {
      return clazz.cast(object);
    } else {
      String msg = "Could not cast context " + ctx + " with class " + object.getClass().getName() + " to class " + clazz.getName();
      throw new ClassCastException(msg);
    }
  }

  /**
   * <p>Read the node and returns a related entity context.</p>
   *
   * </p>When the node is mapped to a chromattic type the following occurs:
   * <ul>
   * <li>any entity context already present in the current session is returned</li>
   * <li>otherwise an entity context is created from the related chromattic type and is inserted in the session</li>
   * <li>a load event is broadcasted to listeners</li>
   * </ul>
   * The node must have the mixin mix:referenceable otherwise a repositoty exception will be thrown.</p>
   *
   * <p>When the node is not mapped, null is returned.</p>
   *
   * @param node the node to read
   * @return the corresponding entity context
   * @throws RepositoryException any repository exception
   */
  private EntityContext nodeRead(Node node) throws RepositoryException {
    NodeType nodeType = node.getPrimaryNodeType();
    String nodeTypeName = nodeType.getName();
    ObjectMapper mapper = domain.getTypeMapper(nodeTypeName);
    if (mapper != null) {
      EntityContext ctx = contexts.get(node.getUUID());
      if (ctx == null) {
        ctx = new EntityContext((ObjectMapper<EntityContext>)mapper, new PersistentEntityContextState(node, this));
        log.trace("Inserted context {} loaded from node path {}", ctx, node.getPath());
        contexts.put(node.getUUID(), ctx);
        broadcaster.loaded(ctx, ctx.getObject());
      }
      else {
        log.trace("Context {} is already present for path ", ctx, node.getPath());
      }
      return ctx;
    }
    else {
      log.trace("Could not find mapper for node type {}", nodeTypeName);
      return null;
    }
  }

  private void nodeAdded(Node node, EntityContext ctx) throws RepositoryException {
    NodeType nodeType = node.getPrimaryNodeType();
    String nodeTypeName = nodeType.getName();
    ObjectMapper mapper = domain.getTypeMapper(nodeTypeName);
    if (mapper != null) {
      if (contexts.containsKey(node.getUUID())) {
        String msg = "Attempt to replace an existing context " + ctx + " with path " + node.getPath();
        log.error(msg);
        throw new AssertionError(msg);
      }
      log.trace("Inserted context {} for path {}", ctx, node.getPath());
      contexts.put(node.getUUID(), ctx);
      ctx.state = new PersistentEntityContextState(node, this);
      broadcaster.added(ctx, ctx.getObject());
    }
    else {
      log.trace("Could not find mapper for node type {}", nodeTypeName);
    }
  }

  public void _close() {
    sessionWrapper.close();
  }
}
