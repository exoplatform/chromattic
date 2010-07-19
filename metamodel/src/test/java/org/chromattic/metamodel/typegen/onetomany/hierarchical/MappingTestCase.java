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

package org.chromattic.metamodel.typegen.onetomany.hierarchical;

import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.RelationshipMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA() { testA(A1.class, A2.class); }
  public void testB() { testB(B1.class, B2.class); }
  public void testC() { testC(C1.class, C2.class); }
  public void testD() { testD(D.class); }

  protected void testA(Class<?> a1class, Class<?> a2class) {
    Map<Class<?>, BeanMapping> mappings = assertValid(a1class, a2class);
    BeanMapping _1 = mappings.get(a1class);
    BeanMapping _2 = mappings.get(a2class);
    RelationshipMapping.OneToMany.Hierarchic r1 = _1.getPropertyMapping("children", RelationshipMapping.OneToMany.Hierarchic.class);
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertNull(r1.getRelatedRelationshipMapping());
    assertEquals(0, _2.getProperties().size());
  }

  protected void testB(Class<?> b1class, Class<?> b2class) {
    Map<Class<?>, BeanMapping> mappings = assertValid(b1class, b2class);
    BeanMapping _1 = mappings.get(b1class);
    BeanMapping _2 = mappings.get(b2class);
    assertEquals(0, _1.getProperties().size());
    RelationshipMapping.ManyToOne.Hierarchic r2 = _2.getPropertyMapping("parent", RelationshipMapping.ManyToOne.Hierarchic.class);
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertNull(r2.getRelatedRelationshipMapping());
  }

  protected void testC(Class<?> c1class, Class<?> c2class) {
    Map<Class<?>, BeanMapping> mappings = assertValid(c1class, c2class);
    BeanMapping _1 = mappings.get(c1class);
    BeanMapping _2 = mappings.get(c2class);
    RelationshipMapping.OneToMany.Hierarchic r1 = _1.getPropertyMapping("children", RelationshipMapping.OneToMany.Hierarchic.class);
    RelationshipMapping.ManyToOne.Hierarchic r2 = _2.getPropertyMapping("parent", RelationshipMapping.ManyToOne.Hierarchic.class);
    assertSame(_2.getBean(), r1.getRelatedBean());
    assertSame(_1.getBean(), r2.getRelatedBean());
    assertSame(r1, r2.getRelatedRelationshipMapping());
    assertSame(r2, r1.getRelatedRelationshipMapping());
  }

  protected void testD(Class<?> dclass) {
    Map<Class<?>, BeanMapping> mappings = assertValid(dclass);
    BeanMapping _ = mappings.get(dclass);
    RelationshipMapping.OneToMany.Hierarchic r1 = _.getPropertyMapping("children", RelationshipMapping.OneToMany.Hierarchic.class);
    RelationshipMapping.ManyToOne.Hierarchic r2 = _.getPropertyMapping("parent", RelationshipMapping.ManyToOne.Hierarchic.class);
    assertSame(_.getBean(), r1.getRelatedBean());
    assertSame(_.getBean(), r2.getRelatedBean());
    assertSame(r1, r2.getRelatedRelationshipMapping());
    assertSame(r2, r1.getRelatedRelationshipMapping());
  }
}
