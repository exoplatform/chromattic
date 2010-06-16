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
import org.chromattic.metamodel.mapping.InvalidMappingException;
import org.chromattic.metamodel.typegen.*;
import org.reflext.api.ClassTypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InheritanceTestCase extends TypeGenTestCase {

  private NodeType a1NT;
  private NodeType a3NT;
  private NodeType a4NT;
  private NodeType a5NT;

  @Override
  protected void setUp() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(A1.class, A3.class, A4.class, A5.class);
    a1NT = a.get(A1.class);
    a3NT = a.get(A3.class);
    a4NT = a.get(A4.class);
    a5NT = a.get(A5.class);
  }

  public void testSuperTypes() throws Exception {
    assertEquals(1, a1NT.getSuperTypes().size());
    assertEquals(2, a3NT.getSuperTypes().size());
    assertEquals(2, a5NT.getSuperTypes().size());
    assertTrue(a3NT.getSuperTypes().contains(a1NT));
    assertTrue(a5NT.getSuperTypes().contains(a1NT));
    assertEquals(3, a4NT.getSuperTypes().size());
    assertTrue(a4NT.getSuperTypes().contains(a1NT));
    assertTrue(a4NT.getSuperTypes().contains(a3NT));
  }

  public void testDeclaredSuperTypes() throws Exception {
    assertEquals(1, a1NT.getDeclaredSuperTypes().size());
    assertEquals(1, a3NT.getDeclaredSuperTypes().size());
    assertEquals(1, a5NT.getDeclaredSuperTypes().size());
    assertTrue(a3NT.getDeclaredSuperTypes().contains(a1NT));
    assertTrue(a5NT.getDeclaredSuperTypes().contains(a1NT));
    assertEquals(1, a4NT.getDeclaredSuperTypes().size());
    assertTrue(a4NT.getDeclaredSuperTypes().contains(a3NT));
  }

  public void testProperty() throws Exception {
    assertEquals(1, a1NT.getPropertyDefinitions().size());
    assertEquals(0, a3NT.getPropertyDefinitions().size());
    assertEquals(0, a5NT.getPropertyDefinitions().size());
  }

  public void testChildNodeDefinitions() throws Exception {
    assertEquals(2, a1NT.getChildNodeDefinitions().size());
    assertEquals(0, a3NT.getChildNodeDefinitions().size());
    assertEquals(2, a5NT.getChildNodeDefinitions().size());
  }

  public void testRelationshipOverride() {
    Map<Class<?>, NodeType> a = assertValid(G1.class, G2.class, G3.class);
    NodeType g1 = a.get(H1.class);
    NodeType g2 = a.get(H2.class);
    NodeType g3 = a.get(H3.class);
//    NodeDefinition g2Def = g3.getChildNodeDefinition("g2");
//    assertNotNull(g2Def);
  }

  public void testGenericRelationship() {
    Map<Class<?>, NodeType> a = assertValid(H1.class, H2.class, H3.class);
    NodeType h1 = a.get(H1.class);
    NodeType h2 = a.get(H2.class);
    NodeType h3 = a.get(H3.class);
    NodeDefinition h1AnyDef = h1.getChildNodeDefinition("*");
    assertNotNull(h1AnyDef);
    assertEquals("nt:base", h1AnyDef.getNodeTypeName());
    NodeDefinition h2AnyDef = h2.getChildNodeDefinition("*");
    assertNotNull(h2AnyDef);
    assertEquals("h3", h2AnyDef.getNodeTypeName());
  }

  public void testOneToOneGenericRelationship() {
    Map<Class<?>, NodeType> a = assertValid(I1.class, I2.class, I3.class, I4.class);
    NodeType i1 = a.get(I1.class);
    NodeType i2 = a.get(I2.class);
    NodeType i3 = a.get(I3.class);
    NodeType i4 = a.get(I4.class);
    NodeDefinition i1ChildDef = i1.getChildNodeDefinition("child");
    assertNotNull(i1ChildDef);
    assertEquals("i3", i1ChildDef.getNodeTypeName());
    NodeDefinition i2ChildDef = i2.getChildNodeDefinition("child");
    assertNotNull(i2ChildDef);
    assertEquals("i4", i2ChildDef.getNodeTypeName());
  }

  public void testInvalidAbstractManyToOne() {
    assertInvalid(B2.class);
  }

//  public void testInvalidAbstractProperty() {
//    assertInvalid(C2.class);
//  }

  public void testInvalidAbstractOwnerOneToOne() {
    assertInvalid(D2.class);
  }

  public void testInvalidAbstractOneToOne() {
    assertInvalid(E2.class);
  }

  public void testInvalidAbstractOneToMany() {
    assertInvalid(F2.class);
  }
}