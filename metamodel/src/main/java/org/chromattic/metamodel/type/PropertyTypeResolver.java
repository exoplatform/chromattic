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

package org.chromattic.metamodel.type;

import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.spi.type.SimpleType;
import org.chromattic.spi.type.SimpleTypeProvider;
import org.reflext.api.*;
import org.reflext.api.annotation.AnnotationInfo;
import org.reflext.api.annotation.AnnotationParameterInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyTypeResolver {

  /** . */
  static final TypeResolver<Type> typeDomain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private static final Map<TypeInfo, ValueTypeInfoImpl> defaultTypeMappings;

  /** . */
  private static final Map<ClassTypeInfo, PropertyMetaType<?>> propertyMetaTypes;

  static {

    // The base mappings
    Map<TypeInfo, ValueTypeInfoImpl> _typeMapping = new HashMap<TypeInfo, ValueTypeInfoImpl>();
    _typeMapping.put(typeDomain.resolve(int.class), new ValueTypeInfoImpl<Long>(SimpleTypeProviders.PRIMITIVE_INTEGER.class, PropertyMetaType.LONG));
    _typeMapping.put(typeDomain.resolve(Integer.class), new ValueTypeInfoImpl<Long>(SimpleTypeProviders.INTEGER.class, PropertyMetaType.LONG));
    _typeMapping.put(typeDomain.resolve(long.class), new ValueTypeInfoImpl<Long>(SimpleTypeProviders.PRIMITIVE_LONG.class, PropertyMetaType.LONG));
    _typeMapping.put(typeDomain.resolve(Long.class), new ValueTypeInfoImpl<Long>(SimpleTypeProviders.LONG.class, PropertyMetaType.LONG));
    _typeMapping.put(typeDomain.resolve(boolean.class), new ValueTypeInfoImpl<Boolean>(SimpleTypeProviders.PRIMITIVE_BOOLEAN.class, PropertyMetaType.BOOLEAN));
    _typeMapping.put(typeDomain.resolve(Boolean.class), new ValueTypeInfoImpl<Boolean>(SimpleTypeProviders.BOOLEAN.class, PropertyMetaType.BOOLEAN));
    _typeMapping.put(typeDomain.resolve(float.class), new ValueTypeInfoImpl<Double>(SimpleTypeProviders.PRIMITIVE_FLOAT.class, PropertyMetaType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(Float.class), new ValueTypeInfoImpl<Double>(SimpleTypeProviders.FLOAT.class, PropertyMetaType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(double.class), new ValueTypeInfoImpl<Double>(SimpleTypeProviders.PRIMITIVE_DOUBLE.class, PropertyMetaType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(Double.class), new ValueTypeInfoImpl<Double>(SimpleTypeProviders.DOUBLE.class, PropertyMetaType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(String.class), new ValueTypeInfoImpl<String>(SimpleTypeProviders.STRING.class, PropertyMetaType.STRING));
    _typeMapping.put(typeDomain.resolve(InputStream.class), new ValueTypeInfoImpl<InputStream>(SimpleTypeProviders.BINARY.class, PropertyMetaType.BINARY));
    _typeMapping.put(typeDomain.resolve(byte[].class), new ValueTypeInfoImpl<InputStream>(SimpleTypeProviders.BYTE_ARRAY.class, PropertyMetaType.BINARY));
    _typeMapping.put(typeDomain.resolve(Date.class), new ValueTypeInfoImpl<Calendar>(SimpleTypeProviders.DATE.class, PropertyMetaType.DATE));

    //
    Map<ClassTypeInfo, PropertyMetaType<?>> _jcrTypes = new HashMap<ClassTypeInfo, PropertyMetaType<?>>();
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.STRING.class), PropertyMetaType.STRING);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.PATH.class), PropertyMetaType.PATH);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.NAME.class), PropertyMetaType.NAME);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.LONG.class), PropertyMetaType.LONG);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.DOUBLE.class), PropertyMetaType.DOUBLE);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.BOOLEAN.class), PropertyMetaType.BOOLEAN);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.BINARY.class), PropertyMetaType.BINARY);
    _jcrTypes.put((ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.DATE.class), PropertyMetaType.DATE);

    //
    defaultTypeMappings = _typeMapping;
    propertyMetaTypes = _jcrTypes;
  }

  /** . */
  private final Map<TypeInfo, ValueTypeInfoImpl> typeMappings;

  public PropertyTypeResolver() {
    typeMappings = new HashMap<TypeInfo, ValueTypeInfoImpl>(defaultTypeMappings);
  }

  public PropertyMetaType<?> resolveJCRPropertyType(TypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.getJCRPropertyType() : null;
  }

  public SimpleTypeProvider<?, ?> resolveValueType(TypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.create() : null;
  }

  public ValueTypeInfo resolveType(TypeInfo typeInfo) {
    ValueTypeInfo jcrType;
    if (typeInfo instanceof ClassTypeInfo) {
      ClassTypeInfo cti = (ClassTypeInfo)typeInfo;
      if (cti.getKind() == ClassKind.ENUM) {
        jcrType = new EnumValueTypeInfo(cti);
      } else {
        jcrType = typeMappings.get(cti);
      }
    } else {
      jcrType = typeMappings.get(typeInfo);
    }

    //
    if (jcrType == null) {
      if (typeInfo instanceof ClassTypeInfo) {
        ClassTypeInfo cti = (ClassTypeInfo)typeInfo;
        AnnotationType<AnnotationInfo, ClassTypeInfo> at = AnnotationType.get((ClassTypeInfo)typeDomain.resolve(SimpleType.class));
        AnnotationInfo ai = cti.getDeclaredAnnotation(at);
        if (ai != null) {
          AnnotationParameterInfo param = ai.getParameter("value");
          ClassTypeInfo abc = (ClassTypeInfo)param.getValue();

          // Find the right subclass
          ClassTypeInfo current = abc;
          while (!current.getSuperClass().getName().equals(SimpleTypeProvider.class.getName())) {
            current = current.getSuperClass();
          }
          PropertyMetaType aaaaa = propertyMetaTypes.get(current);

          //
          ClassTypeInfo stp = (ClassTypeInfo)typeDomain.resolve(SimpleTypeProvider.class);
          TypeVariableInfo tvi = stp.getTypeParameters().get(1); // <E>
          ClassTypeInfo aaa = (ClassTypeInfo)abc.resolve(tvi);
          if (!aaa.equals(typeInfo)) {
            throw new AssertionError(aaa + " should be equals to " + typeInfo);
          }

          //
          ValueTypeInfoImpl vtii = new ValueTypeInfoImpl(abc, aaaaa);
          typeMappings.put(typeInfo, vtii);
          jcrType = vtii;
        }
      }
    }

    //
    return jcrType;
  }
}
