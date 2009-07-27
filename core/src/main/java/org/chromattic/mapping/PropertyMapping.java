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
package org.chromattic.mapping;

import org.chromattic.bean.PropertyInfo;
import org.chromattic.mapping.value.ValueMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMapping<V extends ValueMapping> {

  /** . */
  private final PropertyInfo info;

  /** . */
  private final V valueMapping;

  PropertyMapping(PropertyInfo info, V valueMapping) {
    this.info = info;
    this.valueMapping = valueMapping;
  }

  public String getName() {
    return info.getName();
  }

  public PropertyInfo getInfo() {
    return info;
  }

  public V getValueMapping() {
    return valueMapping;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PropertyMapping) {
      PropertyMapping that = (PropertyMapping)obj;
      return info.getName().equals(that.info.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return info.getName().hashCode();
  }
}