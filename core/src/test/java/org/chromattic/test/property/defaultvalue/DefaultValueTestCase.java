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
package org.chromattic.test.property.defaultvalue;

import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import javax.jcr.Value;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DefaultValueTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testPrimitiveInt1() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    Node aNode = session.getRoot().getNode("a");

    //
    try {
      a.getPrimitiveInt1();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
    assertFalse(aNode.hasProperty("primitive_int_property"));

    //
    a.setPrimitiveInt1(3);
    assertEquals(3, a.getPrimitiveInt1());
    assertEquals(3, aNode.getProperty("primitive_int_property").getLong());
    assertEquals(3, a.getPrimitiveInt1());
  }

  public void testPrimitiveInt2() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    Node aNode = session.getRoot().getNode("a");

    //
    assertEquals(5, a.getPrimitiveInt2());
    assertFalse(aNode.hasProperty("primitive_int_property"));

    //
    a.setPrimitiveInt2(3);
    assertEquals(3, a.getPrimitiveInt2());
    assertEquals(3, aNode.getProperty("primitive_int_property").getLong());
    assertEquals(3, a.getPrimitiveInt2());
  }

  public void testPrimitiveInt3() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    Node aNode = session.getRoot().getNode("a");

    //
    assertEquals(5, a.getPrimitiveInt3());
    assertFalse(aNode.hasProperty("primitive_int_property"));

    //
    a.setPrimitiveInt2(3);
    assertEquals(3, a.getPrimitiveInt3());
    assertEquals(3, aNode.getProperty("primitive_int_property").getLong());
    assertEquals(3, a.getPrimitiveInt3());
  }
}
