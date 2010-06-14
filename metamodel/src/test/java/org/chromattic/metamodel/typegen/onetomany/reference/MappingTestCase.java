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

package org.chromattic.metamodel.typegen.onetomany.reference;

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
    BeanMapping _1 = mappings.get(A1.class);
    BeanMapping _2 = mappings.get(A2.class);
    RelationshipMapping r1 = (RelationshipMapping)_1.getPropertyMapping("referents");
    Relationship.OneToMany.Reference o2 = (Relationship.OneToMany.Reference)r1.getRelationship();
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertEquals("ref", o2.getMappedBy());
    assertNull(r1.getRelatedMapping());
    assertEquals(0, _2.getProperties().size());
  }

  public void testB() {
    Map<Class<?>, BeanMapping> mappings = assertValid(B1.class, B2.class);
    BeanMapping _1 = mappings.get(B1.class);
    BeanMapping _2 = mappings.get(B2.class);
    assertEquals(0, _1.getProperties().size());
    RelationshipMapping r2 = (RelationshipMapping)_2.getPropertyMapping("referenced");
    Relationship.ManyToOne.Reference o2 = (Relationship.ManyToOne.Reference)r2.getRelationship();
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertEquals("ref", o2.getMappedBy());
    assertNull(r2.getRelatedMapping());
  }

  public void testC() {
    Map<Class<?>, BeanMapping> mappings = assertValid(C1.class, C2.class);
    BeanMapping _1 = mappings.get(C1.class);
    BeanMapping _2 = mappings.get(C2.class);
    RelationshipMapping r1 = (RelationshipMapping)_1.getPropertyMapping("referents");
    RelationshipMapping r2 = (RelationshipMapping)_2.getPropertyMapping("referenced");
    Relationship.OneToMany.Reference childRelationship = (Relationship.OneToMany.Reference) r1.getRelationship();
    Relationship.ManyToOne.Reference parentRelationship = (Relationship.ManyToOne.Reference) r2.getRelationship();
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertSame(_1.getBean(), r2.getRelatedBean());
//    assertSame(child, parent.getRelatedMapping());
//    assertSame(parent, child.getRelatedMapping());
  }
}
