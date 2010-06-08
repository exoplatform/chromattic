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

package org.chromattic.metamodel.typegen.onetoone.embedded;

import junit.framework.TestCase;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.TypeGen;
import org.reflext.api.ClassTypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SuperTypeTestCase extends TestCase {

  public void testOwnerSuperType() {
    TypeGen gen = new TypeGen();
    ClassTypeInfo a1 = gen.addType(A1.class);
    ClassTypeInfo a2 = gen.addType(A2.class);
    gen.generate();
    NodeType a1NT = gen.getNodeType(a1);
    assertEquals("a1", a1NT.getName());
    NodeType a2NT = gen.getNodeType(a2);
    assertEquals("a2", a2NT.getName());
    assertTrue(a2NT.getDeclaredSuperTypes().contains(a1NT));
    assertFalse(a1NT.getDeclaredSuperTypes().contains(a2NT));
  }

  public void testOwnedSuperType() {
    TypeGen gen = new TypeGen();
    ClassTypeInfo a3 = gen.addType(A3.class);
    ClassTypeInfo a4 = gen.addType(A4.class);
    gen.generate();
    NodeType a3NT = gen.getNodeType(a3);
    assertEquals("a3", a3NT.getName());
    NodeType a4NT = gen.getNodeType(a4);
    assertEquals("a4", a4NT.getName());
    assertFalse(a4NT.getDeclaredSuperTypes().contains(a3NT));
    assertTrue(a3NT.getDeclaredSuperTypes().contains(a4NT));
  }
}
