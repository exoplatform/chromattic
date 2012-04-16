/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.test.property.map;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PrefixedPropertiesTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(C.class);
  }

  /** . */
  private ChromatticSessionImpl session;

  /** . */
  private C c;

  /** . */
  private Node cNode;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    session = login();
    c = session.insert(C.class, "c");
    cNode = session.getNode(c);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    //
    c = null;
    session.close();
    session = null;
  }

  public void testEmpty() throws Exception {
    Map<String, Object> props = c.getProperties();
    assertEquals(0, props.size());
    cNode.setProperty("foo", "bar");
    assertEquals(0, props.size());
  }

  public void testGet() throws Exception {
    Map<String, Object> props = c.getProperties();
    assertNull(props.get("foo"));
    cNode.setProperty("property_map:foo", "foo_value");
    assertEquals("foo_value", props.get("foo"));
  }

  public void testGetThrowsNPE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      props.get(null);
      fail();
    }
    catch (NullPointerException ignore) {
    }
  }

  public void testGetReturnsNull() throws Exception {
    cNode.setProperty("foo", "foo_value");
    Map<String, Object> props = c.getProperties();
    assertNull(props.get("foo"));
  }

  public void testGetThrowsCCE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      props.get(new Object());
      fail();
    } catch (ClassCastException ignore) {
    }
  }

  public void testPut() throws Exception {
    Map<String, Object> props = c.getProperties();
    props.put("property_map:bar", "bar_value");
    assertEquals("bar_value", cNode.getProperty("property_map:bar").getString());
  }

  public void testPutThrowsNPE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      props.put(null, "bar_value");
      fail();
    }
    catch (NullPointerException ignore) {
    }
  }

  public void testPutThrowsCCE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      ((Map)props).put(new Object(), "bar_value");
      fail();
    }
    catch (ClassCastException ignore) {
    }
  }

  public void testRemove() throws Exception {
    cNode.setProperty("property_map:foo", "foo_value");
    Map<String, Object> props = c.getProperties();
    props.remove("bar");
    assertFalse("bar_value", cNode.hasProperty("property_map:bar"));
  }

  public void testRemoveThrowsCCE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      props.remove(new Object());
      fail();
    }
    catch (ClassCastException ignore) {
    }
  }

  public void testRemoveWithInvalidArg() throws Exception {
    cNode.setProperty("foo", "foo_value");
    Map<String, Object> props = c.getProperties();
    props.remove("foo");
    assertEquals("foo_value", cNode.getProperty("foo").getString());
  }

  public void testRemoveThrowsNPE() throws Exception {
    Map<String, Object> props = c.getProperties();
    try {
      props.remove(null);
      fail();
    }
    catch (NullPointerException ignore) {
    }
  }

  public void testContains() throws Exception {
    Map<String, Object> props = c.getProperties();
    assertEquals(false, props.containsKey("foo"));
    cNode.setProperty("property_map:foo", "foo_value");
    assertEquals(true, props.containsKey("foo"));
  }

}
