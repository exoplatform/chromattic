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

package org.chromattic.metamodel.mapping.onetotone;

import junit.framework.TestCase;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping.TypeMappingBuilder;
import org.chromattic.metamodel.mapping.value.NamedOneToOneMapping;
import org.reflext.api.ClassTypeInfo;
import org.reflext.core.TypeDomain;
import org.reflext.jlr.JavaLangReflectMethodModel;
import org.reflext.jlr.JavaLangReflectTypeModel;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToOneTestCase extends TestCase {

  public void testFoo() {

    TypeDomain domain = new TypeDomain(new JavaLangReflectTypeModel(), new JavaLangReflectMethodModel());
    ClassTypeInfo o = (ClassTypeInfo)domain.getType(Object.class);
    ClassTypeInfo a = (ClassTypeInfo)domain.getType(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.getType(B.class);

    TypeMappingBuilder builder = new TypeMappingBuilder(false);
    builder.add(o);
    builder.add(a);
    builder.add(b);
    builder.build();
    NodeTypeMapping antm = builder.get(a);
    NodeTypeMapping bntm = builder.get(b);

    NamedOneToOneMapping aa = (NamedOneToOneMapping)antm.getPropertyMappings().iterator().next().getValueMapping();
    NamedOneToOneMapping bb = (NamedOneToOneMapping)bntm.getPropertyMappings().iterator().next().getValueMapping();

    assertSame(bb, aa.getRelatedRelationship());

  }

}
