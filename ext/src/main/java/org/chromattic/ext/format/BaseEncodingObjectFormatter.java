/******************************************************************************
 * JBoss by Red Hat                                               *
 * Copyright 2010, Red Hat Middleware, LLC, and individual                 *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 *****************************************************************************/

package org.chromattic.ext.format;

import org.chromattic.api.format.FormatterContext;
import org.chromattic.api.format.ObjectFormatter;

/**
 * Encode a JCR Node name to be valid. Can be prefixed.
 *
 * @author <a href="mailto:theute@redhat.com">Thomas Heute</a>
 * @version $Revision$
 */
public class BaseEncodingObjectFormatter implements ObjectFormatter {

  private static boolean isSpecialChar(char c) {
    return getCode(c) != null;
  }

  private static String getCode(char c) {
    if (c == 0x9
      || c == 0xA
      || c == 0xD
      || (c >= 0x20 && c <= 0xD7FF)
      || (c >= 0xE000 && c <= 0xFFFD)
      || (c >= 0x10000 && c <= 0x10FFFF)) {
      switch (c) {
        case '{':
          return "00";
        case '}':
          return "01";
        case '.':
          return "02";
        case '/':
          return "03";
        case ':':
          return "04";
        case '[':
          return "05";
        case ']':
          return "06";
        case '|':
          return "07";
        case '*':
          return "08";
        case '%':
          return "09";
        default:
          return null;
      }
    }
    else {
      throw new UnsupportedOperationException();
    }
  }

  private static final char[] table = new char[]{
    '{', '}', '.', '/', ':', '[', ']', '|', '*', '%'
  };

  private String decode(String s, int from) {
    StringBuffer buffer = new StringBuffer(s.length());
    buffer.append(s, 0, from);
    int to = s.length();
    while (from < to) {
      char c = s.charAt(from++);
      if (c == '%') {
        if (from + 1 >= to) {
          throw new IllegalStateException("Cannot decode wrong name " + s);
        }
        char c1 = s.charAt(from++);
        if (c1 != '0') {
          throw new IllegalStateException("Cannot decode wrong name " + s);
        }
        char c2 = s.charAt(from++);
        if (c2 < '0' || c2 > '9') {
          throw new IllegalStateException("Cannot decode wrong name " + s);
        }
        buffer.append(table[c2 - '0']);
      }
      else {
        buffer.append(c);
      }
    }
    return buffer.toString();
  }

  public String decodeNodeName(FormatterContext context, String internalName) {
    int length = internalName.length();
    for (int i = 0; i < length; i++) {
      char c = internalName.charAt(i);
      if (c == '%') {
        return decode(internalName, i);
      }
    }
    return internalName;
  }

  private String encode(String s, int from) {
    StringBuffer buffer = new StringBuffer((s.length() * 5) >> 2);
    buffer.append(s, 0, from);
    int to = s.length();
    while (from < to) {
      char c = s.charAt(from++);
      String code = getCode(c);
      if (code != null) {
        buffer.append('%');
        buffer.append(code);
      }
      else {
        buffer.append(c);
      }
    }
    return buffer.toString();
  }

  public String encodeNodeName(FormatterContext context, String externalName) {
    int length = externalName.length();

    //
    for (int i = 0; i < length; i++) {
      char c = externalName.charAt(i);
      if (isSpecialChar(c)) {
        externalName = encode(externalName, i);
        break;
      }
    }

    //
    return externalName;
  }

}