/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.metamodel.typegen.properties;

import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.PropertiesMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testString() {
    Map<Class<?>, BeanMapping> mappings = assertValid(A1.class);
    BeanMapping mapping = mappings.get(A1.class);
    PropertiesMapping propertiesMapping = mapping.getPropertyMapping("properties", PropertiesMapping.class);
    assertEquals(ValueKind.SINGLE, propertiesMapping.getValueKind());
    assertEquals(PropertyMetaType.STRING, propertiesMapping.getMetaType());
    assertEquals(null, propertiesMapping.getPrefix());
  }

  public void testPrefix() {
    Map<Class<?>, BeanMapping> mappings = assertValid(D1.class);
    BeanMapping mapping = mappings.get(D1.class);
    PropertiesMapping propertiesMapping = mapping.getPropertyMapping("stringProperties", PropertiesMapping.class);
    assertEquals(ValueKind.SINGLE, propertiesMapping.getValueKind());
    assertEquals(null, propertiesMapping.getMetaType());
    assertEquals("foo", propertiesMapping.getPrefix());
  }
}
