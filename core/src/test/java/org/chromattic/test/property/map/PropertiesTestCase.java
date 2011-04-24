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

package org.chromattic.test.property.map;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertiesTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
  }

  /** . */
  private ChromatticSessionImpl session;

  /** . */
  private A b;

  /** . */
  private Node bNode;

  /** . */
  private B c;

  /** . */
  private Node cNode;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    session = login();
    b = session.insert(A.class, "a");
    bNode = session.getNode(b);
    c = session.insert(B.class, "b");
    cNode = session.getNode(c);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    //
    b = null;
    session.close();
    session = null;
  }

  public void testGetString() throws Exception {
    bNode.setProperty("string_property", "bar");
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.get("string_property");
    assertEquals("bar", value);
  }

  public void testPutString() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.put("string_property", "bar");
    assertEquals(null, value);
    assertEquals("bar", bNode.getProperty("string_property").getString());
  }

  public void testRemoveString() throws Exception {
    bNode.setProperty("string_property", "bar");
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.remove("string_property");
    assertEquals("bar", value);
    assertFalse(bNode.hasProperty("string_property"));
  }

  public void testGetLong() throws Exception {
    bNode.setProperty("long_property", 3L);
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.get("long_property");
    assertEquals(3L, value);
  }

  public void testPutLong() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.put("long_property", 3L);
    assertEquals(null, value);
    assertEquals(3L, (int)bNode.getProperty("long_property").getLong());
  }

  public void testRemoveLong() throws Exception {
    bNode.setProperty("long_property", 3L);
    Map<String, Object> properties = b.getAnyProperties();
    Object value = properties.remove("long_property");
    assertEquals(3L, value);
    assertFalse(bNode.hasProperty("long_property"));
  }

/*
  public void testPutWrongType() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    try {
      properties.put("string_property", 5);
      fail();
    }
    catch (ClassCastException ignore) {
    }
  }
*/

  public void testGetInvalidKey() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    try {
      properties.get("/invalid");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testRemoveInvalidKey() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    try {
      properties.remove("/invalid");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testPutInvalidKey() throws Exception {
    Map<String, Object> properties = b.getAnyProperties();
    try {
      properties.put("/invalid", "foo");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testGetMultivaluedValue() throws Exception {
    cNode.setProperty("string_array_property", new String[]{"a","b"});
    Map<String, Object> copy = new HashMap<String, Object>();
    for (Map.Entry<String, Object> entry : c.getAnyProperties().entrySet()) {
      copy.put(entry.getKey(), entry.getValue());
    }
    assertTrue(copy.containsKey("string_array_property"));
    assertEquals("a", copy.get("string_array_property"));
  }
}
