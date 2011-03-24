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

package org.chromattic.common;

import javax.jcr.PropertyIterator;
import javax.jcr.Property;
import javax.jcr.NodeIterator;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.NodeType;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCR {

  @SuppressWarnings("unchecked")
  public static Iterator<Property> adapt(final PropertyIterator iterator) {
    return (Iterator<Property>)iterator;
  }

  @SuppressWarnings("unchecked")
  public static Iterator<Node> adapt(final NodeIterator iterator) {
    return (Iterator<Node>)iterator;
  }

  /**
   * Return true if the two nodes are equals with the meaning:
   * <ul>
   *   <li>two null nodes are equals</li>
   *   <li>two non null nodes are equals if
   *     <li>the are originated from the same session</li>
   *     <li>they have the same id when they are referenceable otherwise they have the same path</li>
   *   </li>
   * </ul>
   *
   * @param a the node a
   * @param b the node b
   * @return true if the two nodes are equals
   * @throws RepositoryException any repository exception
   */
  public static boolean equals(Node a, Node b) throws RepositoryException {
    boolean equals;
    if (a == b) {
      equals = true;
    } else if (a == null || b == null) {
      return false;
    } else {
      if (a.getSession() == b.getSession()) {
        try {
          String aId = a.getUUID();
          String bId = b.getUUID();
          equals = aId.equals(bId);
        }
        catch (UnsupportedRepositoryOperationException e) {
          // Compare path
          String aPath = a.getPath();
          String bPath = b.getPath();
          equals = aPath.equals(bPath);
        }
      } else {
        equals = false;
      }
    }
    return equals;
  }

  public static boolean hasMixin(Node node, String mixinTypeName) throws RepositoryException {
    if (node == null) {
      throw new NullPointerException();
    }
    if (mixinTypeName == null) {
      throw new NullPointerException();
    }
    for (NodeType nodeType : node.getMixinNodeTypes()) {
      if (nodeType.getName().equals(mixinTypeName)) {
        return true;
      }
    }
    return false;
  }

  public static PropertyDefinition getPropertyDefinition(NodeType nodeType, String propertyName) throws RepositoryException {
    for (PropertyDefinition def : nodeType.getPropertyDefinitions()) {
      if (def.getName().equals(propertyName)) {
        return def;
      }
    }
    return null;
  }

  public static PropertyDefinition getPropertyDefinition(Node node, String propertyName) throws RepositoryException {
    if (node.hasProperty(propertyName)) {
      return node.getProperty(propertyName).getDefinition();
    } else {
      NodeType primaryNodeType = node.getPrimaryNodeType();
      PropertyDefinition def = getPropertyDefinition(primaryNodeType, propertyName);
      if (def == null) {
        for (NodeType mixinNodeType : node.getMixinNodeTypes()) {
          def = getPropertyDefinition(mixinNodeType, propertyName);
          if (def != null) {
            break;
          }
        }
      }
      return def;
    }
  }

  public static String qualify(String prefix, String localName) {
    if (localName == null) {
      return null;
    } else {
      if (prefix != null && prefix.length() > 0) {
        return prefix + ':' + localName;
      } else {
        return localName;
      }
    }
  }
}
