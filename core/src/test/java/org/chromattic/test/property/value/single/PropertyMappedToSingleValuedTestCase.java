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

package org.chromattic.test.property.value.single;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.support.MultiValue;
import org.chromattic.test.support.EventQueue;
import org.chromattic.core.api.ChromatticSessionImpl;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMappedToSingleValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A1.class);
  }

  /** . */
  private A1 a;

  /** . */
  private Node aNode;

  /** . */
  private ValueFactory factory;

  /** . */
  private EventQueue events;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    aNode = rootNode.addNode("tp_a_a", getNodeTypeName(A1.class));
    a = session.findByNode(A1.class, aNode);
    factory = session.getJCRSession().getValueFactory();
    events = new EventQueue();

    //
    session.addEventListener(events);
  }

  public void testString() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "string_property", "getStringProperty", "setStringProperty", PropertyType.STRING, new MultiValue.List("foo", "bar"), events).run();
  }

  public void testPath() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "path_property", "getPathProperty", "setPathProperty", PropertyType.PATH, new MultiValue.List("/foo", "/bar"), events).run();
  }

  public void testPrimitiveBoolean() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "primitive_boolean_property", "getPrimitiveBooleanProperty", "setPrimitiveBooleanProperty", PropertyType.BOOLEAN, new MultiValue.List(true, false), events).run();
  }

  public void testPrimitiveInt() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "primitive_int_property", "getIntProperty", "setIntProperty", PropertyType.LONG, new MultiValue.List(3, 5), events).run();
  }

  public void testPrimitiveLong() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "primitive_long_property", "getPrimitiveLongProperty", "setPrimitiveLongProperty", PropertyType.LONG, new MultiValue.List(3L, 5L), events).run();
  }

  public void testPrimitiveDouble() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "primitive_double_property", "getPrimitiveDoubleProperty", "setPrimitiveDoubleProperty", PropertyType.DOUBLE, new MultiValue.List(3D, 5D), events).run();
  }

  public void testBoolean() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "boolean_property", "getBooleanProperty", "setBooleanProperty", PropertyType.BOOLEAN, new MultiValue.List(true, false), events).run();
  }

  public void testInt() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "int_property", "getIntegerProperty", "setIntegerProperty", PropertyType.LONG, new MultiValue.List(4, 6), events).run();
  }

  public void testLong() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "long_property", "getLongProperty", "setLongProperty", PropertyType.LONG, new MultiValue.List(4L, 6L), events).run();
  }

  public void testDouble() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "double_property", "getDoubleProperty", "setDoubleProperty", PropertyType.DOUBLE, new MultiValue.List(4D, 6D), events).run();
  }

  public void testDate() throws Exception {
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "date_property", "getDateProperty", "setDateProperty", PropertyType.DATE, new MultiValue.List(new Date(0), new Date(1)), events).run();
  }

  public void testStream() throws Exception {
    InputStream s1 = new ByteArrayInputStream(new byte[]{0, 1, 2});
    InputStream s2 = new ByteArrayInputStream(new byte[]{3, 4, 5});
    new SingleValuedMappedToSingleValuedTest(factory, a, aNode, "bytes_property", "getBytesProperty", "setBytesProperty", PropertyType.BINARY, new MultiValue.List(s1, s2), events).run();
  }
}
