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

package org.chromattic.test.common;

import junit.framework.TestCase;
import org.chromattic.common.PathParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PathTestCase extends TestCase {

  public static class PrefixedName {

    /** . */
    private String prefix;

    /** . */
    private String name;

    public PrefixedName(String prefix, String name) {
      if (prefix == null) {
        throw new IllegalArgumentException();
      }
      if (name == null) {
        throw new IllegalArgumentException();
      }
      this.prefix = prefix;
      this.name = name;
    }


    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof PrefixedName) {
        PrefixedName that = (PrefixedName)obj;
        return that.prefix.equals(prefix) && that.name.equals(name);
      }
      return false;
    }
  }

  public static class ItemList {

    /** . */
    public static final Object DOT = new Object();

    /** . */
    public static final Object DOT_DOT = new Object();

    public static Item newAtom(String prefix, String name, boolean last) {
      return new Item(new PrefixedName(prefix, name), last);
    }

    public static Item newAtom(String name, boolean last) {
      return new Item(new PrefixedName("", name), last);
    }

    public static Item newDot(boolean last) {
      return new Item(DOT, last);
    }

    public static Item newDotDot(boolean last) {
      return new Item(DOT_DOT, last);
    }

    private static class Item {

      /** . */
      private final Object value;

      /** . */
      private final boolean last;

      private Item(Object value, boolean last) {
        this.value = value;
        this.last = last;
      }

      public boolean equals(Object obj) {
        if (obj == this) {
          return true;
        }
        if (obj instanceof Item) {
          Item that = (Item)obj;
          return last == that.last && value.equals(that.value);
        }
        return false;
      }
    }

    /** . */

    private final boolean absolute;

    /** . */
    private final List<Item> items = new ArrayList<Item>();

    public ItemList(boolean absolute) {
      this.absolute = absolute;
    }

    public ItemList appendAtom(String prefix, String name) {
      return appendAtom(prefix, name, false);
    }

    public ItemList appendAtom(String name, boolean last) {
      return appendAtom("", name, last);
    }

    public ItemList appendAtom(String name) {
      return appendAtom(name, false);
    }

    public ItemList appendAtom(String prefix, String name, boolean last) {
      items.add(newAtom(prefix, name, last));
      return this;
    }

    public ItemList appendDot() {
      return appendDot(false);
    }

    public ItemList appendDot(boolean last) {
      items.add(newDot(last));
      return this;
    }

    public ItemList appendDotDot() {
      return appendDotDot(false);
    }

    public ItemList appendDotDot(boolean last) {
      items.add(newDotDot(last));
      return this;
    }

    public String toString() {
      return (absolute ? "absolute" : "relative") + items;
    }
  }

  public static class VisitorImpl implements PathParser.Visitor {
    private ItemList list;

    public void onStart(boolean absolute) {
      list = new ItemList(absolute);
    }

    public void onAtomElement(String s, int prefixPos, int prefixLen, int namePos, int nameLen, boolean last) {
      String prefix = s.substring(prefixPos, prefixPos + prefixLen);
      String name = s.substring(namePos, namePos + nameLen);
      list.appendAtom(prefix, name, last);
    }

    public void onDotElement(boolean last) {
      list.appendDot(last);
    }

    public void onDotDotElement(boolean last) {
      list.appendDotDot(last);
    }
  }

  public void check(String jcrPath, ItemList list) throws Exception {
    VisitorImpl visitor = new VisitorImpl();
    assertTrue(PathParser.parseJCRPath(jcrPath, visitor));
    assertEquals(visitor.list.absolute, list.absolute);
    assertEquals(visitor.list.items, list.items);
  }

  public void assertFail(String jcrPath) {
    VisitorImpl visitor = new VisitorImpl();
    assertFalse("Was expecting a failure instead got " + visitor.list, PathParser.parseJCRPath(jcrPath, visitor));
  }

  public void test1() throws Exception {
    check("/", new ItemList(true));
    check("a", new ItemList(false).appendAtom("a", true));
    check(".", new ItemList(false).appendDot(true));
    assertFail(":");
    assertFail("[");
    assertFail("]");
    assertFail("'");
    assertFail("\"");
    assertFail("|");

    check("..", new ItemList(false).appendDotDot(true));
    check(".a", new ItemList(false).appendAtom(".a", true));
    check("a.", new ItemList(false).appendAtom("a.", true));
    check("ab", new ItemList(false).appendAtom("ab", true));
    check("/a", new ItemList(true).appendAtom("a", true));
    check("/.", new ItemList(true).appendDot(true));
    assertFail("//");
    assertFail("/:");
    assertFail("/[");
    assertFail("/]");
    assertFail("/*");
    assertFail("/'");
    assertFail("/'");
    assertFail("/\"");
    assertFail("/|");
    assertFail("./");
    assertFail(".:");
    assertFail(".[");
    assertFail(".]");
    assertFail(".*");
    assertFail(".'");
    assertFail(".\"");
    assertFail(".|");
    assertFail(":a");
    assertFail("a:");

    check("./.", new ItemList(false).appendDot().appendDot(true));
    check("./a", new ItemList(false).appendDot().appendAtom("a", true));
    check("a/.", new ItemList(false).appendAtom("a").appendDot(true));
    check("aaa", new ItemList(false).appendAtom("aaa", true));
    check("a:a", new ItemList(false).appendAtom("a", "a", true));

    check("../..", new ItemList(false).appendDotDot().appendDotDot(true));
    check("../.", new ItemList(false).appendDotDot().appendDot(true));
    check("./..", new ItemList(false).appendDot().appendDotDot(true));
    check("../a", new ItemList(false).appendDotDot().appendAtom("a", true));
    check("a/..", new ItemList(false).appendAtom("a").appendDotDot(true));

    check("/toto/titi", new ItemList(true).appendAtom("toto").appendAtom("titi", true));
  }
}
