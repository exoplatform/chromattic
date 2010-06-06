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

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyTypeResolver {

  /** . */
  static final TypeResolver<Type> typeDomain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  private static final PropertyTypeResolver base;

  static {

    // The base mappings
    PropertyTypeResolver _base = new PropertyTypeResolver(new HashMap<TypeInfo, PropertyTypeEntry>());
    _base.add(SimpleTypeProviders.INTEGER.class);
    _base.add(SimpleTypeProviders.LONG.class);
    _base.add(SimpleTypeProviders.BOOLEAN.class);
    _base.add(SimpleTypeProviders.FLOAT.class);
    _base.add(SimpleTypeProviders.DOUBLE.class);
    _base.add(SimpleTypeProviders.STRING.class);
    _base.add(SimpleTypeProviders.BINARY.class);
    _base.add(SimpleTypeProviders.BYTE_ARRAY.class);
    _base.add(SimpleTypeProviders.DATE.class);

    //
    base = _base;
  }

  /** . */
  private final Map<TypeInfo, PropertyTypeEntry> typeMappings;

  public PropertyTypeResolver(Map<TypeInfo, PropertyTypeEntry> typeMappings) {
    this.typeMappings = new HashMap<TypeInfo, PropertyTypeEntry>(typeMappings);
  }

  public PropertyTypeResolver() {
    this(base.typeMappings);
  }

  public PropertyMetaType<?> resolveJCRPropertyType(TypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.getJCRPropertyType() : null;
  }

  public SimpleTypeProvider<?, ?> resolveValueType(TypeInfo cti) {
    ValueTypeInfo vti = resolveType(cti);
    return vti != null ? vti.create() : null;
  }

  private synchronized <I, E> void add(Class<? extends SimpleTypeProvider<I, E>> provider) {
    ClassTypeInfo bilto = (ClassTypeInfo)typeDomain.resolve(provider);
    ValueTypeInfoImpl a = new ValueTypeInfoImpl(bilto);
    typeMappings.put(a.external, new PropertyTypeEntry(a));
  }

  private static final EnumMap<LiteralType, TypeInfo> aty;

  static {
    EnumMap<LiteralType, TypeInfo> _aty = new EnumMap<LiteralType, TypeInfo>(LiteralType.class);

    _aty.put(LiteralType.BOOLEAN, typeDomain.resolve(Boolean.class));
    _aty.put(LiteralType.INT, typeDomain.resolve(Integer.class));
    _aty.put(LiteralType.LONG, typeDomain.resolve(Long.class));
    _aty.put(LiteralType.FLOAT, typeDomain.resolve(Float.class));
    _aty.put(LiteralType.DOUBLE, typeDomain.resolve(Double.class));

    aty = _aty;
  }

  public synchronized ValueTypeInfo resolveType(TypeInfo typeInfo) {
    ValueTypeInfo jcrType = null;
    if (typeInfo instanceof ClassTypeInfo) {
      ClassTypeInfo cti = (ClassTypeInfo)typeInfo;
      if (cti.getKind() == ClassKind.ENUM) {
        jcrType = new EnumValueTypeInfo(cti);
      }
    }

    //
    if (jcrType == null) {
      if (typeInfo instanceof SimpleTypeInfo) {
        SimpleTypeInfo sti = (SimpleTypeInfo)typeInfo;
        if (sti.isPrimitive()) {
          typeInfo = aty.get(sti.getLiteralType());
        }
      }
      PropertyTypeEntry entry = typeMappings.get(typeInfo);
      if (entry != null) {
        jcrType = entry.getDefault();
      }
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
          ValueTypeInfoImpl vtii = new ValueTypeInfoImpl(abc);
          typeMappings.put(typeInfo, new PropertyTypeEntry(vtii));
          jcrType = vtii;
        }
      }
    }

    //
    return jcrType;
  }
}
