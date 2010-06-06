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
import org.chromattic.spi.type.SimpleTypeProvider;
import org.reflext.api.TypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * This code is synchronized. Normally it should not have performance impact on runtime, i.e
 * this should not be used at runtime and the result should be cached somewhere in the runtime layer.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PropertyTypeEntry {

  /** . */
  private final ValueTypeInfoImpl<?> defaultValueTypeInfo;

  /** . */
  private final Map<PropertyMetaType<?>, ValueTypeInfoImpl<?>> metaTypeMapping = new HashMap<PropertyMetaType<?>, ValueTypeInfoImpl<?>>();

  <E, I> PropertyTypeEntry(Class<? extends SimpleTypeProvider<I, E>> provider, PropertyMetaType<I> propertyMT) {
    ValueTypeInfoImpl<I> vti = new ValueTypeInfoImpl<I>(provider, propertyMT);

    //
    this.defaultValueTypeInfo = vti;
    this.metaTypeMapping.put(propertyMT, vti);
  }

  PropertyTypeEntry(ValueTypeInfoImpl<?> defaultValueTypeInfo) {
    this.defaultValueTypeInfo = add(defaultValueTypeInfo);
    this.metaTypeMapping.put(defaultValueTypeInfo.getJCRPropertyType(), defaultValueTypeInfo);
  }

  public ValueTypeInfoImpl<?> getDefault() {
    return defaultValueTypeInfo;
  }

  public synchronized <E, I> ValueTypeInfoImpl<I> add(Class<E> classType, Class<? extends SimpleTypeProvider<I, E>> provider, PropertyMetaType<I> propertyMT) {
    TypeInfo typeInfo = PropertyTypeResolver.typeDomain.resolve(classType);
    if (!typeInfo.equals(defaultValueTypeInfo.typeInfo)) {
      throw new IllegalArgumentException();
    }
    ValueTypeInfoImpl<I> vti = new ValueTypeInfoImpl<I>(provider, propertyMT);
    metaTypeMapping.put(propertyMT, vti);
    return vti;
  }

  public synchronized <I> ValueTypeInfoImpl<I> add(ValueTypeInfoImpl<I> valueType) {
    metaTypeMapping.put(valueType.getJCRPropertyType(), valueType);
    return valueType;
  }

  public synchronized <I> ValueTypeInfoImpl<I> get(PropertyMetaType<I> propertyMT) {
    return (ValueTypeInfoImpl<I>)metaTypeMapping.get(propertyMT);
  }

  public synchronized ValueTypeInfoImpl<?> resolve(PropertyMetaType<?> propertyMT) {
    ValueTypeInfoImpl<?> valueTypeInfo = metaTypeMapping.get(propertyMT);
    if (valueTypeInfo == null) {
      valueTypeInfo = defaultValueTypeInfo;
    }
    return valueTypeInfo;
  }
}
