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

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface SessionWrapper {

  NodeType getNodeType(String nodeTypeName) throws RepositoryException;

  Node addNode(Node parentNode, String name, NodeDef nodeDef) throws RepositoryException;

  void orderBefore(Node parentNode, Node srcNode, Node dstNode) throws RepositoryException;

  Node getParent(Node childNode) throws RepositoryException;

  Iterator<Node> getChildren(Node parentNode) throws RepositoryException;

  Node getChild(Node parentNode, String name) throws RepositoryException;

  Node getNodeByUUID(String uuid) throws RepositoryException;

  Iterator<String> remove(Node node) throws RepositoryException;

  void save() throws RepositoryException;

  Node setReferenced(Node referent, String propertyName, Node referenced, LinkType linkType) throws RepositoryException;

  Node getReferenced(Node referent, String propertyName, LinkType linkType) throws RepositoryException;

  Iterator<Node> getReferents(Node referenced, String propertyName, LinkType linkType) throws RepositoryException;

  Session getSession();

  void close();

}
