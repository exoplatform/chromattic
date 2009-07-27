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
import org.chromattic.core.DomainSession;
import org.chromattic.common.IO;

import javax.jcr.Node;
import java.util.Calendar;
import java.util.Date;
import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMappedToSingleValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_A.class);
  }

  /** . */
  private TP_A a;

  /** . */
  private Node aNode;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    aNode = rootNode.addNode("tp_a_a", "tp_a");
    a = session.findByNode(TP_A.class, aNode);
  }

  public void testString() throws Exception {
    assertEquals(null, a.getString());
    aNode.setProperty("string_property", "foo");
    assertEquals("foo", a.getString());
    a.setString("bar");
    assertEquals("bar", aNode.getProperty("string_property").getString());
    a.setString(null);
    assertFalse(aNode.hasProperty("string_property"));
  }

  public void testPrimitiveBoolean() throws Exception {
    try {
      a.getPrimitiveBoolean();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    aNode.setProperty("primitive_boolean_property", true);
    assertEquals(true, a.getPrimitiveBoolean());
    a.setPrimitiveBoolean(false);
    assertEquals(false, aNode.getProperty("primitive_boolean_property").getBoolean());
  }

  public void testPrimitiveInt() throws Exception {
    try {
      a.getPrimitiveInt();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    aNode.setProperty("primitive_int_property", 3);
    assertEquals(3, a.getPrimitiveInt());
    a.setPrimitiveInt(5);
    assertEquals(5, aNode.getProperty("primitive_int_property").getLong());
  }

  public void testPrimitiveLong() throws Exception {
    try {
      a.getPrimitiveLong();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    aNode.setProperty("primitive_long_property", 3);
    assertEquals(3, a.getPrimitiveLong());
    a.setPrimitiveLong(5);
    assertEquals(5, aNode.getProperty("primitive_long_property").getLong());
  }

  public void testPrimitiveDouble() throws Exception {
    try {
      a.getPrimitiveDouble();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    aNode.setProperty("primitive_double_property", 3D);
    assertEquals(3D, a.getPrimitiveDouble());
    a.setPrimitiveDouble(5D);
    assertEquals(5D, aNode.getProperty("primitive_double_property").getDouble());
  }

  public void testBoolean() throws Exception {
    assertNull(a.getBoolean());
    aNode.setProperty("boolean_property", true);
    assertEquals((Boolean)true, a.getBoolean());
    a.setBoolean(false);
    assertEquals(false, aNode.getProperty("boolean_property").getBoolean());
    a.setBoolean(null);
    assertFalse(aNode.hasProperty("boolean_property"));
  }

  public void testInt() throws Exception {
    assertNull(a.getInt());
    aNode.setProperty("int_property", 4);
    assertEquals((Integer)4, a.getInt());
    a.setInt(6);
    assertEquals(6, aNode.getProperty("int_property").getLong());
    a.setInt(null);
    assertFalse(aNode.hasProperty("int_property"));
  }

  public void testLong() throws Exception {
    assertNull(a.getLong());
    aNode.setProperty("long_property", 4);
    assertEquals((Long)4L, a.getLong());
    a.setLong(6L);
    assertEquals(6, aNode.getProperty("long_property").getLong());
    a.setLong(null);
    assertFalse(aNode.hasProperty("long_property"));
  }

  public void testDouble() throws Exception {
    assertNull(a.getDouble());
    aNode.setProperty("double_property", 4D);
    assertEquals(4D, a.getDouble());
    a.setDouble(6D);
    assertEquals(6D, aNode.getProperty("double_property").getDouble());
    a.setDouble(null);
    assertFalse(aNode.hasProperty("double_property"));
  }

  public void testDate() throws Exception {
    Date d = new Date(0);
    Calendar c = Calendar.getInstance();
    c.setTime(d);

    assertNull(a.getDate());
    aNode.setProperty("date_property", c);
    assertEquals(d, a.getDate());
    a.setDate(new Date(1));
    assertEquals(new Date(1), aNode.getProperty("date_property").getDate().getTime());
    a.setDate(null);
    assertFalse(aNode.hasProperty("date_property"));
  }

  public void testStream() throws Exception {
    assertNull(a.getBytes());
    aNode.setProperty("bytes_property", new ByteArrayInputStream("foo".getBytes("UTF8")));
    assertEquals("foo", new String(IO.getBytes(a.getBytes()), "UTF8"));
    a.setBytes(new ByteArrayInputStream("bar".getBytes("UTF8")));
    assertEquals("bar", new String(IO.getBytes(aNode.getProperty("bytes_property").getStream()), "UTF8"));
    a.setBytes(null);
    assertFalse(aNode.hasProperty("bytes_property"));
  }
}
