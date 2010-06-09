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

package org.chromattic.test.property.value.multi.array;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.property.value.multi.MultiValuedMappedToSingleValuedTest;
import org.chromattic.test.support.MultiValue;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyArrayMappedToSingleValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A1.class);
  }

  /** . */
  private A1 d;

  /** . */
  private Node dNode;

  /** . */
  private ValueFactory factory;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    dNode = rootNode.addNode("tp_d_a", getNodeTypeName(A1.class));
    d = session.findByNode(A1.class, dNode);
    assertNotNull(d);
    factory = session.getJCRSession().getValueFactory();
  }

  public void testPrimitiveBoolean() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "primitive_boolean_property",
      "getPrimitiveBoolean",
      "setPrimitiveBoolean",
      PropertyType.BOOLEAN,
      new MultiValue.Array(new boolean[]{true, false, true})
    ).run();
  }

  public void testBoolean() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "boolean_property",
      "getBoolean",
      "setBoolean",
      PropertyType.BOOLEAN,
      new MultiValue.Array(new Boolean[]{true, false, true})
    ).run();
  }

  public void testPrimitiveInt() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "primitive_int_property",
      "getPrimitiveInt",
      "setPrimitiveInt",
      PropertyType.LONG,
      new MultiValue.Array(new int[]{0, 1, 2})
    ).run();
  }

  public void testInt() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "int_property",
      "getInt",
      "setInt",
      PropertyType.LONG,
      new MultiValue.Array(new Integer[]{0, 1, 2})
    ).run();
  }

  public void testPrimitiveLong() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "primitive_long_property",
      "getPrimitiveLong",
      "setPrimitiveLong",
      PropertyType.LONG,
      new MultiValue.Array(new long[]{0, 1, 2})
    ).run();
  }

  public void testLong() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "long_property",
      "getLong",
      "setLong",
      PropertyType.LONG,
      new MultiValue.Array(new Long[]{0L, 1L, 2L})
    ).run();
  }

  public void testString() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      d,
      dNode,
      "string_property",
      "getString",
      "setString",
      PropertyType.STRING,
      new MultiValue.Array(new String[]{"foo", "bar1", "bar2"})
    ).run();
  }
}