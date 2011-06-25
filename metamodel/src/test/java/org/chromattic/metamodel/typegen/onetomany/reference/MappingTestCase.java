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

import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.RelationshipMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A1.class, A2.class, B1.class, B2.class, C1.class, C2.class, D.class})
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA() {
    Map<Class<?>, BeanMapping> mappings = assertValid(A1.class, A2.class);
    BeanMapping _1 = mappings.get(A1.class);
    BeanMapping _2 = mappings.get(A2.class);
    RelationshipMapping.OneToMany.Reference r1 = _1.getPropertyMapping("referents", RelationshipMapping.OneToMany.Reference.class);
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertEquals("ref", r1.getMappedBy());
    assertEquals(Collections.<RelationshipMapping.ManyToOne.Reference>emptyList(), r1.getRelatedRelationshipMapping());
    assertEquals(0, _2.getProperties().size());
  }

  public void testB() {
    Map<Class<?>, BeanMapping> mappings = assertValid(B1.class, B2.class);
    BeanMapping _1 = mappings.get(B1.class);
    BeanMapping _2 = mappings.get(B2.class);
    assertEquals(0, _1.getProperties().size());
    RelationshipMapping.ManyToOne.Reference r2 = _2.getPropertyMapping("referenced", RelationshipMapping.ManyToOne.Reference.class);
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertEquals("ref", r2.getMappedBy());
    assertEquals(Collections.<RelationshipMapping.OneToMany.Reference>emptyList(), r2.getRelatedRelationshipMapping());
  }

  public void testC() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(C1.class, C2.class);
    BeanMapping _1 = mappings.get(C1.class);
    BeanMapping _2 = mappings.get(C2.class);
    RelationshipMapping.OneToMany.Reference r1 = _1.getPropertyMapping("referents", RelationshipMapping.OneToMany.Reference.class);
    RelationshipMapping.ManyToOne.Reference r2 = _2.getPropertyMapping("referenced", RelationshipMapping.ManyToOne.Reference.class);
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertEquals("ref", r1.getMappedBy());
    assertEquals("ref", r2.getMappedBy());
    assertEquals(Arrays.asList(r2), r1.getRelatedRelationshipMapping());
    assertEquals(Arrays.asList(r1), r2.getRelatedRelationshipMapping());
  }

  public void testD() {
    Map<Class<?>, BeanMapping> mappings = assertValid(D.class);
    BeanMapping _1 = mappings.get(D.class);
    RelationshipMapping.OneToMany.Reference r1 = _1.getPropertyMapping("referents", RelationshipMapping.OneToMany.Reference.class);
    RelationshipMapping.ManyToOne.Reference r2 = _1.getPropertyMapping("referenced", RelationshipMapping.ManyToOne.Reference.class);
    assertSame(_1.getBean(), r1.getRelatedBean());
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertEquals("ref", r1.getMappedBy());
    assertEquals("ref", r2.getMappedBy());
    assertEquals(Arrays.asList(r2), r1.getRelatedRelationshipMapping());
    assertEquals(Arrays.asList(r1), r2.getRelatedRelationshipMapping());
  }
}
