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
import org.chromattic.spi.type.SimpleValueTypes;
import org.chromattic.spi.type.ValueType;
import org.reflext.api.ClassKind;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
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
  static final TypeResolver<Type> typeDomain = TypeResolverImpl.create(new JavaLangReflectReflectionModel());

  /** . */
  private static final Map<String, ValueTypeInfoImpl> defaultTypeMappings;

  /** . */
  private static final Map<JCRPropertyType<?>, ClassTypeInfo> jcrTypes;

  static {

    // The base mappings
    Map<String, ValueTypeInfoImpl> _typeMapping = new HashMap<String, ValueTypeInfoImpl>();
    _typeMapping.put(int.class.getName(), new ValueTypeInfoImpl<Long>(SimpleValueTypes.PRIMITIVE_INTEGER.class, JCRPropertyType.LONG));
    _typeMapping.put(Integer.class.getName(), new ValueTypeInfoImpl<Long>(SimpleValueTypes.INTEGER.class, JCRPropertyType.LONG));
    _typeMapping.put(long.class.getName(), new ValueTypeInfoImpl<Long>(SimpleValueTypes.PRIMITIVE_LONG.class, JCRPropertyType.LONG));
    _typeMapping.put(Long.class.getName(), new ValueTypeInfoImpl<Long>(SimpleValueTypes.LONG.class, JCRPropertyType.LONG));
    _typeMapping.put(boolean.class.getName(), new ValueTypeInfoImpl<Boolean>(SimpleValueTypes.PRIMITIVE_BOOLEAN.class, JCRPropertyType.BOOLEAN));
    _typeMapping.put(Boolean.class.getName(), new ValueTypeInfoImpl<Boolean>(SimpleValueTypes.BOOLEAN.class, JCRPropertyType.BOOLEAN));
    _typeMapping.put(float.class.getName(), new ValueTypeInfoImpl<Double>(SimpleValueTypes.PRIMITIVE_FLOAT.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(Float.class.getName(), new ValueTypeInfoImpl<Double>(SimpleValueTypes.FLOAT.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(double.class.getName(), new ValueTypeInfoImpl<Double>(SimpleValueTypes.PRIMITIVE_DOUBLE.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(Double.class.getName(), new ValueTypeInfoImpl<Double>(SimpleValueTypes.DOUBLE.class, JCRPropertyType.DOUBLE));
    _typeMapping.put(String.class.getName(), new ValueTypeInfoImpl<String>(SimpleValueTypes.STRING.class, JCRPropertyType.STRING));
    _typeMapping.put(InputStream.class.getName(), new ValueTypeInfoImpl<InputStream>(SimpleValueTypes.BINARY.class, JCRPropertyType.BINARY));
    _typeMapping.put(Date.class.getName(), new ValueTypeInfoImpl<Calendar>(SimpleValueTypes.DATE.class, JCRPropertyType.DATE));

    //
    _typeMapping.put("byte", new ValueTypeInfoImpl<InputStream>(SimpleValueTypes.BYTE_ARRAY.class, JCRPropertyType.BINARY));

    //
    Map<JCRPropertyType<?>, ClassTypeInfo> _jcrTypes = new HashMap<JCRPropertyType<?>, ClassTypeInfo>();
    _jcrTypes.put(JCRPropertyType.STRING, (ClassTypeInfo)typeDomain.resolve(ValueType.STRING.class));
    _jcrTypes.put(JCRPropertyType.PATH, (ClassTypeInfo)typeDomain.resolve(ValueType.PATH.class));
    _jcrTypes.put(JCRPropertyType.NAME, (ClassTypeInfo)typeDomain.resolve(ValueType.NAME.class));
    _jcrTypes.put(JCRPropertyType.LONG, (ClassTypeInfo)typeDomain.resolve(ValueType.LONG.class));
    _jcrTypes.put(JCRPropertyType.DOUBLE, (ClassTypeInfo)typeDomain.resolve(ValueType.DOUBLE.class));
    _jcrTypes.put(JCRPropertyType.BOOLEAN, (ClassTypeInfo)typeDomain.resolve(ValueType.BOOLEAN.class));
    _jcrTypes.put(JCRPropertyType.BINARY, (ClassTypeInfo)typeDomain.resolve(ValueType.BINARY.class));
    _jcrTypes.put(JCRPropertyType.DATE, (ClassTypeInfo)typeDomain.resolve(ValueType.DATE.class));


    defaultTypeMappings = _typeMapping;
    jcrTypes = _jcrTypes;
  }

  /** . */
  private final Map<String, ValueTypeInfoImpl> typeMappings;

  public PropertyTypeResolver() {
    typeMappings = new HashMap<String, ValueTypeInfoImpl>(defaultTypeMappings);
  }

  public JCRPropertyType<?> resolveJCRPropertyType(ClassTypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.getJCRPropertyType() : null;
  }

  public ValueType<?, ?> resolveValueType(ClassTypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.create() : null;
  }

  ValueTypeInfo resolveType(ClassTypeInfo typeInfo) {
    ValueTypeInfo jcrType;
    if (typeInfo.getKind() == ClassKind.ENUM) {
      jcrType = new EnumeratedValueTypeInfo(typeInfo);
    } else {
      return typeMappings.get(typeInfo.getName());
    }

    //
    return jcrType;
  }
}
