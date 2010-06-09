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
package org.chromattic.test.property;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.api.ChromatticSessionImpl;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ResidualPropertyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_TypedResidual.class);
    addClass(TP_UndefinedResidual.class);
  }

  /** . */
  private TP_TypedResidual o;

  /** . */
  private Node node;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    node = rootNode.addNode("tp_typedresidual", getNodeTypeName(TP_TypedResidual.class));
    o = session.findByNode(TP_TypedResidual.class, node);
  }

  public void testString() throws Exception {
    assertNull(o.getString());
    o.setString("foo");
    assertEquals("foo", o.getString());
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, o.getString());
    }
  }

  public void testStringKey() throws Exception {
    assertNull(o.getString());
    Map<String, Object> map = o.getProperties();
    map.put("string_property", "foo");
    assertEquals("foo", map.get("string_property"));
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, map.get("string_property"));
    }
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

  public void testUndefinedResidualPropertyForString() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node node = rootNode.addNode("tp_undefinedresidual", getNodeTypeName(TP_UndefinedResidual.class));
    TP_UndefinedResidual o = session.findByNode(TP_UndefinedResidual.class, node);
    assertNull(o.getString());
    o.setString("foo");
    assertEquals("foo", o.getString());
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, o.getString());
    }
  }

  public void testUndefinedResidualPropertyForMap() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node node = rootNode.addNode("tp_undefinedresidual", getNodeTypeName(TP_UndefinedResidual.class));
    TP_UndefinedResidual o = session.findByNode(TP_UndefinedResidual.class, node);
    assertNull(o.getString());
    Map<String, Object> map = o.getProperties();
    map.put("string_property", "foo");
    assertEquals("foo", map.get("string_property"));
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, map.get("string_property"));
    }
  }
}