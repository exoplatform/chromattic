/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.bean2;

import java.beans.PropertyDescriptor;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PropertyMetaData {

  final String name;
  final Class<?> type;
  final AccessMode accessMode;

  PropertyMetaData(PropertyDescriptor pd) {
    this.name = pd.getName();
    this.type = pd.getPropertyType();
    this.accessMode = pd.getReadMethod() == null ? AccessMode.WRITE_ONLY : (pd.getWriteMethod() == null ? AccessMode.READ_ONLY : AccessMode.READ_WRITE);
  }

  PropertyMetaData(String name, Class<?> type, AccessMode accessMode) {
    this.name = name;
    this.type = type;
    this.accessMode = accessMode;
  }

  PropertyMetaData(PropertyInfo pi) {
    this.name = pi.getName();
    this.type = (Class<?>)pi.getValue().getDeclaredType().unwrap();
    this.accessMode = pi.getGetter() == null ? AccessMode.WRITE_ONLY : (pi.getSetter() == null ? AccessMode.READ_ONLY : AccessMode.READ_WRITE);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PropertyMetaData) {
      PropertyMetaData that = (PropertyMetaData)o;
      return this.name.equals(that.name) && this.type.equals(that.type) && this.accessMode.equals(that.accessMode);
    }
    return false;
  }

  @Override
  public String toString() {
    return "PropertyMetaData[name=" + name + ",type=" + type + ",accessMode=" + accessMode + "]";
  }
}
