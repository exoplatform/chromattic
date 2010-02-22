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

package org.chromattic.metamodel.typegen;

import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleType;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;

import javax.jcr.PropertyType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyDefinition {

  PropertyDefinition(String name, boolean multiple, int type) {
    this.name = name;
    this.multiple = multiple;
    this.type = type;
    this.defaultValues = null;
  }

  <V> PropertyDefinition(JCRPropertyMapping<V> mapping, PropertyInfo<SimpleValueInfo<V>> propertyInfo) {

    //
    List<String> defaultValues = null;
    List<V> defaultValue = mapping.getDefaultValue();
    if (defaultValue != null) {
      defaultValues = new ArrayList<String>(defaultValue.size());
      for (V v : defaultValue) {
        String s = propertyInfo.getValue().getSimpleType().toString(v);
        defaultValues.add(s);
      }
      defaultValues = Collections.unmodifiableList(defaultValues);
    }

    //
    int propertyType;
    SimpleValueInfo simpleValueInfo = propertyInfo.getValue();
    SimpleType stk = simpleValueInfo.getSimpleType();
    if (stk == SimpleType.STRING) {
      propertyType = PropertyType.STRING;
    } else if (stk == SimpleType.LONG || stk ==SimpleType.PRIMITIVE_LONG) {
      propertyType = PropertyType.LONG;
    } else if (stk == SimpleType.PATH) {
      propertyType = PropertyType.PATH;
    } else if (stk == SimpleType.DATE) {
      propertyType = PropertyType.DATE;
    } else if (stk == SimpleType.BOOLEAN || stk ==SimpleType.PRIMITIVE_BOOLEAN) {
      propertyType = PropertyType.BOOLEAN;
    } else if (stk == SimpleType.INTEGER || stk ==SimpleType.PRIMITIVE_INTEGER) {
      propertyType = PropertyType.LONG;
    } else if (stk == SimpleType.FLOAT || stk ==SimpleType.PRIMITIVE_FLOAT) {
      propertyType = PropertyType.DOUBLE;
    } else if (stk == SimpleType.DOUBLE || stk ==SimpleType.PRIMITIVE_DOUBLE) {
      propertyType = PropertyType.DOUBLE;
    } else if (stk == SimpleType.STREAM) {
      propertyType = PropertyType.BINARY;
    } else if (stk instanceof SimpleType.Enumerated) {
      propertyType = PropertyType.STRING;
    } else {
      throw new AssertionError();
    }

    //
    this.multiple = propertyInfo instanceof MultiValuedPropertyInfo;
    this.name = mapping.getName();
    this.type = propertyType;
    this.defaultValues = defaultValues;
  }

  final String name;

  final boolean multiple;

  final int type;

  final List<String> defaultValues;

  public List<String> getDefaultValues() {
    return defaultValues;
  }

  public String getName() {
    return name;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public int getType() {
    return type;
  }
}
