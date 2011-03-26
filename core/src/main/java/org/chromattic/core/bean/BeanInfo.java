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

import org.chromattic.api.BuilderException;
import org.chromattic.api.annotations.DefaultValue;
import org.reflext.api.TypeInfo;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.SimpleTypeInfo;
import org.reflext.api.ParameterizedTypeInfo;
import org.reflext.api.ArrayTypeInfo;
import org.reflext.api.AnnotationIntrospector;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.introspection.HierarchyScope;
import org.chromattic.api.annotations.Path;

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

  private static ClassTypeInfo resolveClass(ClassTypeInfo baseType, TypeInfo type) {
    TypeInfo resolvedType = baseType.resolve(type);
    return resolvedType instanceof ClassTypeInfo ? (ClassTypeInfo)resolvedType : null;
  }

  private static Map<Class<? extends Annotation>, List<? extends Annotation>> getAnnotations(MethodInfo getter, MethodInfo setter, Class<? extends Annotation>... annotationClasses) {
    Map<Class<? extends Annotation>, List<? extends Annotation>> annotationMap = new HashMap<Class<? extends Annotation>, List<? extends Annotation>>();
    for (Class<? extends Annotation> annotationClass : annotationClasses) {
      List<? extends Annotation> annotations = getAnnotation(getter, setter, annotationClass);
      if (annotations.size() > 0) {
        annotationMap.put(annotationClass, annotations);
      }
    }
    return annotationMap;
  }

  private static <A extends Annotation> List<A> getAnnotation(MethodInfo getter, MethodInfo setter, Class<A> annotationClass) {
    AnnotationIntrospector<A> spector = new AnnotationIntrospector<A>(annotationClass);
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

  private static ValueInfo createValue(
    ClassTypeInfo type,
    MethodInfo getter,
    MethodInfo setter) throws BuilderException {

    // That should be somehow improved
    Map<Class<? extends Annotation>, List<? extends Annotation>> annotationMap = getAnnotations(
      getter,
      setter,
      DefaultValue.Int.class,
      DefaultValue.Boolean.class,
      DefaultValue.Long.class,
      DefaultValue.Float.class,
      DefaultValue.Double.class
    );
    if (annotationMap.size() > 2) {
      throw new BuilderException("Too many default value annotations");
    }
    Annotation defaultValue = null;
    if (annotationMap.size() == 1) {
      List<? extends Annotation> annotations = annotationMap.values().iterator().next();
      if (annotations.size() == 1) {
        defaultValue = annotations.get(0);
      } else {
        throw new BuilderException("Too many default value annotations");
      }
    }

    if (type instanceof SimpleTypeInfo) {
      return SimpleValueInfo.create(type, defaultValue);
    } else if (type.getName().equals(String.class.getName())) {
      AnnotationIntrospector<Path> intro = new AnnotationIntrospector<Path>(Path.class);
      if ((getter != null && intro.resolve(getter) != null ) || (setter != null && intro.resolve(setter) != null)) {
        return SimpleValueInfo.createPath(type);
      } else {
        return SimpleValueInfo.create(type, defaultValue);
      }
    } else if (
      type.getName().equals(InputStream.class.getName()) ||
      type.getName().equals(Date.class.getName())) {
      return SimpleValueInfo.create(type, defaultValue);
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
            ValueInfo resolvedElementTI = createValue(elementTI, getter, setter);
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
            ValueInfo resolvedElementTI = createValue(elementTI, getter, setter);
            TypeInfo keyTV = parameterizedTI.getTypeArguments().get(0);
            ClassTypeInfo keyTI = resolveClass(beanTypeInfo, keyTV);
            if (keyTI != null) {
              ValueInfo resolvedKeyTI = createValue(keyTI, getter, setter);
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
      ValueInfo resolved = createValue((ClassTypeInfo)resolvedTI, getter, setter);
      return new SingleValuedPropertyInfo<ValueInfo>(
        name,
        resolved,
        getter,
        setter);
    } else if (resolvedTI instanceof ArrayTypeInfo) {
      TypeInfo componentTI = ((ArrayTypeInfo)resolvedTI).getComponentType();
      if (componentTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawComponentTI = (ClassTypeInfo)componentTI;
        ValueInfo resolved = createValue(rawComponentTI, getter, setter);
        if (resolved instanceof SimpleValueInfo) {
          return new ArrayPropertyInfo<SimpleValueInfo>(name, (SimpleValueInfo)resolved, getter, setter);
        }
      }
    }
    return null;
  }

}
