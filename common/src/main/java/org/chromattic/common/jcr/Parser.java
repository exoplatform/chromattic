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

package org.chromattic.common.jcr;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class Parser {

  static void parsePath(PathVisitor visitor, String path, int start, int end) throws PathException {
    int length = end - start;
    if (length > 0) {
      char c = path.charAt(start);
      if (c == '/') {
        if (start + 1 < end) {
          parseRelativePath(visitor, path, start + 1, end);
        }
      } else if (c == '[') {
        throw new UnsupportedOperationException("todo");
      } else {
        parseRelativePath(visitor, path, start, end);
      }
    } else {
      parseRelativePath(visitor, path, start, end);
    }
  }

  static void parseAbsolutePath(PathVisitor visitor, String path, int start, int end) throws PathException {
    int length = end - start;
    if (length == 0) {
      throw new PathException("Invalid absolute empty path");
    }
    char c = path.charAt(start);
    if (c == '/') {
      if (start + 1 < end) {
        parseRelativePath(visitor, path, start + 1, end);
      }
    } else {
      throw new PathException("Invalid absolute path" + path.substring(start, end));
    }
  }

  static void parseRelativePath(PathVisitor visitor, String s, int start, int end) throws PathException {
    if (start == end) {
      parsePathSegment(visitor, s, start, end);
    } else {
      int pos = lastIndexOf(s, '/', start, end);
      if (pos == start) {
        throw new PathException("Cannot parse absolute path" + s.substring(start, end));
      } else if (pos == end - 1) {
        parseRelativePath(visitor, s, start, end - 1);
      } else if (pos == -1) {
        if (s.charAt(end -1) == '/') {
          parsePathSegment(visitor, s, start, end - 1);
        } else {
          parsePathSegment(visitor, s, start, end);
        }
      } else {
        parseRelativePath(visitor, s, start, pos);
        if (s.charAt(end -1) == '/') {
          parsePathSegment(visitor, s, pos + 1, end - 1);
        } else {
          parsePathSegment(visitor, s, pos + 1, end);
        }
      }
    }
  }

  static void parsePathSegment(PathVisitor visitor, String s, int start, int end) throws PathException {
    int length = end - start;
    if (length == 1) {
      if (s.charAt(start) == '.') {
        visitor.onSelf();
        return;
      }
    } else if (length == 2) {
      if (s.charAt(start) == '.' && s.charAt(start + 1) == '.') {
        visitor.onParent();
        return;
      }
    }
    int pos = indexOf(s, '[', start, end);
    if (pos != -1) {
      if (pos == end -1) {
        throw new PathException("Malformed expression " + s.substring(start, end));
      }
      if (s.charAt(end - 1) != ']') {
        throw new PathException("Missing ending ] in expression " + s.substring(start, end));
      }
      String number = s.substring(pos + 1, end - 1);
      Integer numberValue;
      try {
        numberValue = Integer.parseInt(number);
        if (numberValue < 0) {
          throw new PathException("No negative index allowed in expression " + s.substring(start, end));
        }
      }
      catch (NumberFormatException e) {
        throw new PathException("Invalid index in expression " + s.substring(start, end));
      }
      parseName(visitor, s, start, pos, numberValue);
    } else {
      parseName(visitor, s, start, end, null);
    }
  }

  private static void parseName(PathVisitor visitor, String s, int start, int end, Integer number) throws PathException {
    /*
    PathSegment ::= ExpandedName [Index] | QualifiedName [Index] | SelfOrParent
    Index ::= '[' Number ']'
    Number ::= An integer > 0
    ExpandedName ::= '{' Namespace '}' LocalName
    Namespace ::= EmptyString | Uri
    Uri ::= A URI, as defined in Section 3 in http://tools.ietf.org/html/rfc3986#section-3
    QualifiedName ::= [ Prefix ':' ] LocalName
    Prefix ::= Any string that matches the NCName production in  http://www.w3.org/TR/REC-xml-names
    LocalName ::= ValidString SelfOrParent
    ValidString ::= XmlChar InvalidChar
    InvalidChar ::= '/' | ':' | '[' | ']' | '|' | '*'
    XmlChar ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    XmlChar ::= Any character that matches the Char production at http://www.w3.org/TR/xml/#NT-Char
    SelfOrParent ::= '.' | '..'
    */

    //
    int length = end - start;
    if (length > 0 &&s.charAt(start) == '{') {
      int curlyBraceIndex = indexOf(s, '}', start + 1, end);
      if (curlyBraceIndex == -1) {
        throw new PathException("Uri not closed in name value " + s.substring(start, end));
      }
      // Should validate URI ...
      validateLocalName(s, curlyBraceIndex + 1, end);
      visitor.onURIPathSegment(s, start + 1, curlyBraceIndex, curlyBraceIndex + 1, end, number);
    } else {
      // Maybe there is an optional prefix
      int colonIndex = indexOf(s, ':', start, end);
      if (colonIndex != -1) {
        String prefix = s.substring(start, colonIndex);
        // Should validate prefix
        validateLocalName(s, colonIndex + 1, end);
        visitor.onPrefixPathSegment(s, start, colonIndex, colonIndex + 1, end, number);
      } else {
        validateLocalName(s, start, end);
        visitor.onPathSegment(s, start, end, number);
      }
    }
  }

  private static void validateLocalName(String s, int start, int end) throws PathException {
    int length = end - start;

    // Now validate as a name
    if (length - start == 1) {
      if (s.charAt(start) == '.') {
        throw new PathException("'.' is not a valid name");
      }
    } else if (length - start == 2) {
      if (s.charAt(start) == '.' && s.charAt(start + 1) == '.') {
        throw new PathException("'..' is not a valid name");
      }
    }

    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    while (start < end) {
      char c = s.charAt(start);
      if (c == 0x9
        || c == 0xA
        ||  c == 0xD
        || (c >= 0x20 && c <= 0xD7FF)
        || (c >= 0xE000 && c <= 0xFFFD)
        || (c >= 0x10000 && c <= 0x10FFFF)) {
        if (c == '/' || c == ':' || c == '[' || c == ']' || c == '|' || c == '*') {
          throw new PathException("Illegal path value " + s.substring(start, end) + "  (char " + c + " at position " + start + " not accepted)");
        }
        start++;
        continue;
      }
      throw new PathException("Illegal path value " + s.substring(start, end) + "  (char " + c + " at position " + start + " not accepted)");
    }
  }

  private static int indexOf(String s, char c, int start, int end) {
    while (start < end) {
      if (s.charAt(start) == c) {
        return start;
      }
      start++;
    }
    return -1;
  }

  private static int lastIndexOf(String s, char c, int start, int end) {
    while (start < end) {
      int next = end - 1;
      if (s.charAt(next) == c) {
        return next;
      }
      end = next;
    }
    return -1;
  }
}
