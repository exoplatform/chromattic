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

import org.chromattic.metatype.ValueType;

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
  private final SimpleTypeMappingImpl<?> defaultValueTypeInfo;

  /** . */
  private final Map<ValueType<?>, SimpleTypeMappingImpl<?>> metaTypeMapping;

  PropertyTypeEntry(PropertyTypeEntry that) {
    this.defaultValueTypeInfo = that.defaultValueTypeInfo;
    this.metaTypeMapping = new HashMap<ValueType<?>, SimpleTypeMappingImpl<?>>(that.metaTypeMapping);
  }

  PropertyTypeEntry(SimpleTypeMappingImpl<?> defaultValueTypeInfo) {
    Map<ValueType<?>, SimpleTypeMappingImpl<?>> metaTypeMapping = new HashMap<ValueType<?>, SimpleTypeMappingImpl<?>>();
    metaTypeMapping.put(defaultValueTypeInfo.getPropertyMetaType(), defaultValueTypeInfo);

    //
    this.defaultValueTypeInfo = defaultValueTypeInfo;
    this.metaTypeMapping = metaTypeMapping;
  }

  public SimpleTypeMappingImpl<?> getDefault() {
    return defaultValueTypeInfo;
  }

  public synchronized <I> SimpleTypeMappingImpl<I> add(SimpleTypeMappingImpl<I> valueType) {
    if (!valueType.external.equals(defaultValueTypeInfo.external)) {
      throw new IllegalArgumentException("Was expecting those types to be equals " + valueType.external + " " + defaultValueTypeInfo.external);
    }
    metaTypeMapping.put(valueType.getPropertyMetaType(), valueType);
    return valueType;
  }

  public synchronized <I> SimpleTypeMappingImpl<I> get(ValueType<I> propertyMT) {
    return (SimpleTypeMappingImpl<I>)metaTypeMapping.get(propertyMT);
  }

  public synchronized SimpleTypeMappingImpl<?> resolve(ValueType<?> propertyMT) {
    SimpleTypeMappingImpl<?> valueTypeInfo = metaTypeMapping.get(propertyMT);
    if (valueTypeInfo == null) {
      valueTypeInfo = defaultValueTypeInfo;
    }
    return valueTypeInfo;
  }
}
