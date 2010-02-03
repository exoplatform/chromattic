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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PathParser {

  // todo - namespace prefix validation - indexing in the path element production rule
  public static boolean parseJCRPath(String jcrPath, Visitor visitor) throws IllegalArgumentException {
    if (jcrPath == null) {
      throw new NullPointerException();
    }

    //
    int length = jcrPath.length();
    if (length == 0) {
      return false;
    }

    //
    boolean absolute = jcrPath.charAt(0) == '/';
    visitor.onStart(absolute);
    if (absolute && length == 1) {
      return true;
    }

    //
    if (!parseJCRRelPath(jcrPath, absolute ? 1 : 0, jcrPath.length(), visitor, true)) {
      return false;
    }

    //
    return true;
  }

  private static boolean parseJCRRelPath(String jcrpath, int start, int end, Visitor visitor, boolean top) {
    int len = end - start;
    if (len <= 0) {
      return false;
    }
    if (len <= 2) {
      if (!parseJCRPathElement(jcrpath, start, end, visitor, top)) {
        return false;
      }
    }
    else {
      int slashPos = jcrpath.lastIndexOf('/', end - 1);
      if (slashPos < start) {
        if (!parseJCRPathElement(jcrpath, start, end, visitor, top)) {
          return false;
        }
      }
      else {
        if (!parseJCRRelPath(jcrpath, start, slashPos, visitor, false)) {
          return false;
        }
        if (!parseJCRPathElement(jcrpath, slashPos + 1, end, visitor, top)) {
          return false;
        }
      }
    }

    //
    return true;
  }

  private static boolean parseJCRPathElement(String jcrpath, int start, int end, Visitor visitor, boolean last) {
    int len = end - start;
    if (len <= 0) {
      return false;
    }
    
    //
    if (len == 1) {
      char c = jcrpath.charAt(start);
      if (c == '.') {
        visitor.onDotElement(last);
      }
      else if (isJCROneCharSimpleName(c)) {
        visitor.onAtomElement(jcrpath, start, 0, start, 1, last);
      }
      else {
        return false;
      }
    }
    else if (len == 2) {
      char c1 = jcrpath.charAt(start);
      char c2 = jcrpath.charAt(start + 1);
      if (c1 == '.') {
        if (c2 == '.') {
          visitor.onDotDotElement(last);
        }
        else if (isJCROneCharSimpleName(c2)) {
          visitor.onAtomElement(jcrpath, start, 0, start, 2, last);
        }
        else {
          return false;
        }
      }
      else if (isJCROneCharSimpleName(c1)) {
        if (c2 == '.' || isJCROneCharSimpleName(c2)) {
          visitor.onAtomElement(jcrpath, start, 0, start, 2, last);
        }
        else {
          return false;
        }
      }
      else {
        return false;
      }
    }
    else {
      int prefixPos = start;
      int prefixLen = 0;
      int colonPos = jcrpath.indexOf(':', start + 1);
      if (colonPos != -1) {
        prefixLen = colonPos - prefixPos;
        start = colonPos + 1;
        len = end - start;
        // todo : validate that the prefix is Any valid non-empty XML NCName
      }

      //
      char c1 = jcrpath.charAt(start);
      if (isJCRNonSpace(c1) == false) {
        return false;
      }
      char c2 = jcrpath.charAt(end - 1);
      if (isJCRNonSpace(c2) == false) {
        return false;
      }
      for (int i = start; i < end - 2; i++) {
        char c = jcrpath.charAt(i);
        if (isJCRNonSpace(c) == false && c != ' ') {
          return false;
        }
      }

      //
      visitor.onAtomElement(jcrpath, prefixPos, prefixLen, start, len, last);
    }

    //
    return true;
  }

  private static boolean isJCROneCharSimpleName(char c) {
    switch (c) {
      case '.':
      case '/':
      case ':':
      case '[':
      case ']':
      case '*':
      case '\'':
      case '"':
      case '|':
        return false;
      default:
        return Character.isWhitespace(c) == false;
    }
  }

  private static boolean isJCRNonSpace(char c) {
    switch (c) {
      case '/':
      case ':':
      case '[':
      case ']':
      case '*':
      case '\'':
      case '"':
      case '|':
        return false;
      default:
        return Character.isWhitespace(c) == false;
    }
  }

  public interface Visitor {

    void onStart(boolean absolute);

    void onAtomElement(String s, int prefixPos, int prefixLen, int namePos, int nameLen, boolean last);

    void onDotElement(boolean last);

    void onDotDotElement(boolean last);
  }
}
