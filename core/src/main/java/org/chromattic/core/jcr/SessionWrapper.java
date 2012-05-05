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

import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface SessionWrapper {

  boolean hasProperty(Node node, String relPath) throws RepositoryException;

  Property getProperty(Node node, String relPath) throws RepositoryException;

  Iterator<Property> getProperties(Node node) throws RepositoryException;

  Iterator<Property> getProperties(Node node, String namePattern) throws RepositoryException;

  Node getNode(Node node, String relPath) throws RepositoryException;

  Node getNode(String path) throws RepositoryException;

  NodeType getNodeType(String nodeTypeName) throws RepositoryException;

  Node addNode(Node parentNode, String relPath, String primartyNodeTypeName, List<String> mixinNodeTypeNames) throws RepositoryException;

  void orderBefore(Node parentNode, Node srcNode, Node dstNode) throws RepositoryException;

  void move(Node srcNode, Node dstNode, String dstName) throws RepositoryException;

  Node getParent(Node childNode) throws RepositoryException;

  Iterator<Node> getChildren(Node parentNode) throws RepositoryException;

  boolean hasChildren(Node parentNode) throws RepositoryException;
  
  Node getChild(Node parentNode, String name) throws RepositoryException;

  boolean hasChild(Node parentNode, String name) throws RepositoryException;

  Node getNodeByUUID(String uuid) throws RepositoryException;

  void remove(Node node) throws RepositoryException;

  boolean canAddMixin(Node node, String mixinTypeName) throws RepositoryException;

  void addMixin(Node node, String mixinTypeName) throws RepositoryException;

  boolean removeMixin(Node node, String mixinTypeName) throws RepositoryException;

  boolean haxMixin(Node node, String mixinTypeName) throws RepositoryException;

  void save() throws RepositoryException;

  Node setReferenced(Node referent, String propertyName, Node referenced, LinkType linkType) throws RepositoryException;

  Node getReferenced(Node referent, String propertyName, LinkType linkType) throws RepositoryException;

  Iterator<Node> getReferents(Node referenced, String propertyName, LinkType linkType) throws RepositoryException;

  Session getSession();

  void close();

  boolean isClosed();

  Query createQuery(String statement) throws RepositoryException;

  QueryResult executeQuery(Query query, Long offset, Long limit) throws RepositoryException;

  int hits(QueryResult result) throws RepositoryException;
}
