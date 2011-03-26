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

import org.chromattic.common.logging.Logger;
import org.chromattic.api.Status;
import org.chromattic.api.DuplicateNameException;
import org.chromattic.api.NameConflictResolution;
import org.chromattic.core.mapper.NodeTypeMapper;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.core.jcr.LinkType;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PathNotFoundException;
import javax.jcr.nodetype.NodeType;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DomainSessionImpl extends DomainSession {

  /** . */
  final Domain domain;

  /** . */
  private final Map<String, EntityContext> contexts;

  /** . */
  private final Logger log = Logger.getLogger(DomainSession.class);

  public DomainSessionImpl(Domain domain, SessionWrapper sessionWrapper) {
    super(domain, sessionWrapper);

    //
    this.domain = domain;
    this.contexts = new HashMap<String, EntityContext>();
  }

  protected <O> O _findByPath(EntityContext ctx, Class<O> clazz, String relPath) throws RepositoryException {
    Node origin;
    if (ctx != null) {
      origin = ctx.state.getNode();
    } else {
      origin = getRoot();
      nodeRead(origin);
    }
    try {
      Node node = origin.getNode(relPath);
      nodeRead(node);
      return findByNode(clazz, node);
    }
    catch (PathNotFoundException e) {
      return null;
    }
  }

  protected String _persist(EntityContext ctx, String relPath) throws RepositoryException {
    if (ctx == null) {
      throw new NullPointerException("No null object context accepted");
    }
    if (relPath == null) {
      throw new NullPointerException("No relative path specified");
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
    return _persist(getRoot(), relPath, ctx);
  }

  /**
   * Insert a context as a child of a parent context.
   *
   * @param srcCtx the source context
   * @param relPath the destination path relative to the source context
   * @param dstCtx the destination context
   * @return the id of the inserted context
   * @throws NullPointerException
   * @throws IllegalArgumentException
   * @throws IllegalStateException
   * @throws RepositoryException
   */
  protected String _persist(EntityContext srcCtx, String relPath, EntityContext dstCtx) throws
    NullPointerException,
    IllegalArgumentException,
    IllegalStateException,
    RepositoryException {
    if (srcCtx == null) {
      String msg = "Cannot insert context " + dstCtx + " as a child of a null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (dstCtx.getStatus() != Status.TRANSIENT) {
      String msg = "Attempt to insert non transient context " + dstCtx + " as child of " + srcCtx;
      log.error(msg);
      throw new IllegalStateException(msg);
    }
    if (relPath == null) {
      String msg = "Attempt to insert context " + dstCtx + " with no relative path to " + srcCtx;
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (srcCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to insert context " + dstCtx + " as child of non persistent context " + srcCtx;
      log.error(msg);
      throw new IllegalStateException(msg);
    }

    //
    Node parentNode = srcCtx.state.getNode();

    //
    return _persist(parentNode, relPath, dstCtx);
  }

  private String _persist(Node srcNode, String relPath, EntityContext dstCtx) throws RepositoryException {
    if (!(dstCtx.mapper instanceof NodeTypeMapper)) {
      throw new IllegalArgumentException("Cannot persist an object mapper to a mixin type " + dstCtx.mapper);
    }
    NodeTypeMapper mapper = (NodeTypeMapper)dstCtx.mapper;

    //
    Node dstParentNode;
    String name;
    int pos = relPath.indexOf('/');
    if (pos == -1) {
      dstParentNode = srcNode;
      name = relPath;
    } else {
      String dstParentPath = relPath.substring(0, pos);
      dstParentNode = srcNode.getNode(dstParentPath);
      name = relPath.substring(pos + 1);
    }

    //
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NodeType parentNodeType = dstParentNode.getPrimaryNodeType();
    TypeMapper parentTypeMapper = domain.getTypeMapper(parentNodeType.getName());
    if (parentTypeMapper != null) {
      onDuplicate = parentTypeMapper.getOnDuplicate();
    }

    // Check insertion capability
    Node previousNode = sessionWrapper.getNode(dstParentNode, name);
    if (previousNode != null) {
      log.trace("Found existing child with same name {}", name);
      if (onDuplicate == NameConflictResolution.FAIL) {
        String msg = "Attempt to insert context " + dstCtx + " as an existing child with name " + relPath + " child of node " + dstParentNode.getPath();
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
    log.trace("Adding node for context {} and node type {} as child of node {}", dstCtx, primaryNodeTypeName, dstParentNode.getPath());

    //
    Node dstNode = sessionWrapper.addNode(dstParentNode, name, primaryNodeTypeName, Collections.<String>emptyList());

    //
    nodeAdded(dstNode, dstCtx);
    String relatedId = dstNode.getUUID();

    //
    log.trace("Added context {} for id {} and path {}", dstCtx, relatedId, dstNode.getPath());
    return relatedId;
  }

  @Override
  protected void _move(EntityContext srcCtx, EntityContext dstCtx) throws RepositoryException {
    if (srcCtx == null) {
      String msg = "Cannot move null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (dstCtx == null) {
      String msg = "Cannot move to null context";
      log.error(msg);
      throw new NullPointerException(msg);
    }
    if (srcCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to move non persistent context " + srcCtx + " as child of " + dstCtx;
      log.error(msg);
      throw new IllegalStateException(msg);
    }
    if (dstCtx.getStatus() != Status.PERSISTENT) {
      String msg = "Attempt to move child " + srcCtx + " to a non persistent context " + dstCtx;
      log.error(msg);
      throw new IllegalStateException(msg);
    }

    //
    Node dstNode = dstCtx.state.getNode();
    Node srcNode = srcCtx.state.getNode();
    String name = srcNode.getName();

    //
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NodeType parentNodeType = dstNode.getPrimaryNodeType();
    TypeMapper parentTypeMapper = domain.getTypeMapper(parentNodeType.getName());
    if (parentTypeMapper != null) {
      onDuplicate = parentTypeMapper.getOnDuplicate();
    }

    // Check insertion capability
    Node previousNode = sessionWrapper.getNode(dstNode, name);
    if (previousNode != null) {
      log.trace("Found existing child with same name {}", name);
      if (onDuplicate == NameConflictResolution.FAIL) {
        String msg = "Attempt to move context " + dstCtx + " as an existing child with name " + name + " child of node " + dstNode.getPath();
        log.error(msg);
        throw new DuplicateNameException(msg);
      } else {
        log.trace("About to remove same name {} child with id {}", previousNode.getPath(), previousNode.getName());
        //previousNode.remove();
        throw new UnsupportedOperationException("Do that properly");
      }
    }

    //
    sessionWrapper.move(srcNode, dstNode);

    // Generate some kind of event ????
  }

  protected void _orderBefore(EntityContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) throws RepositoryException {

    if (parentCtx == null) {
      throw new NullPointerException();
    }
    if (srcCtx == null) {
      throw new NullPointerException();
    }

    //
    Node parentNode = parentCtx.state.getNode();
    Node srcNode = srcCtx.state.getNode();
    Node dstNode = dstCtx != null ? dstCtx.state.getNode() : null;

    //
    sessionWrapper.orderBefore(parentNode, srcNode, dstNode);
  }

  protected <O> O _create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, RepositoryException {
    if (clazz == null) {
      throw new NullPointerException();
    }

    //
    TypeMapper typeMapper = domain.getTypeMapper(clazz);
    TransientEntityContextState state = new TransientEntityContextState(this);

    //
    EntityContext ctx = new EntityContext(typeMapper, state);

    //
    if (name != null) {
      ctx.setName(name);
    }

    //
    broadcaster.created(ctx.getObject());

    //
    return clazz.cast(ctx.getObject());
  }

  protected <O> O _findById(Class<O> clazz, String id) throws RepositoryException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (id == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = contexts.get(id);

    // Attempt to load the object
    if (ctx == null) {
      try {
        log.trace("About to load node with id {} and class {}", id, clazz.getName());
        Node node = sessionWrapper.getNodeByUUID(id);
        nodeRead(node);
        log.trace("Loaded node with id {}", id, clazz.getName());
        ctx = contexts.get(id);
        log.trace("Obtained context {} node for id {} and class {}", ctx, id, clazz.getName());
      }
      catch (ItemNotFoundException e) {
        log.trace("Could not find node with id {}", id, clazz.getName());
        return null;
      }
      catch (RepositoryException e) {
        throw new RuntimeException(e);
      }
    }

    //
    if (ctx == null) {
      return null;
    } else {
      Object object = ctx.object;
      if (clazz.isInstance(object)) {
        return clazz.cast(object);
      } else {
        String msg = "Could not cast context " + ctx + " with class " + object.getClass().getName() + " to class " + clazz.getName();
        throw new ClassCastException(msg);
      }
    }
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

  private void remove(Node node) throws RepositoryException {
    Iterator<String> ids = sessionWrapper.remove(node);
    while (ids.hasNext()) {
      String id = ids.next();
      nodeRemoved(id);
    }
  }

  protected Object _getReferenced(EntityContext referentCtx, String name, LinkType linkType) throws RepositoryException {
    if (referentCtx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException();
    }
    Node referent = referentCtx.state.getNode();
    Node referenced = sessionWrapper.getReferenced(referent, name, linkType);
    if (referenced != null) {
      return findByNode(Object.class, referenced);
    } else {
      return null;
    }
  }

  protected boolean _setReferenced(EntityContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws RepositoryException {
    if (referentCtx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException("Cannot create a relationship with a non persisted context " + this);
    }

    //
    Node referent = referentCtx.state.getNode();

    // Then create
    if (referencedCtx != null) {
      if (referencedCtx.getStatus() != Status.PERSISTENT) {
        throw new IllegalStateException();
      }

      // Should do some type checking probably!!!!

      //
      Node referenced = referencedCtx.state.getNode();

      //
      return referenced != sessionWrapper.setReferenced(referent, name, referenced, linkType);
    } else {
      return null != sessionWrapper.setReferenced(referent, name, null, linkType);
    }
  }

  protected <T> Iterator<T> _getReferents(EntityContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws RepositoryException {
    Node referenced = referencedCtx.state.getNode();
    Iterator<Node> referents = sessionWrapper.getReferents(referenced, name, linkType);
    return new ReferentCollectionIterator<T>(this, referents, filterClass, name);
  }

  protected void _removeChild(EntityContext ctx, String name) throws RepositoryException {
    name = encodeName(name);
    Node node = ctx.state.getNode();
    Node childNode = sessionWrapper.getNode(node, name);
    if (childNode != null) {
      remove(childNode);
    }
  }

  protected Object _getChild(EntityContext ctx, String name) throws RepositoryException {
    name = encodeName(name);
    Node node = ctx.state.getNode();
    log.trace("About to load the name child {} of context {}", name, this);
    Node child = sessionWrapper.getChild(node, name);
    if (child != null) {
      log.trace("Loaded named child {} of context {} with id {}", name, this, child.getUUID());
      return findByNode(Object.class, child);
    } else {
      log.trace("No child named {} to load for context {}", name, this);
      return null;
    }
  }

  protected <T> Iterator<T> _getChildren(EntityContext ctx, Class<T> filterClass) throws RepositoryException {
    Node node = ctx.state.getNode();
    Iterator<Node> iterator = sessionWrapper.getChildren(node);
    return new ChildCollectionIterator<T>(this, iterator, filterClass);
  }

  protected Object _getParent(EntityContext ctx) throws RepositoryException {
    if (ctx.getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException();
    }
    Node node = ctx.state.getNode();
    Node parent = sessionWrapper.getParent(node);
    return findByNode(Object.class, parent);
  }

  protected Node _getRoot() throws RepositoryException {
    if ("/".equals(domain.rootNodePath)) {
      return sessionWrapper.getSession().getRootNode();
    } else {
      return (Node)sessionWrapper.getSession().getItem(domain.rootNodePath);
    }
  }

  public void nodeRead(Node node) throws RepositoryException {
    NodeType nodeType = node.getPrimaryNodeType();
    String nodeTypeName = nodeType.getName();
    TypeMapper mapper = domain.getTypeMapper(nodeTypeName);
    if (mapper != null) {
      String id = node.getUUID();
      EntityContext ctx = contexts.get(id);
      if (ctx == null) {
        ctx = new EntityContext(mapper);
        log.trace("Inserted context {} loaded from node id {}", ctx, id);
        contexts.put(id, ctx);
        PersistentEntityContextState persistentState = new PersistentEntityContextState(node, this);
        ctx.state = persistentState;
        broadcaster.loaded(persistentState, ctx.getObject());
      }
      else {
        log.trace("Context {} is already present for id ", ctx, id);
      }
    }
    else {
      log.trace("Could not find mapper for node type {}", nodeTypeName);
    }
  }

  public void nodeAdded(Node node, EntityContext ctx) throws RepositoryException {
    NodeType nodeType = node.getPrimaryNodeType();
    String nodeTypeName = nodeType.getName();
    TypeMapper mapper = domain.getTypeMapper(nodeTypeName);
    if (mapper != null) {
      String id = node.getUUID();
      if (contexts.containsKey(id)) {
        String msg = "Attempt to replace an existing context " + ctx + " with id " + id;
        log.error(msg);
        throw new AssertionError(msg);
      }
      log.trace("Inserted context {} for id {}", ctx, id);
      contexts.put(id, ctx);
      PersistentEntityContextState persistentState = new PersistentEntityContextState(node, this);
      ctx.state = persistentState;
      broadcaster.added(persistentState, ctx.getObject());
    }
    else {
      log.trace("Could not find mapper for node type {}", nodeTypeName);
    }
  }

  public void nodeRemoved(String nodeId) throws RepositoryException {
    log.trace("Removing context for id {}", nodeId);
    EntityContext ctx = contexts.remove(nodeId);
    if (ctx != null) {
      PersistentEntityContextState persistentState = (PersistentEntityContextState)ctx.state; 
      ctx.state = new RemovedEntityContextState(nodeId);
      broadcaster.removed(persistentState, ctx.getObject());
      log.trace("Removed context {} for id {}", ctx, nodeId);
    } else {
      log.trace("Context absent for removal for id {}", ctx, nodeId);
    }
  }

  public void close() {
    sessionWrapper.close();
  }
}
