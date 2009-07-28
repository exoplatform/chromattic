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

package org.chromattic.core.bean;

import org.reflext.api.TypeInfo;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.ClassIntrospector;
import org.reflext.api.MethodInfo;
import org.reflext.api.SimpleTypeInfo;
import org.reflext.api.ParameterizedTypeInfo;
import org.reflext.api.ArrayTypeInfo;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.lang.annotation.Annotation;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfo {

  /** . */
  private final ClassTypeInfo typeInfo;

  /** . */
  private final Map<String, PropertyInfo> properties;

  public BeanInfo(ClassTypeInfo typeInfo) {

    //
    this.typeInfo = typeInfo;
    this.properties = buildProperties(typeInfo);
  }

  public Set<String> getPropertyNames() {
    return properties.keySet();
  }

  public Collection<PropertyInfo> getProperties() {
    return properties.values();
  }

  public ClassTypeInfo getTypeInfo() {
    return typeInfo;
  }

  public PropertyInfo getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  public <A extends Annotation> Collection<PropertyInfo> getProperties(Class<A> annotationClass) {
    List<PropertyInfo> matched = new ArrayList<PropertyInfo>();
    for (PropertyInfo property : properties.values()) {
      if (property.getAnnotation(annotationClass) != null) {
        matched.add(property);
      }
    }
    return matched;
  }

  private static  Map<String, PropertyInfo> buildProperties(ClassTypeInfo type) {
    ClassIntrospector introspector = new ClassIntrospector(type);
    Map<String, MethodInfo> getterMap = introspector.getGetterMap();
    Map<String, Set<MethodInfo>> setterMap = introspector.getSetterMap();

    //
    Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();

    //
    for (Map.Entry<String, MethodInfo> getterEntry : getterMap.entrySet()) {
      String name = getterEntry.getKey();
      MethodInfo getter = getterEntry.getValue();
      TypeInfo getterTypeInfo = getter.getReturnType();

      //
      Set<MethodInfo> setters = setterMap.get(name);
      PropertyInfo property = null;

      //
      if (setters != null) {
        for (MethodInfo setter : setters) {
          TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
          if (getterTypeInfo.equals(setterTypeInfo)) {
            property = createPropertyInfo(
              type,
              name,
              getterTypeInfo,
              getter,
              setter);
          }
        }
      }

      //
      if (property == null) {
        property = createPropertyInfo(
          type,
          name,
          getterTypeInfo,
          getter,
          null);
      }

      //
      if (property != null) {
        properties.put(name, property);
      }
    }

    //
    setterMap.keySet().removeAll(properties.keySet());
    for (Map.Entry<String, Set<MethodInfo>> setterEntry : setterMap.entrySet()) {
      String name = setterEntry.getKey();
      for (MethodInfo setter : setterEntry.getValue()) {
        TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
        PropertyInfo property = createPropertyInfo(
          type,
          name,
          setterTypeInfo,
          null,
          setter);
        if (property != null) {
          properties.put(name, property);
          break;
        }
      }
    }

    //
    return properties;
  }

  private static ClassTypeInfo resolveClass(ClassTypeInfo baseType, TypeInfo type) {
    TypeInfo resolvedType = baseType.resolve(type);
    return resolvedType instanceof ClassTypeInfo ? (ClassTypeInfo)resolvedType : null;
  }

  private static ValueInfo createValue(ClassTypeInfo type) {
    if (type instanceof SimpleTypeInfo) {
      return new SimpleValueInfo(type);
    } else if (
      type.getName().equals(String.class.getName()) ||
      type.getName().equals(InputStream.class.getName()) ||
      type.getName().equals(Date.class.getName())) {
      return new SimpleValueInfo(type);
    } else {
      return new BeanValueInfo(type);
    }
  }

  private static PropertyInfo createPropertyInfo(
    ClassTypeInfo beanTypeInfo,
    String name,
    TypeInfo typeInfo,
    MethodInfo getter,
    MethodInfo setter) {
    TypeInfo resolvedTI = beanTypeInfo.resolve(typeInfo);
    if (resolvedTI instanceof ParameterizedTypeInfo) {
      ParameterizedTypeInfo parameterizedTI = (ParameterizedTypeInfo)resolvedTI;
      TypeInfo rawTI = parameterizedTI.getRawType();
      if (rawTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawClassTI = (ClassTypeInfo)rawTI;
        String rawClassName = rawClassTI.getName();
        if (rawClassName.equals("java.util.Collection") || rawClassName.equals("java.util.List")) {
          TypeInfo elementTV = parameterizedTI.getTypeArguments().get(0);
          ClassTypeInfo elementTI = resolveClass(beanTypeInfo, elementTV);
          if (elementTI != null) {
            ValueInfo resolvedElementTI = createValue(elementTI);
            if (rawClassName.equals("java.util.Collection")) {
              return new CollectionPropertyInfo<ValueInfo>(
                name,
                resolvedElementTI,
                getter,
                setter);
            } else {
              return new ListPropertyInfo<ValueInfo>(
                name,
                resolvedElementTI,
                getter,
                setter);
            }
          }
        } else if (rawClassName.equals("java.util.Map")) {
          TypeInfo elementTV = parameterizedTI.getTypeArguments().get(1);
          ClassTypeInfo elementTI = resolveClass(beanTypeInfo, elementTV);
          if (elementTI != null) {
            ValueInfo resolvedElementTI = createValue(elementTI);
            TypeInfo keyTV = parameterizedTI.getTypeArguments().get(0);
            ClassTypeInfo keyTI = resolveClass(beanTypeInfo, keyTV);
            if (keyTI != null) {
              ValueInfo resolvedKeyTI = createValue(keyTI);
              return new MapPropertyInfo<ValueInfo, ValueInfo>(
                name,
                resolvedElementTI,
                resolvedKeyTI,
                getter,
                setter);
            }
          }
        }
      }
    } else if (resolvedTI instanceof ClassTypeInfo) {
      ValueInfo resolved = createValue((ClassTypeInfo)resolvedTI);
      return new SingleValuedPropertyInfo<ValueInfo>(
        name,
        resolved,
        getter,
        setter);
    } else if (resolvedTI instanceof ArrayTypeInfo) {
      TypeInfo componentTI = ((ArrayTypeInfo)resolvedTI).getComponentType();
      if (componentTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawComponentTI = (ClassTypeInfo)componentTI;
        ValueInfo resolved = createValue(rawComponentTI);
        if (resolved instanceof SimpleValueInfo) {
          return new ArrayPropertyInfo<SimpleValueInfo>(name, (SimpleValueInfo)resolved, getter, setter);
        }
      }
    }
    return null;
  }

}
