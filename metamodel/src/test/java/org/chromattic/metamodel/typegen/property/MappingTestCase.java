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

import org.chromattic.metamodel.bean.MultiValueKind;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.PropertiesMapping;
import org.chromattic.metamodel.mapping.ValueMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;
import org.reflext.api.ClassTypeInfo;

import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA1() throws Exception { testA1(A1.class); }
  public void testA2() throws Exception { testA2(A2.class); }
  public void testB1() throws Exception { testB1(B1.class); }
  public void testB2() throws Exception { testB2(B2.class); }
  public void testC1() throws Exception { testC1(C1.class); }
  public void testC2() throws Exception { testC2(C2.class); }
  public void testD1() throws Exception { testD1(D1.class); }
  public void testD2() throws Exception { testD2(D2.class); }
  public void testE() throws Exception { testE(E.class); }

  protected void testA1(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Single stringMapping = mapping.getPropertyMapping("string", ValueMapping.Single.class);
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("string", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("string", stringMapping.getName());
  }

  protected void testA2(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Single stringMapping = mapping.getPropertyMapping("string", ValueMapping.Single.class);
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("string", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("string", stringMapping.getName());
  }

  protected void testB1(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.LIST, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  protected void testB2(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.LIST, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo","bar"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  protected void testC1(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.ARRAY, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(null, propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  protected void testC2(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    ValueMapping.Multi stringMapping = mapping.getPropertyMapping("strings", ValueMapping.Multi.class);
    assertEquals(MultiValueKind.ARRAY, stringMapping.getProperty().getKind());
    PropertyDefinitionMapping propertyDefinition = stringMapping.getPropertyDefinition();
    assertEquals("strings", propertyDefinition.getName());
    assertEquals(Arrays.asList("foo","bar"), propertyDefinition.getDefaultValue());
    assertEquals(PropertyMetaType.STRING, propertyDefinition.getMetaType());
    assertEquals("strings", stringMapping.getName());
  }

  protected void testD1(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    PropertiesMapping<?> stringMapping = mapping.getPropertyMapping("properties", PropertiesMapping.class);
    assertEquals(MultiValueKind.MAP, stringMapping.getProperty().getKind());
    assertEquals(Object.class.getName(), ((ClassTypeInfo)stringMapping.getProperty().getValue().getEffectiveType()).getName());
  }

  protected void testD2(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    PropertiesMapping<?> stringMapping = mapping.getPropertyMapping("properties", PropertiesMapping.class);
    assertEquals(MultiValueKind.MAP, stringMapping.getProperty().getKind());
    assertEquals(String.class.getName(), ((ClassTypeInfo)stringMapping.getProperty().getValue().getEffectiveType()).getName());
  }

  protected void testE(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    PropertyMapping<?, ?> stringMapping = mapping.getPropertyMapping("bytes", PropertyMapping.class);
//    assertEquals(String.class.getName(), stringMapping.getProperty().getValue().getClassType().getName());
  }
}
