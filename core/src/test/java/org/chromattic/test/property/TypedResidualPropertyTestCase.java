/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.chromattic.test.property;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypedResidualPropertyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_TypedResidual.class);
  }

  /** . */
  private TP_TypedResidual o;

  /** . */
  private Node node;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    node = rootNode.addNode("tp_typedresidual", "tp_e");
    o = session.findByNode(TP_TypedResidual.class, node);
  }

  public void testString() throws Exception {
    assertNull(o.getString());
    o.setString("foo");
    assertEquals("foo", o.getString());
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    node.setProperty("string_property", (String)null);
    assertEquals(null, o.getString());
  }

  public void testStringKey() throws Exception {
    assertNull(o.getString());
    Map<String, Object> map = o.getProperties();
    map.put("string_property", "foo");
    assertEquals("foo", map.get("string_property"));
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    node.setProperty("string_property", (String)null);
    assertEquals(null, map.get("string_property"));
  }

  public void testInteger() throws Exception {
    o.getInteger();
    try {
      o.setInteger(5);
      fail();
    }
    catch (ClassCastException ignore) {
    }
    node.setProperty("integer_property", "foo");
    try {
      o.getInteger();
      fail();
    }
    catch (ClassCastException ignore) {
    }
  }

  public void testIntegerKey() throws Exception {
    Map<String, Object> map = o.getProperties();
    try {
      map.put("integer_property", 5);
      fail();
    }
    catch (ClassCastException ignore) {
    }
    assertFalse(node.hasProperty("integer_property"));
  }
}