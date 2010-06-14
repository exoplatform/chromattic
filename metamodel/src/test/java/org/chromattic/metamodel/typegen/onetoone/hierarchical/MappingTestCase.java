/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.typegen.onetoone.hierarchical;

import org.chromattic.metamodel.mapping2.BeanMapping;
import org.chromattic.metamodel.mapping2.Relationship;
import org.chromattic.metamodel.mapping2.RelationshipMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA() {
    Map<Class<?>, BeanMapping> mappings = assertValid(A1.class, A2.class);
    BeanMapping a1 = mappings.get(A1.class);
    BeanMapping a2 = mappings.get(A2.class);
    RelationshipMapping r1 = (RelationshipMapping)a1.getPropertyMapping("child");
    assertSame(a2.getBean(), r1.getRelatedBean());
    Relationship.OneToOne.Hierarchic relationship = (Relationship.OneToOne.Hierarchic)r1.getRelationship();
    assertTrue(relationship.isOwner());
    assertEquals("child", relationship.getMappedBy());
  }

  public void testB() {
    Map<Class<?>, BeanMapping> mappings = assertValid(B1.class, B2.class);
    BeanMapping _1 = mappings.get(B1.class);
    BeanMapping _2 = mappings.get(B2.class);
    RelationshipMapping r2 = (RelationshipMapping)_2.getPropertyMapping("parent");
    assertSame(_1.getBean(), r2.getRelatedBean());
    Relationship.OneToOne.Hierarchic relationship = (Relationship.OneToOne.Hierarchic)r2.getRelationship();
    assertFalse(relationship.isOwner());
    assertEquals("child", relationship.getMappedBy());
  }

  public void testC() {
    Map<Class<?>, BeanMapping> mappings = assertValid(C1.class, C2.class);
    BeanMapping _1 = mappings.get(C1.class);
    BeanMapping _2 = mappings.get(C2.class);
    RelationshipMapping child = (RelationshipMapping)_1.getPropertyMapping("child");
    assertSame(_2.getBean(), child.getRelatedBean());
    Relationship.OneToOne.Hierarchic childRelationship = (Relationship.OneToOne.Hierarchic)child.getRelationship();
    assertEquals("child", childRelationship.getMappedBy());
    assertEquals(true, childRelationship.isOwner());
    RelationshipMapping parent = (RelationshipMapping)_2.getPropertyMapping("parent");
    assertSame(_1.getBean(), parent.getRelatedBean());
    assertSame(child, parent.getRelatedMapping());
    Relationship.OneToOne.Hierarchic parentRelationship = (Relationship.OneToOne.Hierarchic)parent.getRelationship();
    assertEquals("child", parentRelationship.getMappedBy());
    assertEquals(false, parentRelationship.isOwner());
    assertSame(parent, child.getRelatedMapping());
  }

  public void testD() {
    Map<Class<?>, BeanMapping> mappings = assertValid(D.class);
    BeanMapping _ = mappings.get(D.class);
    RelationshipMapping child = (RelationshipMapping)_.getPropertyMapping("child");
    assertSame(_.getBean(), child.getRelatedBean());
    Relationship.OneToOne.Hierarchic childRelationship = (Relationship.OneToOne.Hierarchic)child.getRelationship();
    assertEquals("child", childRelationship.getMappedBy());
    assertEquals(true, childRelationship.isOwner());
    RelationshipMapping parent = (RelationshipMapping)_.getPropertyMapping("parent");
    assertSame(_.getBean(), parent.getRelatedBean());
    assertSame(child, parent.getRelatedMapping());
    Relationship.OneToOne.Hierarchic parentRelationship = (Relationship.OneToOne.Hierarchic)parent.getRelationship();
    assertEquals("child", parentRelationship.getMappedBy());
    assertEquals(false, parentRelationship.isOwner());
    assertSame(parent, child.getRelatedMapping());
  }
}
