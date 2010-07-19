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

import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.TypeGen;
import org.chromattic.metamodel.typegen.TypeGenTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class EmbeddedTypeTestCase extends TypeGenTestCase {

  public void testOwnerMixinType() { testOwnerMixinType(B1.class, B2.class); }
  public void testOwnerSuperType() { testOwnerSuperType(A1.class, A2.class); }
  public void testOwnedSuperType() { testOwnedSuperType(A3.class, A4.class); }

  protected void testOwnerMixinType(Class<?> b1class, Class<?> b2class) {
    Map<Class<?>, NodeType> a = assertValid(b1class, b2class);
    TypeGen gen = new TypeGen();
    gen.generate();
    NodeType b1NT = a.get(b1class);
    assertEquals("b1", b1NT.getName());
    assertEquals(Collections.<String>set(), b1NT.getPropertyDefinitions().keySet());
    NodeType b2NT = a.get(b2class);
    assertEquals("b2", b2NT.getName());
    assertEquals(Collections.<String>set("foo"), b2NT.getPropertyDefinitions().keySet());
    assertFalse(b2NT.getDeclaredSuperTypes().contains(b1NT));
    assertFalse(b1NT.getDeclaredSuperTypes().contains(b2NT));
  }

  protected void testOwnerSuperType(Class<?> a1class, Class<?> a2class) {
    Map<Class<?>, NodeType> a = assertValid(a1class, a2class);
    NodeType a1NT = a.get(a1class);
    assertEquals("a1", a1NT.getName());
    NodeType a2NT = a.get(a2class);
    assertEquals("a2", a2NT.getName());
    assertTrue(a2NT.getDeclaredSuperTypes().contains(a1NT));
    assertFalse(a1NT.getDeclaredSuperTypes().contains(a2NT));
  }

  protected void testOwnedSuperType(Class<?> a3class, Class<?> a4class) {
    Map<Class<?>, NodeType> a = assertValid(a3class, a4class);
    NodeType a3NT = a.get(a3class);
    assertEquals("a3", a3NT.getName());
    NodeType a4NT = a.get(a4class);
    assertEquals("a4", a4NT.getName());
    assertFalse(a4NT.getDeclaredSuperTypes().contains(a3NT));
    assertTrue(a3NT.getDeclaredSuperTypes().contains(a4NT));
  }
}
