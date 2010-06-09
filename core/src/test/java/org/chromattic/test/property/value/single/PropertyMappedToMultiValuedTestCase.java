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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMappedToMultiValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(C1.class);
  }

  /** . */
  private C1 e;

  /** . */
  private Node eNode;

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
    eNode = rootNode.addNode("tp_a_a", getNodeTypeName(C1.class));
    factory = session.getJCRSession().getValueFactory();
    e = session.findByNode(C1.class, eNode);
    events = new EventQueue();

    //
    session.addEventListener(events);
  }

  public void testString() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "string_array_property", "getString", "setString", PropertyType.STRING, new MultiValue.List("foo", "bar"), events).run();
  }

  public void testPrimitiveBoolean() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "primitive_boolean_array_property", "getPrimitiveBoolean", "setPrimitiveBoolean", PropertyType.BOOLEAN, new MultiValue.List(true, false), events).run();
  }

  public void testPrimitiveInt() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "primitive_int_array_property", "getPrimitiveInt", "setPrimitiveInt", PropertyType.LONG, new MultiValue.List(3, 5), events).run();
  }

  public void testPrimitiveLong() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "primitive_long_array_property", "getPrimitiveLong", "setPrimitiveLong", PropertyType.LONG, new MultiValue.List(3L, 5L), events).run();
  }

  public void testBoolean() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "boolean_array_property", "getBoolean", "setBoolean", PropertyType.BOOLEAN, new MultiValue.List(true, false), events).run();
  }

  public void testInt() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "int_array_property", "getInt", "setInt", PropertyType.LONG, new MultiValue.List(4, 6), events).run();
  }

  public void testLong() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "long_array_property", "getLong", "setLong", PropertyType.LONG, new MultiValue.List(4L, 6L), events).run();
  }

  public void testDate() throws Exception {
    new SingleValuedMappedToMultiValuedTest(factory, e, eNode, "date_array_property", "getDate", "setDate", PropertyType.LONG, new MultiValue.List(new Date(0), new Date(0)), events).run();
  }
}