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

import org.chromattic.metamodel.mapping2.NodeTypeMapping;
import org.chromattic.metamodel.mapping2.Relationship;
import org.chromattic.metamodel.mapping2.RelationshipPropertyMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA() {
    Map<Class<?>, NodeTypeMapping> mappings = assertValid(A1.class, A2.class);
    NodeTypeMapping a1 = mappings.get(A1.class);
    NodeTypeMapping a2 = mappings.get(A2.class);
    RelationshipPropertyMapping r1 = (RelationshipPropertyMapping)a1.getPropertyMapping("child");
    assertSame(a2.getBean(), r1.getRelatedBean());
    Relationship.OneToOne.Hierarchic relationship = (Relationship.OneToOne.Hierarchic)r1.getRelationship();
    assertTrue(relationship.isOwner());
    assertEquals("child", relationship.getMappedBy());
  }

  public void testB() {
    Map<Class<?>, NodeTypeMapping> mappings = assertValid(B1.class, B2.class);
    NodeTypeMapping _1 = mappings.get(B1.class);
    NodeTypeMapping _2 = mappings.get(B2.class);
    RelationshipPropertyMapping r2 = (RelationshipPropertyMapping)_2.getPropertyMapping("parent");
    assertSame(_1.getBean(), r2.getRelatedBean());
    Relationship.OneToOne.Hierarchic relationship = (Relationship.OneToOne.Hierarchic)r2.getRelationship();
    assertFalse(relationship.isOwner());
    assertEquals("child", relationship.getMappedBy());
  }

  public void testC() {
    Map<Class<?>, NodeTypeMapping> mappings = assertValid(C.class);
    NodeTypeMapping _ = mappings.get(C.class);
    RelationshipPropertyMapping child = (RelationshipPropertyMapping)_.getPropertyMapping("child");
    assertSame(_.getBean(), child.getRelatedBean());
    Relationship.OneToOne.Hierarchic childRelationship = (Relationship.OneToOne.Hierarchic)child.getRelationship();
    assertEquals("child", childRelationship.getMappedBy());
    assertEquals(true, childRelationship.isOwner());
    RelationshipPropertyMapping parent = (RelationshipPropertyMapping)_.getPropertyMapping("parent");
    assertSame(_.getBean(), parent.getRelatedBean());
    assertSame(child, parent.getRelatedMapping());
    Relationship.OneToOne.Hierarchic parentRelationship = (Relationship.OneToOne.Hierarchic)parent.getRelationship();
    assertEquals("child", parentRelationship.getMappedBy());
    assertEquals(false, parentRelationship.isOwner());
    assertSame(parent, child.getRelatedMapping());
  }
}
