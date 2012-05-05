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
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class, B.class})
public class PropertiesTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
  }

  /** . */
  private ChromatticSessionImpl session;

  /** . */
  private A a;

  /** . */
  private Node aNode;

  /** . */
  private B b;

  /** . */
  private Node bNode;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    session = login();
    a = session.insert(A.class, "a");
    aNode = session.getNode(a);
    b = session.insert(B.class, "b");
    bNode = session.getNode(b);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    //
    a = null;
    session.close();
    session = null;
  }

  public void testGetString() throws Exception {
    aNode.setProperty("string_property", "bar");
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.get("string_property");
    assertEquals("bar", value);
  }

  public void testPutString() throws Exception {
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.put("string_property", "bar");
    assertEquals(null, value);
    assertEquals("bar", aNode.getProperty("string_property").getString());
  }

  public void testRemoveString() throws Exception {
    aNode.setProperty("string_property", "bar");
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.remove("string_property");
    assertEquals("bar", value);
    assertFalse(aNode.hasProperty("string_property"));
  }

  public void testGetLong() throws Exception {
    aNode.setProperty("long_property", 3L);
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.get("long_property");
    assertEquals(3L, value);
  }

  public void testPutLong() throws Exception {
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.put("long_property", 3L);
    assertEquals(null, value);
    assertEquals(3L, (int) aNode.getProperty("long_property").getLong());
  }

  public void testRemoveLong() throws Exception {
    aNode.setProperty("long_property", 3L);
    Map<String, Object> properties = a.getAnyProperties();
    Object value = properties.remove("long_property");
    assertEquals(3L, value);
    assertFalse(aNode.hasProperty("long_property"));
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
    Map<String, Object> properties = a.getAnyProperties();
    try {
      properties.get("/invalid");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testRemoveInvalidKey() throws Exception {
    Map<String, Object> properties = a.getAnyProperties();
    try {
      properties.remove("/invalid");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testPutInvalidKey() throws Exception {
    Map<String, Object> properties = a.getAnyProperties();
    try {
      properties.put("/invalid", "foo");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testGetMultivaluedValue() throws Exception {
    bNode.setProperty("string_array_property", new String[]{"a","b"});
    Map<String, Object> copy = new HashMap<String, Object>();
    for (Map.Entry<String, Object> entry : b.getAnyProperties().entrySet()) {
      copy.put(entry.getKey(), entry.getValue());
    }
    assertTrue(copy.containsKey("string_array_property"));
    assertEquals("a", copy.get("string_array_property"));
  }

  public void testFoo1() throws Exception {
    aNode.setProperty("string_array_property", "a");
    Map<String, List<Object>> props = a.getAnyMultiProperties();
    List<Object> val = props.get("string_array_property");
    assertEquals(Arrays.<Object>asList("a"), val);
    if (getProfile().isStateCacheDisabled()) {
      aNode.setProperty("string_array_property", (String)null);
    } else {
      props.remove("string_array_property");
    }
    val = props.get("string_array_property");
    assertEquals(java.util.Collections.<Object>emptyList(), val);

    //
    props.put("string_array_property", Arrays.<Object>asList("b"));
    assertEquals("b", aNode.getProperty("string_array_property").getString());

    //
    try {
      props.put("string_array_property", Arrays.<Object>asList("b", "c"));
      fail();
    } catch (IllegalArgumentException e) {
    }
    assertEquals("b", aNode.getProperty("string_array_property").getString());
  }

  public void testFoo2() throws Exception {
    bNode.setProperty("string_array_property", new String[]{"a", "b"});
    Map<String, List<Object>> props = b.getAnyMultiProperties();
    List<Object> val = props.get("string_array_property");
    assertEquals(Arrays.<Object>asList("a", "b"), val);
    if (getProfile().isStateCacheDisabled()) {
       bNode.setProperty("string_array_property", new String[0]);
     } else {
       props.put("string_array_property", Arrays.<Object>asList(new String[0]));
     }
    val = props.get("string_array_property");
    assertEquals(java.util.Collections.<Object>emptyList(), val);

    //
    props.put("string_array_property", Arrays.<Object>asList("a", "b", "c"));
    Property p = bNode.getProperty("string_array_property");
    Value[] values = p.getValues();
    assertEquals(3, values.length);
    assertEquals("a", values[0].getString());
    assertEquals("b", values[1].getString());
    assertEquals("c", values[2].getString());
  }
  
  public void testContains() throws Exception {
    aNode.setProperty("property", "bar");
    Map<String, Object> properties = a.getAnyProperties();
    assertEquals(true, properties.containsKey("property"));
    assertEquals(false, properties.containsKey("foo"));
  }

}
