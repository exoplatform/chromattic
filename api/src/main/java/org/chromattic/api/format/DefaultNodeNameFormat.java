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

package org.chromattic.api.format;

/**
 * Defines the default codec format that only performs validation of JCR names.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DefaultNodeNameFormat implements CodecFormat<String, String> {

  /** . */
  private static final DefaultNodeNameFormat INSTANCE = new DefaultNodeNameFormat();

  public static DefaultNodeNameFormat getInstance() {
    return INSTANCE;
  }

  public static void validateName(String name) {
    INSTANCE.encode(name);
  }

  protected DefaultNodeNameFormat() {
  }

  public String encode(String external) {
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

    if (external == null) {
      throw new NullNameException("No null name accepted");
    }

    //
    if (external.length() == 0) {
      throw new NameSyntaxException("No empty name accepted");
    }

    //
    int index = 0;
    if (external.charAt(0) == '{') {
      int curlyBraceIndex = external.indexOf('}');
      if (curlyBraceIndex == -1) {
        throw new NameSyntaxException("Uri not closed in name value " + external);
      }
      String uri = external.substring(1, curlyBraceIndex);
      // validate URI ...
      index = curlyBraceIndex + 1;
    } else {
      // Maybe there is an optional prefix
      int colonIndex = external.indexOf(':');
      if (colonIndex != -1) {
        String prefix = external.substring(0, colonIndex);
        // Validate prefix
        index = colonIndex + 1;
      }
    }

    // Now validate as a name
    int length = external.length();
    if (length - index == 1) {
      if (external.charAt(index) == '.') {
        throw new NameSyntaxException();
      }
    } else if (length - index == 2) {
      if (external.charAt(index) == '.' && external.charAt(index + 1) == '.') {
        throw new NameSyntaxException();
      }
    }

    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    while (index < length) {
      char c = external.charAt(index++);
      if (c == 0x9
        || c == 0xA
        ||  c == 0xD
        || (c >= 0x20 && c <= 0xD7FF)
        || (c >= 0xE000 && c <= 0xFFFD)
        || (c >= 0x10000 && c <= 0x10FFFF)) {
        if (c == '/' || c == ':' || c == '[' || c == ']' || c == '|' || c == '*') {
          throw new NameSyntaxException("Illegal path value " + external + "  (char " + c + " at position " + index + " not accepted)");
        }
        continue;
      }
      throw new NameSyntaxException("Illegal path value " + external + "  (char " + c + " at position " + index + " not accepted)");
    }

    //
    return external;
  }

  public String decode(String internal) {
    return internal;
  }
}
