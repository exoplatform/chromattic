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

import org.chromattic.metatype.DataType;
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
 * This code is synchronized. Normally it should not have performance impact on runtime, i.e
 * this should not be used at runtime and the result should be cached somewhere in the runtime layer.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleTypeResolver {

  /** . */
  static final TypeResolver<Type> typeDomain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private static final SimpleTypeResolver base;

  /** . */
  private static final EnumMap<LiteralType, TypeInfo> literalWrappers;

  static {

    // The base mappings
    SimpleTypeResolver _base = new SimpleTypeResolver(new HashMap<TypeInfo, PropertyTypeEntry>());

    // Numeric
    _base.add(SimpleTypeProviders.INTEGER.class);
    _base.add(SimpleTypeProviders.LONG.class);
    _base.add(SimpleTypeProviders.BOOLEAN.class);
    _base.add(SimpleTypeProviders.FLOAT.class);
    _base.add(SimpleTypeProviders.DOUBLE.class);

    // String
    _base.add(SimpleTypeProviders.STRING.class);

    // Path
    _base.add(SimpleTypeProviders.PATH.class);

    // Name
    _base.add(SimpleTypeProviders.NAME.class);

    // Binary
    _base.add(SimpleTypeProviders.BINARY.class);
    _base.add(SimpleTypeProviders.BYTE_ARRAY.class);

    // Date
    _base.add(SimpleTypeProviders.DATE.class);
    _base.add(SimpleTypeProviders.CALENDAR.class);
    _base.add(SimpleTypeProviders.TIMESTAMP.class);

    // Primitive unwrapping
    EnumMap<LiteralType, TypeInfo> _literalWrappers = new EnumMap<LiteralType, TypeInfo>(LiteralType.class);
    _literalWrappers.put(LiteralType.BOOLEAN, typeDomain.resolve(Boolean.class));
    _literalWrappers.put(LiteralType.INT, typeDomain.resolve(Integer.class));
    _literalWrappers.put(LiteralType.LONG, typeDomain.resolve(Long.class));
    _literalWrappers.put(LiteralType.FLOAT, typeDomain.resolve(Float.class));
    _literalWrappers.put(LiteralType.DOUBLE, typeDomain.resolve(Double.class));

    //
    base = _base;
    literalWrappers = _literalWrappers;
  }

  /** . */
  private final Map<TypeInfo, PropertyTypeEntry> typeMappings;

  private SimpleTypeResolver(Map<TypeInfo, PropertyTypeEntry> typeMappings) {
    this.typeMappings = typeMappings;
  }

  /**
   * The default constructor.
   */
  public SimpleTypeResolver() {
    this(base);
  }

  /**
   * Deep clone constructor.
   *
   * @param that that resolver to clone
   */
  public SimpleTypeResolver(SimpleTypeResolver that) {
    if (that == null) {
      throw new NullPointerException();
    }

    //
    HashMap<TypeInfo, PropertyTypeEntry> typeMappings = new HashMap<TypeInfo, PropertyTypeEntry>();
    for (Map.Entry<TypeInfo, PropertyTypeEntry> entry : that.typeMappings.entrySet()) {
      typeMappings.put(entry.getKey(), new PropertyTypeEntry(entry.getValue()));
    }

    //
    this.typeMappings = typeMappings;
  }

  private synchronized <I, E> void add(Class<? extends SimpleTypeProvider<I, E>> provider) {
    ClassTypeInfo bilto = (ClassTypeInfo)typeDomain.resolve(provider);
    SimpleTypeMappingImpl<I> a = new SimpleTypeMappingImpl<I>(bilto);
    PropertyTypeEntry existing = typeMappings.get(a.external);
    if (existing == null) {
      typeMappings.put(a.external, new PropertyTypeEntry(a));
    } else {
      existing.add(a);
    }
  }

  public synchronized SimpleTypeMapping resolveType(TypeInfo typeInfo) {
    return resolveType(typeInfo, null);
  }

  public synchronized SimpleTypeMapping resolveType(
    TypeInfo typeInfo,
    DataType<?> propertyMT) {
    SimpleTypeMapping jcrType = null;
    if (typeInfo instanceof ClassTypeInfo) {
      ClassTypeInfo cti = (ClassTypeInfo)typeInfo;
      if (cti.getKind() == ClassKind.ENUM) {
        jcrType = new EnumSimpleTypeMapping(cti);
      }
    }

    //
    if (jcrType == null) {
      if (typeInfo instanceof org.reflext.api.SimpleTypeInfo) {
        org.reflext.api.SimpleTypeInfo sti = (org.reflext.api.SimpleTypeInfo)typeInfo;
        if (sti.isPrimitive()) {
          typeInfo = literalWrappers.get(sti.getLiteralType());
        }
      }
      PropertyTypeEntry entry = typeMappings.get(typeInfo);
      if (entry != null) {
        if (propertyMT != null) {
          jcrType = entry.get(propertyMT);
        } else {
          jcrType = entry.getDefault();
        }
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
          SimpleTypeMappingImpl vtii = new SimpleTypeMappingImpl(abc);
          if (propertyMT != null && propertyMT != vtii.getPropertyMetaType()) {
            throw new UnsupportedOperationException("todo " + vtii.getPropertyMetaType() + " " + propertyMT);
          }
          typeMappings.put(typeInfo, new PropertyTypeEntry(vtii));
          jcrType = vtii;
        }
      }
    }

    //
    return jcrType;
  }
}
