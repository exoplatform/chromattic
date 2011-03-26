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
import org.chromattic.test.support.MultiValue;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyArrayMappedToMultiValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_C.class);
  }

  /** . */
  private TP_C c;

  /** . */
  private Node cNode;

  /** . */
  private ValueFactory factory;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    cNode = rootNode.addNode("tp_c_a", "tp_c");
    c = session.findByNode(TP_C.class, cNode);
    factory = session.getJCRSession().getValueFactory();
  }

  public void testPrimitiveBoolean() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "primitive_boolean_array_property",
      "getPrimitiveBoolean",
      "setPrimitiveBoolean",
      PropertyType.BOOLEAN,
      new MultiValue.Array(new boolean[]{true, false, true})
    ).run();
  }

  public void testBoolean() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "boolean_array_property",
      "getBoolean",
      "setBoolean",
      PropertyType.BOOLEAN,
      new MultiValue.Array(new Boolean[]{true, false, true})
    ).run();
  }

  public void testPrimitiveInt() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "primitive_int_array_property",
      "getPrimitiveInt",
      "setPrimitiveInt",
      PropertyType.LONG,
      new MultiValue.Array(new int[]{0, 1, 2})
    ).run();
  }

  public void testInt() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "int_array_property",
      "getInt",
      "setInt",
      PropertyType.LONG,
      new MultiValue.Array(new Integer[]{0, 1, 2})
    ).run();
  }

  public void testPrimitiveLong() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "primitive_long_array_property",
      "getPrimitiveLong",
      "setPrimitiveLong",
      PropertyType.LONG,
      new MultiValue.Array(new long[]{0, 1, 2})
    ).run();
  }

  public void testLong() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "long_array_property",
      "getLong",
      "setLong",
      PropertyType.LONG,
      new MultiValue.Array(new Long[]{0L, 1L, 2L})
    ).run();
  }

  public void testString() throws Exception {
    new MultiValuedMappedToMultiValuedTest(
      factory,
      c,
      cNode,
      "string_array_property",
      "getString",
      "setString",
      PropertyType.STRING,
      new MultiValue.Array(new String[]{"foo", "bar1", "bar2"})
    ).run();
  }
}