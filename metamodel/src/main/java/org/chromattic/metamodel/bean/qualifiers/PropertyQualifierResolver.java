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

package org.chromattic.metamodel.bean.qualifiers;

import org.chromattic.api.BuilderException;
import org.chromattic.metamodel.bean.*;
import org.reflext.api.*;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyQualifierResolver {

  /** . */
  private final ClassTypeInfo beanType;

  public PropertyQualifierResolver(ClassTypeInfo beanType) {
    this.beanType = beanType;
  }

  public PropertyQualifier createPropertyInfo(
    ClassTypeInfo beanTypeInfo,
    PropertyInfo propertyInfo,
    TypeInfo typeInfo) {
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
              return new CollectionPropertyQualifier<ValueInfo>(propertyInfo, resolvedElementTI);
            } else {
              return new ListPropertyQualifier<ValueInfo>(propertyInfo, resolvedElementTI);
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
              return new MapPropertyQualifier<ValueInfo, ValueInfo>(propertyInfo, resolvedElementTI, resolvedKeyTI);
            }
          }
        }
      }
    } else if (resolvedTI instanceof ClassTypeInfo) {
      ValueInfo resolved = createValue((ClassTypeInfo)resolvedTI);
      return new SingleValuedPropertyQualifier<ValueInfo>(propertyInfo, resolved);
    } else if (resolvedTI instanceof ArrayTypeInfo) {
      TypeInfo componentTI = ((ArrayTypeInfo)resolvedTI).getComponentType();
      if (componentTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawComponentTI = (ClassTypeInfo)componentTI;

        if (rawComponentTI.getName().equals("byte")) {


          //
        } else {
          ValueInfo resolved = createValue(rawComponentTI);
          if (resolved instanceof SimpleValueInfo) {
            return new ArrayPropertyQualifier<SimpleValueInfo>(propertyInfo, (SimpleValueInfo)resolved);
          }
        }
      }
    }
    return null;
  }

  private ClassTypeInfo resolveClass(ClassTypeInfo baseType, TypeInfo type) {
    TypeInfo resolvedType = baseType.resolve(type);
    return resolvedType instanceof ClassTypeInfo ? (ClassTypeInfo)resolvedType : null;
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
