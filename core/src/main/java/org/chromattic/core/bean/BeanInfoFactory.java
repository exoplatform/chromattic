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
import org.chromattic.core.bean.SimpleTypeKind;
import org.chromattic.api.annotations.DefaultValue;
import org.chromattic.api.annotations.Path;
import org.reflext.api.*;
import org.reflext.api.introspection.HierarchyScope;
import org.reflext.api.introspection.MethodIntrospector;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfoFactory {

  /** . */
  private static final ObjectSimpleType<String> PATH = new ObjectSimpleType<String>(BaseSimpleTypes.PATH, String.class);

  /** . */
  private Map<String, SimpleTypeKind<?, ?>> types;

  public BeanInfoFactory(Map<String, SimpleTypeKind<?, ?>> types) {

    // Clone and add base types
    types = new HashMap<String, SimpleTypeKind<?,?>>(types);
    types.put(String.class.getName(), BaseSimpleTypes.STRING);
    types.put(Date.class.getName(), BaseSimpleTypes.DATE);
    types.put(InputStream.class.getName(), BaseSimpleTypes.STREAM);
    types.put(Boolean.class.getName(), BaseSimpleTypes.BOOLEAN);
    types.put(Integer.class.getName(), BaseSimpleTypes.INT);
    types.put(Long.class.getName(), BaseSimpleTypes.LONG);
    types.put(Float.class.getName(), BaseSimpleTypes.FLOAT);
    types.put(Double.class.getName(), BaseSimpleTypes.DOUBLE);

    //
    this.types = types;
  }

  public BeanInfoFactory() {
    this(new HashMap<String, SimpleTypeKind<?,?>>());
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

  private ValueInfo createValue(
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
      return createSimpleValueInfo(type, defaultValue);
    } else if (type.getName().equals(String.class.getName())) {
      AnnotationIntrospector<Path> intro = new AnnotationIntrospector<Path>(Path.class);
      if ((getter != null && intro.resolve(getter) != null ) || (setter != null && intro.resolve(setter) != null)) {
        return createPath(type);
      } else {
        return createSimpleValueInfo(type, defaultValue);
      }
    } else if (
      type.getName().equals(InputStream.class.getName()) ||
      type.getName().equals(Date.class.getName()) ||
      type.getKind() == ClassKind.ENUM) {
      return createSimpleValueInfo(type, defaultValue);
    } else {
      return new BeanValueInfo(type);
    }
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

  /**
   * Build a simple value info meta data.
   *
   * @param typeInfo the type info
   * @param defaultValue the default value
   * @return the simple value info
   * @throws BuilderException any exception that may prevent the correct building such as having a default value that
   *         does not match the type
   */
  private SimpleValueInfo<?> createSimpleValueInfo(ClassTypeInfo typeInfo, Annotation defaultValue) throws BuilderException {
    if (typeInfo == null) {
      throw new NullPointerException();
    }

    //
    if (typeInfo instanceof SimpleTypeInfo && ((SimpleTypeInfo)typeInfo).isPrimitive()) {
      switch (((SimpleTypeInfo)typeInfo).getLiteralType()) {
        case BOOLEAN: {
          List<Boolean> defaultBoolean = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Boolean) {
              defaultBoolean = Arrays.asList(((DefaultValue.Boolean)defaultValue).value());
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Boolean>(typeInfo, PrimitiveSimpleType.BOOLEAN, defaultBoolean);
        }
        case INT: {
          List<Integer> defaultInteger = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Int) {
              defaultInteger = Arrays.asList(((DefaultValue.Int)defaultValue).value());
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Integer>(typeInfo, PrimitiveSimpleType.INT, defaultInteger);
        }
        case LONG: {
          List<Long> defaultLong = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Long) {
              defaultLong = Arrays.asList(((DefaultValue.Long)defaultValue).value());
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Long>(typeInfo, PrimitiveSimpleType.LONG, defaultLong);
        }
        case FLOAT: {
          List<Float> defaultFloat = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Float) {
              defaultFloat = Arrays.asList(((DefaultValue.Float)defaultValue).value());
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Float>(typeInfo, PrimitiveSimpleType.FLOAT, defaultFloat);
        }
        case DOUBLE: {
          List<Double> defaultDouble = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Double) {
              defaultDouble = Arrays.asList(((DefaultValue.Double)defaultValue).value());
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Double>(typeInfo, PrimitiveSimpleType.DOUBLE, defaultDouble);
        }
        default:
          throw new AssertionError();
      }
    } else {
      if (defaultValue != null) {
        throw new BuilderException("Non primitive property cannot have a default value");
      }

      //
      SimpleTypeKind<?, ?> stk = null;
      switch (typeInfo.getKind()) {
        case CLASS:
        case INTERFACE:
          for (SimpleTypeKind<?, ?> entry : types.values()) {
            if (entry.getExternalType().getName().equals(typeInfo.getName())) {
              stk = entry;
              break;
            }
          }
          break;
        case ENUM:
          stk = new StringEnumTypeKind((Class)typeInfo.getType());
          break;
        case ANNOTATION:
          break;
      }

      if (stk == null) {
        throw new AssertionError();
      }

      //
      return foo(typeInfo, stk);
    }
  }

  private <E> SimpleValueInfo<E> foo(ClassTypeInfo typeInfo, SimpleTypeKind<E, ?> stk) {
    Class<E> a = stk.getExternalType();
    SimpleType<E> st = new ObjectSimpleType<E>(stk, a);
    return new SimpleValueInfo<E>(typeInfo, st, null);
  }

  private static SimpleValueInfo<String> createPath(ClassTypeInfo typeInfo) {
    if (typeInfo == null) {
      throw new NullPointerException();
    }
    if (typeInfo.getName().equals(String.class.getName())) {
      return new SimpleValueInfo<String>(typeInfo, PATH, null);
    } else {
      throw new IllegalArgumentException("Simple value of type path must have a type of " + String.class.getName());
    }
  }
}
