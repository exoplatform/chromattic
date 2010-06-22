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

package org.chromattic.metamodel.typegen.property;

import org.chromattic.metamodel.bean2.MultiValueKind;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.mapping2.BeanMapping;
import org.chromattic.metamodel.mapping2.PropertiesMapping;
import org.chromattic.metamodel.mapping2.PropertyMapping;
import org.chromattic.metamodel.mapping2.ValueMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;
import org.reflext.api.ClassTypeInfo;

import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {


  public void testA1() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(A1.class);
    BeanMapping mapping = mappings.get(A1.class);
    ValueMapping.Single stringMapping = mapping.getPropertyMapping("string", ValueMapping.Single.class);
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("string", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("string", stringMapping.getName());
  }

  public void testA2() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(A2.class);
    BeanMapping mapping = mappings.get(A2.class);
    ValueMapping.Single stringMapping = mapping.getPropertyMapping("string", ValueMapping.Single.class);
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("string", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("string", stringMapping.getName());
  }

  public void testB1() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(B1.class);
    BeanMapping mapping = mappings.get(B1.class);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.LIST, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  public void testB2() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(B2.class);
    BeanMapping mapping = mappings.get(B2.class);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.LIST, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo","bar"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  public void testC1() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(C1.class);
    BeanMapping mapping = mappings.get(C1.class);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.ARRAY, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  public void testC2() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(C2.class);
    BeanMapping mapping = mappings.get(C2.class);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.ARRAY, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo","bar"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  public void testD1() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(D1.class);
    BeanMapping mapping = mappings.get(D1.class);
    PropertiesMapping<?> stringMapping = mapping.getPropertyMapping("properties", PropertiesMapping.class);
    assertEquals(MultiValueKind.MAP, stringMapping.getProperty().getKind());
    assertEquals(Object.class.getName(), ((ClassTypeInfo)stringMapping.getProperty().getValue().getEffectiveType()).getName());
  }

  public void testD2() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(D2.class);
    BeanMapping mapping = mappings.get(D2.class);
    PropertiesMapping<?> stringMapping = mapping.getPropertyMapping("properties", PropertiesMapping.class);
    assertEquals(MultiValueKind.MAP, stringMapping.getProperty().getKind());
    assertEquals(String.class.getName(), ((ClassTypeInfo)stringMapping.getProperty().getValue().getEffectiveType()).getName());
  }

  public void testE() throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(E.class);
    BeanMapping mapping = mappings.get(E.class);
    PropertyMapping<?, ?> stringMapping = mapping.getPropertyMapping("bytes", PropertyMapping.class);
//    assertEquals(String.class.getName(), stringMapping.getProperty().getValue().getClassType().getName());
  }
}
