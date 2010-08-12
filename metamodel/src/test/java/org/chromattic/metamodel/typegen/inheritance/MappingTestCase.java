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

package org.chromattic.metamodel.typegen.inheritance;

import org.chromattic.metamodel.mapping.RelationshipMapping;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testB() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(B1.class, B2.class);

    //
    BeanMapping _1 = mappings.get(B1.class);
    RelationshipMapping.ManyToOne.Hierarchic r1 = _1.getPropertyMapping("parent2", RelationshipMapping.ManyToOne.Hierarchic.class);
    assertNotNull(r1);
    assertNull(r1.getParent());
    assertTrue(r1.isNew());

    //
    BeanMapping _2 = mappings.get(B2.class);
    RelationshipMapping.ManyToOne.Hierarchic r2 = _2.getPropertyMapping("parent2", RelationshipMapping.ManyToOne.Hierarchic.class);
    assertNotNull(r2);
    assertNotNull(r2.getParent());
    assertFalse(r2.isNew());
  }

}
