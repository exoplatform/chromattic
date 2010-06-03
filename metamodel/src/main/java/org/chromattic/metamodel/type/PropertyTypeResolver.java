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

import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.spi.type.SimpleTypeConverter;
import org.reflext.api.*;
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

  //

  /** . */
  private static final Map<TypeInfo, ValueTypeInfoImpl> defaultTypeMappings;

  /** . */
  private static final Map<JCRPropertyType<?>, ClassTypeInfo> jcrTypes;

  static {

    // The base mappings
    Map<TypeInfo, ValueTypeInfoImpl> _typeMapping = new HashMap<TypeInfo, ValueTypeInfoImpl>();
    _typeMapping.put(typeDomain.resolve(int.class), new ValueTypeInfoImpl<Long>(SimpleTypeConverters.PRIMITIVE_INTEGER.class, JCRPropertyType.LONG));
    _typeMapping.put(typeDomain.resolve(Integer.class), new ValueTypeInfoImpl<Long>(SimpleTypeConverters.INTEGER.class, JCRPropertyType.LONG));
    _typeMapping.put(typeDomain.resolve(long.class), new ValueTypeInfoImpl<Long>(SimpleTypeConverters.PRIMITIVE_LONG.class, JCRPropertyType.LONG));
    _typeMapping.put(typeDomain.resolve(Long.class), new ValueTypeInfoImpl<Long>(SimpleTypeConverters.LONG.class, JCRPropertyType.LONG));
    _typeMapping.put(typeDomain.resolve(boolean.class), new ValueTypeInfoImpl<Boolean>(SimpleTypeConverters.PRIMITIVE_BOOLEAN.class, JCRPropertyType.BOOLEAN));
    _typeMapping.put(typeDomain.resolve(Boolean.class), new ValueTypeInfoImpl<Boolean>(SimpleTypeConverters.BOOLEAN.class, JCRPropertyType.BOOLEAN));
    _typeMapping.put(typeDomain.resolve(float.class), new ValueTypeInfoImpl<Double>(SimpleTypeConverters.PRIMITIVE_FLOAT.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(Float.class), new ValueTypeInfoImpl<Double>(SimpleTypeConverters.FLOAT.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(double.class), new ValueTypeInfoImpl<Double>(SimpleTypeConverters.PRIMITIVE_DOUBLE.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(Double.class), new ValueTypeInfoImpl<Double>(SimpleTypeConverters.DOUBLE.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(typeDomain.resolve(String.class), new ValueTypeInfoImpl<String>(SimpleTypeConverters.STRING.class, JCRPropertyType.STRING));
    _typeMapping.put(typeDomain.resolve(InputStream.class), new ValueTypeInfoImpl<InputStream>(SimpleTypeConverters.BINARY.class, JCRPropertyType.BINARY));
    _typeMapping.put(typeDomain.resolve(Date.class), new ValueTypeInfoImpl<Calendar>(SimpleTypeConverters.DATE.class, JCRPropertyType.DATE));
    _typeMapping.put(typeDomain.resolve(byte[].class), new ValueTypeInfoImpl<InputStream>(SimpleTypeConverters.BYTE_ARRAY.class, JCRPropertyType.BINARY));

    //
    Map<JCRPropertyType<?>, ClassTypeInfo> _jcrTypes = new HashMap<JCRPropertyType<?>, ClassTypeInfo>();
    _jcrTypes.put(JCRPropertyType.STRING, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.STRING.class));
    _jcrTypes.put(JCRPropertyType.PATH, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.PATH.class));
    _jcrTypes.put(JCRPropertyType.NAME, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.NAME.class));
    _jcrTypes.put(JCRPropertyType.LONG, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.LONG.class));
    _jcrTypes.put(JCRPropertyType.DOUBLE, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.DOUBLE.class));
    _jcrTypes.put(JCRPropertyType.BOOLEAN, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.BOOLEAN.class));
    _jcrTypes.put(JCRPropertyType.BINARY, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.BINARY.class));
    _jcrTypes.put(JCRPropertyType.DATE, (ClassTypeInfo)typeDomain.resolve(SimpleTypeConverter.DATE.class));


    defaultTypeMappings = _typeMapping;
    jcrTypes = _jcrTypes;
  }

  /** . */
  private final Map<TypeInfo, ValueTypeInfoImpl> typeMappings;

  public PropertyTypeResolver() {
    typeMappings = new HashMap<TypeInfo, ValueTypeInfoImpl>(defaultTypeMappings);
  }

  public JCRPropertyType<?> resolveJCRPropertyType(TypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.getJCRPropertyType() : null;
  }

  public SimpleTypeConverter<?, ?> resolveValueType(TypeInfo cti) {
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
    return jcrType;
  }
}
