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
  public String decodeNodeName(FormatterContext context, String internalName) {
    internalName = _unescapeIllegalJcrChars(internalName);
    return internalName;
  }

 /**
  * Escapes all illegal JCR 1.0 name characters of a string.
  * <p>
  * QName EBNF:<br>
  * <xmp>
  * simplename ::= onecharsimplename | twocharsimplename | threeormorecharname
  * onecharsimplename ::= (* Any Unicode character except: '.', '/', ':', '[', ']', '*', ''', '"', '|' or any whitespace character *)
  * twocharsimplename ::= '.' onecharsimplename | onecharsimplename '.' | onecharsimplename onecharsimplename
  * threeormorecharname ::= nonspace string nonspace
  * string ::= char | string char
  * char ::= nonspace | ' '
  * nonspace ::= (* Any Unicode character except: '/', ':', '[', ']', '*', ''', '"', '|' or any whitespace character *)
  * </xmp>
  */
  public String encodeNodeName(FormatterContext context, String externalName) {
    externalName = _escapeIllegalJCRChars(externalName);
    return externalName;
  }


  private String _escapeIllegalJCRChars(String name) {
    String illegal = "%/:[]*'\"|\t\r\n";
    StringBuffer buffer = new StringBuffer(name.length() * 2);
    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (illegal.indexOf(ch) != -1 
        || (ch == '.' && name.length() < 3)
        || (ch == ' ' && (i == 0 || i == name.length() - 1))) {
        buffer.append('%');
        buffer.append(Character.toUpperCase(Character.forDigit(ch / 16, 16)));
        buffer.append(Character.toUpperCase(Character.forDigit(ch % 16, 16)));
      }
      else {
        buffer.append(ch);
      }
    }
    return buffer.toString();
  }

  private String _unescapeIllegalJcrChars(String name) {
    StringBuilder buffer = new StringBuilder(name.length());
    int i = name.indexOf('%');
    while (i > -1 && i + 2 < name.length()) {
      buffer.append(name.toCharArray(), 0, i);
      int a = Character.digit(name.charAt(i + 1), 16);
      int b = Character.digit(name.charAt(i + 2), 16);
      if (a > -1 && b > -1) {
        buffer.append((char)(a * 16 + b));
        name = name.substring(i + 3);
      }
      else {
        buffer.append('%');
        name = name.substring(i + 1);
      }
      i = name.indexOf('%');
    }
    buffer.append(name);
    return buffer.toString();
  }
}