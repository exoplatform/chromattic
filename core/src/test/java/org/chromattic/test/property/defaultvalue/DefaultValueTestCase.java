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

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class DefaultValueTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testPrimitiveInt2() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    Node aNode = session.getRoot().getNode("a");

    //
    assertEquals(5, a.getPrimitiveInt());
    assertFalse(aNode.hasProperty("primitive_int_property"));

    //
    a.setPrimitiveInt((int) 3);
    assertEquals(3, a.getPrimitiveInt());
    assertEquals(3, aNode.getProperty("primitive_int_property").getLong());
    assertEquals(3, a.getPrimitiveInt());
  }
}
