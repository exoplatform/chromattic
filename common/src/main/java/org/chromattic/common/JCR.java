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

  public static void validateName(String name) {
    /*
    PathSegment ::= ExpandedName [Index] | QualifiedName [Index] | SelfOrParent
    Index ::= '[' Number ']'
    Number ::= An integer > 0
    ExpandedName ::= '{' Namespace '}' LocalName
    Namespace ::= EmptyString | Uri
    Uri ::= A URI, as defined in Section 3 in http://tools.ietf.org/html/rfc3986#section-3
    QualifiedName ::= [ Prefix ':' ] LocalName
    Prefix ::= Any string that matches the NCName production in  http://www.w3.org/TR/REC-xml-names
    LocalName ::= ValidString Ð SelfOrParent
    ValidString ::= XmlChar Ð InvalidChar
    InvalidChar ::= '/' | ':' | '[' | ']' | '|' | '*'
    XmlChar ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    XmlChar ::= Any character that matches the Char production at http://www.w3.org/TR/xml/#NT-Char
    SelfOrParent ::= '.' | '..'
    */

    if (name == null) {
      throw new NullPointerException();
    }

    //
    if (name.length() == 0) {
      throw new IllegalArgumentException();
    }

    //
    int index = 0;
    if (name.charAt(0) == '{') {
      int curlyBraceIndex = name.indexOf('}');
      if (curlyBraceIndex == -1) {
        throw new IllegalArgumentException("Uri not closed in name " + name);
      }
      String uri = name.substring(1, curlyBraceIndex);
      // validate URI ...
      index = curlyBraceIndex + 1;
    } else {
      // Maybe there is an optional prefix
      int colonIndex = name.indexOf(':');
      if (colonIndex != -1) {
        String prefix = name.substring(0, colonIndex);
        // Validate prefix
        index = colonIndex + 1;
      }
    }

    // Now validate as a name
    int length = name.length();
    if (length - index == 1) {
      if (name.charAt(index) == '.') {
        throw new IllegalArgumentException();
      }
    } else if (length - index == 2) {
      if (name.charAt(index) == '.' && name.charAt(index + 1) == '.') {
        throw new IllegalArgumentException();
      }
    }

    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    while (index < length) {
      char c = name.charAt(index++);
      if (c == 0x9
        || c == 0xA
        ||  c == 0xD
        || (c >= 0x20 && c <= 0xD7FF)
        || (c >= 0xE000 && c <= 0xFFFD)
        || (c >= 0x10000 && c <= 0x10FFFF)) {
        if (c == '/' || c == ':' || c == '[' || c == ']' || c == '|' || c == '*') {
          throw new IllegalArgumentException("Char " + c + " at position " + index + "not accepted");
        }
        continue;
      }
      throw new IllegalArgumentException("Char " + c + " at position " + index + "not accepted");
    }
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

  public static PropertyDefinition findPropertyDefinition(Node node, String propertyName) throws RepositoryException {
    PropertyDefinition def = getPropertyDefinition(node, propertyName);
    if (def == null) {
      return getPropertyDefinition(node, "*");
    }
    return def;
  }
}
