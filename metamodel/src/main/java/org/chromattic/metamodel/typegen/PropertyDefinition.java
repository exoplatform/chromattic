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
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;

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

  <V> PropertyDefinition(JCRPropertyMapping mapping, PropertyInfo<SimpleValueInfo> propertyInfo) {
    this.multiple = propertyInfo instanceof MultiValuedPropertyInfo;
    this.name = mapping.getName();
    this.type = mapping.getJCRType().getCode();
    this.defaultValues = mapping.getDefaultValue();
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
