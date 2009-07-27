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

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMappedToMultiValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_E.class);
  }

  /** . */
  private TP_E e;

  /** . */
  private Node eNode;

  /** . */
  private ValueFactory factory;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    eNode = rootNode.addNode("tp_a_a", "tp_c");
    factory = session.getJCRSession().getValueFactory();
    e = session.findByNode(TP_E.class, eNode);
  }

  public void testString() throws Exception {
    assertEquals(null, e.getString());
    eNode.setProperty("string_array_property", new String[]{"foo"});
    assertEquals("foo", e.getString());
    e.setString("bar");
    assertEquals(1, eNode.getProperty("string_array_property").getValues().length);
    assertEquals("bar", eNode.getProperty("string_array_property").getValues()[0].getString());
    e.setString(null);
    assertEquals(0, eNode.getProperty("string_array_property").getValues().length);
  }

  public void testPrimitiveBoolean() throws Exception {
    try {
      e.getPrimitiveBoolean();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    eNode.setProperty("primitive_boolean_array_property", new Value[]{factory.createValue(true)});
    assertEquals(true, e.getPrimitiveBoolean());
    e.setPrimitiveBoolean(false);
    assertEquals(1, eNode.getProperty("primitive_boolean_array_property").getValues().length);
    assertEquals(false, eNode.getProperty("primitive_boolean_array_property").getValues()[0].getBoolean());
  }

  public void testPrimitiveInt() throws Exception {
    try {
      e.getPrimitiveInt();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    eNode.setProperty("primitive_int_array_property", new Value[]{factory.createValue(3)});
    assertEquals(3, e.getPrimitiveInt());
    e.setPrimitiveInt(5);
    assertEquals(1, eNode.getProperty("primitive_int_array_property").getValues().length);
    assertEquals(5, eNode.getProperty("primitive_int_array_property").getValues()[0].getLong());
  }

  public void testPrimitiveLong() throws Exception {
    try {
      e.getPrimitiveLong();
      fail();
    }
    catch (IllegalStateException expected) {
    }
    eNode.setProperty("primitive_long_array_property", new Value[]{factory.createValue(3)});
    assertEquals(3, e.getPrimitiveLong());
    e.setPrimitiveLong(5);
    assertEquals(1, eNode.getProperty("primitive_long_array_property").getValues().length);
    assertEquals(5, eNode.getProperty("primitive_long_array_property").getValues()[0].getLong());
  }

  public void testBoolean() throws Exception {
    assertNull(e.getBoolean());
    eNode.setProperty("boolean_array_property", new Value[]{factory.createValue(true)});
    assertEquals((Boolean)true, e.getBoolean());
    e.setBoolean(false);
    assertEquals(1, eNode.getProperty("boolean_array_property").getValues().length);
    assertEquals(false, eNode.getProperty("boolean_array_property").getValues()[0].getBoolean());
    e.setBoolean(null);
    assertEquals(0, eNode.getProperty("boolean_array_property").getValues().length);
  }

  public void testInt() throws Exception {
    assertNull(e.getInt());
    eNode.setProperty("int_array_property", new Value[]{factory.createValue(4)});
    assertEquals((Integer)4, e.getInt());
    e.setInt(6);
    assertEquals(1, eNode.getProperty("int_array_property").getValues().length);
    assertEquals(6, eNode.getProperty("int_array_property").getValues()[0].getLong());
    e.setInt(null);
    assertEquals(0, eNode.getProperty("int_array_property").getValues().length);
  }

  public void testLong() throws Exception {
    assertNull(e.getLong());
    eNode.setProperty("long_array_property", new Value[]{factory.createValue(4)});
    assertEquals((Long)4L, e.getLong());
    e.setLong(6L);
    assertEquals(1, eNode.getProperty("long_array_property").getValues().length);
    assertEquals(6, eNode.getProperty("long_array_property").getValues()[0].getLong());
    e.setLong(null);
    assertEquals(0, eNode.getProperty("long_array_property").getValues().length);
  }

  public void testDate() throws Exception {
    Date d = new Date(0);
    Calendar c = Calendar.getInstance();
    c.setTime(d);

    assertNull(e.getDate());
    eNode.setProperty("date_array_property", new Value[]{factory.createValue(c)});
    assertEquals(d, e.getDate());
    e.setDate(new Date(1));
    assertEquals(1, eNode.getProperty("date_array_property").getValues().length);
    assertEquals(new Date(1), eNode.getProperty("date_array_property").getValues()[0].getDate().getTime());
    e.setDate(null);
    assertEquals(0, eNode.getProperty("date_array_property").getValues().length);
  }
}