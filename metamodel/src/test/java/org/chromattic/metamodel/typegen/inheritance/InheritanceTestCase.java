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

package org.chromattic.metamodel.typegen.inheritance;

import junit.framework.TestCase;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.TypeGen;
import org.reflext.api.ClassTypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InheritanceTestCase extends TestCase {

  private NodeType a1NT;
  private NodeType a3NT;
  private NodeType a4NT;

  @Override
  protected void setUp() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo a1 = gen.addType(A1.class);
    ClassTypeInfo a3 = gen.addType(A3.class);
    ClassTypeInfo a4 = gen.addType(A4.class);
    gen.generate();
    a1NT = gen.getNodeType(a1);
    a3NT = gen.getNodeType(a3);
    a4NT = gen.getNodeType(a4);
  }

  public void testSuperTypes() throws Exception {
    assertEquals(0, a1NT.getSuperTypes().size());
    assertEquals(1, a3NT.getSuperTypes().size());
    assertTrue(a3NT.getSuperTypes().contains(a1NT));
    assertEquals(2, a4NT.getSuperTypes().size());
    assertTrue(a4NT.getSuperTypes().contains(a1NT));
    assertTrue(a4NT.getSuperTypes().contains(a3NT));
  }

  public void testDeclaredSuperTypes() throws Exception {
    assertEquals(0, a1NT.getDeclaredSuperTypes().size());
    assertEquals(1, a3NT.getDeclaredSuperTypes().size());
    assertTrue(a3NT.getDeclaredSuperTypes().contains(a1NT));
    assertEquals(1, a4NT.getDeclaredSuperTypes().size());
    assertTrue(a4NT.getDeclaredSuperTypes().contains(a3NT));
  }

  public void testProperty() throws Exception {
    assertEquals(1, a1NT.getPropertyDefinitions().size());
    assertEquals(0, a3NT.getPropertyDefinitions().size());
  }

  public void testChildNodeDefinitions() throws Exception {
    assertEquals(2, a1NT.getChildNodeDefinitions().size());
    assertEquals(0, a3NT.getChildNodeDefinitions().size());
  }
}