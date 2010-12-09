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

package org.chromattic.test.property.value.multi.list;

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
public class PropertyListMappedToSingleValuedTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A1.class);
  }

  /** . */
  private A1 g;

  /** . */
  private Node gNode;

  /** . */
  private ValueFactory factory;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    gNode = rootNode.addNode("tp_d_a", getNodeTypeName(A1.class));
    g = session.findByNode(A1.class, gNode);
    assertNotNull(g);
    factory = session.getJCRSession().getValueFactory();
  }

  public void testBoolean() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      g,
      gNode,
      "boolean_property",
      "getBooleanProperty",
      "setBooleanProperty",
      PropertyType.BOOLEAN,
      new MultiValue.List(true, false, true)
    ).run();
  }

  public void testInt() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      g,
      gNode,
      "int_property",
      "getIntegerProperty",
      "setIntegerProperty",
      PropertyType.LONG,
      new MultiValue.List(0, 1, 2)
    ).run();
  }

  public void testLong() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      g,
      gNode,
      "long_property",
      "getLongProperty",
      "setLongProperty",
      PropertyType.LONG,
      new MultiValue.List(0L, 1L, 2L)
    ).run();
  }

  public void testString() throws Exception {
    new MultiValuedMappedToSingleValuedTest(
      factory,
      g,
      gNode,
      "string_property",
      "getStringProperty",
      "setStringProperty",
      PropertyType.STRING,
      new MultiValue.List("foo", "bar1", "bar2")
    ).run();
  }
}