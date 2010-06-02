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

package org.chromattic.metamodel.bean;

import org.chromattic.api.BuilderException;
import org.reflext.api.*;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfoFactory {

  public BeanInfoFactory() {
  }

  public BeanInfo build(ClassTypeInfo typeInfo) {
    Map<String, PropertyInfo> properties = buildProperties(typeInfo);
    return new BeanInfo(typeInfo, properties);
  }

  private Map<String, PropertyInfo> buildProperties(ClassTypeInfo type) {
    MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL, true);
    Map<String, MethodInfo> getterMap = introspector.getGetterMap(type);
    Map<String, Set<MethodInfo>> setterMap = introspector.getSetterMap(type);

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

  private ClassTypeInfo resolveClass(ClassTypeInfo baseType, TypeInfo type) {
    TypeInfo resolvedType = baseType.resolve(type);
    return resolvedType instanceof ClassTypeInfo ? (ClassTypeInfo)resolvedType : null;
  }

  private Map<Class<? extends Annotation>, List<? extends Annotation>> getAnnotations(MethodInfo getter, MethodInfo setter, Class<? extends Annotation>... annotationClasses) {
    Map<Class<? extends Annotation>, List<? extends Annotation>> annotationMap = new HashMap<Class<? extends Annotation>, List<? extends Annotation>>();
    for (Class<? extends Annotation> annotationClass : annotationClasses) {
      List<? extends Annotation> annotations = getAnnotation(getter, setter, annotationClass);
      if (annotations.size() > 0) {
        annotationMap.put(annotationClass, annotations);
      }
    }
    return annotationMap;
  }

  private <A extends Annotation> List<A> getAnnotation(MethodInfo getter, MethodInfo setter, Class<A> annotationClass) {
    AnnotationType<A, ?> annotationType = AnnotationType.get(annotationClass);
    AnnotationIntrospector<A> spector = new AnnotationIntrospector<A>(annotationType);
    List<A> list = new ArrayList<A>(2);
    if (getter != null) {
      A getterAnnotation = spector.resolve(getter);
      if (getterAnnotation != null) {
        list.add(getterAnnotation);
      }
    }
    if (setter != null) {
      A setterAnnotation = spector.resolve(setter);
      if (setterAnnotation != null) {
        list.add(setterAnnotation);
      }
    }
    return list;
  }

  private PropertyInfo createPropertyInfo(
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

        if (rawComponentTI.getName().equals("byte")) {


          //
        } else {
          ValueInfo resolved = createValue(rawComponentTI);
          if (resolved instanceof SimpleValueInfo) {
            return new ArrayPropertyInfo<SimpleValueInfo>(name, (SimpleValueInfo)resolved, getter, setter);
          }
        }
      }
    }
    return null;
  }

  private ValueInfo createValue(ClassTypeInfo type) throws BuilderException {
    if (type instanceof SimpleTypeInfo) {
      return createSimpleValueInfo(type);
    } else if (type.getName().equals(String.class.getName())) {
      return createSimpleValueInfo(type);
    } else if (
      type.getName().equals(InputStream.class.getName()) ||
        type.getName().equals(Date.class.getName()) ||
        type.getKind() == ClassKind.ENUM) {
      return createSimpleValueInfo(type);
    } else {
      return new BeanValueInfo(type);
    }
  }

  /**
   * Build a simple value info meta data.
   *
   * @param typeInfo the type info
   * @return the simple value info
   * @throws BuilderException any exception that may prevent the correct building such as having a default value that
   *         does not match the type
   */
  private SimpleValueInfo createSimpleValueInfo(ClassTypeInfo typeInfo) throws BuilderException {
    if (typeInfo == null) {
      throw new NullPointerException();
    }

    //
    if (typeInfo instanceof SimpleTypeInfo && ((SimpleTypeInfo)typeInfo).isPrimitive()) {
      return new SimpleValueInfo(typeInfo);
    } else {
      switch (typeInfo.getKind()) {
        case CLASS:
        case ENUM:
          break;
        default:
          throw new AssertionError();
      }

      //
      return new SimpleValueInfo(typeInfo);
    }
  }
}
