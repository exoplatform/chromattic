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

package org.chromattic.core.jcr;

import org.chromattic.common.logging.Logger;
import org.chromattic.spi.jcr.SessionLifeCycle;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SessionWrapperImpl implements SessionWrapper {

  /** . */
  private static final Logger log = Logger.getLogger(SessionWrapperImpl.class);

  /** . */
  private Session session;

  /** . */
  private AbstractLinkManager[] linkMgrs;

  /** . */
  private SessionLifeCycle sessionLifeCycle;

  /** . */
  private final boolean hasPropertyOptimized;

  /** . */
  private final boolean hasNodeOptimized;

  public SessionWrapperImpl(
    SessionLifeCycle sessionLifeCycle,
    Session session,
    boolean hasPropertyOptimized,
    boolean hasNodeOptimized) {

    //
    this.hasPropertyOptimized = hasPropertyOptimized;
    this.hasNodeOptimized = hasNodeOptimized;
    this.sessionLifeCycle = sessionLifeCycle;
    this.session = session;
    this.linkMgrs = new AbstractLinkManager[] {
      new ReferenceLinkManager(this),
      new PathLinkManager(this)
    };
  }

  @SuppressWarnings("unchecked")
  public Iterator<Property> getProperties(Node node) throws RepositoryException {
    return node.getProperties();
  }

  @SuppressWarnings("unchecked")
  public Iterator<Property> getProperties(Node node, String names) throws RepositoryException {
    return node.getProperties(names);
  }

  public boolean hasProperty(Node node, String relPath) throws RepositoryException {
    return node.hasProperty(relPath);
  }

  public Property getProperty(Node node, String relPath) throws RepositoryException {
    if (hasPropertyOptimized) {
      try {
        return node.getProperty(relPath);
      }
      catch (PathNotFoundException e) {
        if (log.isTraceEnabled()) {
          log.trace("The property '" + relPath + "' could not be found under " + node.getPath(), e);
        }
        return null;
      }
    } else {
      if (node.hasProperty(relPath)) {
        return node.getProperty(relPath);
      } else {
        return null;
      }
    }
  }

  public Node getNode(String path) throws RepositoryException {
    try {
      Item item = session.getItem(path);
      if (item instanceof Node) {
        return (Node)item;
      }
    }
    catch (PathNotFoundException ignore) {
    }
    return null;
  }

  public Node getNode(Node node, String relPath) throws RepositoryException {
    if (hasNodeOptimized) {
      try {
        return node.getNode(relPath);
      }
      catch (PathNotFoundException e) {
        return null;
      }
    } else {
      if (node.hasNode(relPath)) {
        return node.getNode(relPath);
      } else {
        return null;
      }
    }
  }

  public NodeType getNodeType(String nodeTypeName) throws RepositoryException {
    NodeTypeManager mgr = session.getWorkspace().getNodeTypeManager();
    return mgr.getNodeType(nodeTypeName);
  }

  public Node addNode(Node parentNode, String relPath, String primartyNodeTypeName, List<String> mixinNodeTypeNames) throws RepositoryException {

    Node childNode = parentNode.addNode(relPath, primartyNodeTypeName);
    if (mixinNodeTypeNames instanceof RandomAccess) {
      int size = mixinNodeTypeNames.size();
      for (int i = 0;i < size;i++) {
        String mixinNodeTypeName = mixinNodeTypeNames.get(i);
        childNode.addMixin(mixinNodeTypeName);
      }
    } else {
      for (String mixinNodeTypeName : mixinNodeTypeNames) {
        childNode.addMixin(mixinNodeTypeName);
      }
    }
    return childNode;
  }

  public void move(Node srcNode, Node dstNode, String dstName) throws RepositoryException {
    String dstPath = dstNode.getPath() + "/" + dstName;
    session.move(srcNode.getPath(), dstPath);
  }

  public void orderBefore(Node parentNode, Node srcNode, Node dstNode) throws RepositoryException {
    if (dstNode != null) {
      parentNode.orderBefore(srcNode.getName(), dstNode.getName());
    } else {
      long size = parentNode.getNodes().getSize();
      if (size > 1) {
        parentNode.orderBefore(srcNode.getName(), null);
      }
    }
  }

  public Node getNodeByUUID(String uuid) throws RepositoryException {
    try {
      return session.getNodeByUUID(uuid);
    }
    catch (ItemNotFoundException e) {
      return null;
    }
  }

  public Node getParent(Node childNode) throws RepositoryException {
    return childNode.getParent();
  }

  public Iterator<Node> getChildren(Node parentNode) throws RepositoryException {
    return (Iterator<Node>)parentNode.getNodes();
  }

  public boolean hasChildren(Node parentNode) throws RepositoryException {
    return parentNode.hasNodes();
  }

  public Node getChild(Node parentNode, String name) throws RepositoryException {
    return getNode(parentNode, name);
  }

  public boolean hasChild(Node parentNode, String name) throws RepositoryException {
    return parentNode.hasNode(name);
  }

  /**
   * Remove a node recursively in order to have one remove event generated for every descendants of the node in order to
   * keep the contexts state corrects. It also remove all existing references to that node.
   *
   * @param node the node to remove
   * @throws RepositoryException any repository exception
   */
  public void remove(Node node) throws RepositoryException {

    //
    cleanReferencesForRemoval(node);

    //
    node.remove();
  }

  public boolean canAddMixin(Node node, String mixinTypeName) throws RepositoryException {
    return node.canAddMixin(mixinTypeName);
  }

  public void addMixin(Node node, String mixinTypeName) throws RepositoryException {
    node.addMixin(mixinTypeName);
  }

  public boolean removeMixin(Node node, String mixinTypeName) throws RepositoryException {
    try {
      node.removeMixin(mixinTypeName);
      return true;
    } catch (NoSuchNodeTypeException ignore) {
      // Mixin was not here
      return false;
    }
  }

  public boolean haxMixin(Node node, String mixinTypeName) throws RepositoryException {
    for (NodeType mixinNodeType : node.getMixinNodeTypes()) {
      if (mixinNodeType.getName().equals(mixinTypeName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Need to find a way to optimized this method as it forces us to visit the entire children hierarchy.
   *
   * @param node the node to be removed
   * @throws RepositoryException any repository exception
   */
  public void cleanReferencesForRemoval(Node node) throws RepositoryException {
    for (NodeIterator i = node.getNodes(); i.hasNext();) {
      Node child = i.nextNode();
      cleanReferencesForRemoval(child);
    }

    // Cleanup
    for (PropertyIterator i = node.getReferences(); i.hasNext();) {
      Property property = i.nextProperty();
      property.setValue((Node)null);
    }

    // Update reference manager
    for (PropertyIterator i = node.getProperties(); i.hasNext();) {
      Property property = i.nextProperty();
      if (property.getType() == PropertyType.REFERENCE) {
        linkMgrs[LinkType.REFERENCE.index].setReferenced(node, property.getName(), null);
      } else if (property.getType() == PropertyType.PATH) {
        linkMgrs[LinkType.PATH.index].setReferenced(node, property.getName(), null);
      }
    }
  }

  public void save() throws RepositoryException {
    sessionLifeCycle.save(session);
    for (AbstractLinkManager mgr : linkMgrs) {
      mgr.clear();
    }
  }

  public Node getReferenced(Node referent, String propertyName, LinkType linkType) throws RepositoryException {
    return linkMgrs[linkType.index].getReferenced(referent, propertyName);
  }

  public Node setReferenced(Node referent, String propertyName, Node referenced, LinkType linkType) throws RepositoryException {
    return linkMgrs[linkType.index].setReferenced(referent, propertyName, referenced);
  }

  public Iterator<Node> getReferents(Node referenced, String propertyName, LinkType linkType) throws RepositoryException {
    return linkMgrs[linkType.index].getReferents(referenced, propertyName);
  }

  @Override
  public int hashCode() {
    return session.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof SessionWrapperImpl) {
      SessionWrapperImpl that = (SessionWrapperImpl)obj;
      return session == that.session;
    }
    return false;
  }

  public Session getSession() {
    return session;
  }

  public boolean isClosed()
  {
    return session == null;
  }

  public void close() {
    if (session != null)
    {
      for (AbstractLinkManager mgr : linkMgrs) {
        mgr.clear();
      }
      sessionLifeCycle.close(session);
      session = null;
    }
  }

  public Query createQuery(String statement) throws RepositoryException {
    QueryManager queryMgr = session.getWorkspace().getQueryManager();
    return queryMgr.createQuery(statement, Query.SQL);
  }

  public QueryResult executeQuery(Query query, Long offset, Long limit) throws RepositoryException {
    if (offset != null && offset > 0)
    {
      invokeLongSetter(query, "setOffset", offset);
    }
    if (limit != null && limit >= 0)
    {
      invokeLongSetter(query, "setLimit", limit);
    }
    return query.execute();
  }

  private int invokeIntGetter(Object o, String methodName)
  {
    Class<?> clazz = o.getClass();
    int hits = -1;
    try {
      Method getter = clazz.getMethod(methodName);
      Class<?> ret = getter.getReturnType();
      if (ret.equals(int.class))
      {
        hits = (Integer)getter.invoke(o);
      }
    }
    catch (NoSuchMethodException ignore) {
      log.trace("Could not find method " + methodName + " on " + clazz.getName(), ignore);
    }
    catch (Exception e) {
      log.error("Could not invoke " + methodName + " of class " + clazz.getName() +  " on " + o, e);
    }
    return hits;
  }

  private void invokeLongSetter(Object o, String methodName, Long value)
  {
    Class<?> clazz = o.getClass();
    try {
      Method setter = clazz.getMethod(methodName, long.class);
      setter.invoke(o, value);
    }
    catch (NoSuchMethodException ignore) {
      log.trace("Could not find method " + methodName + " on " + clazz.getName(), ignore);
    }
    catch (Exception e) {
      log.error("Could not invoke " + methodName + " of class " + clazz.getName() +  " on " + o + " with value " + value, e);
    }
  }
}
