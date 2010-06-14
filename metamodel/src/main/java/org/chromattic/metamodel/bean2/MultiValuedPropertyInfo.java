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

import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MultiValuedPropertyInfo<V extends ValueInfo> extends PropertyInfo<V> {

  /** . */
  private final MultiValueKind kind;

  public MultiValuedPropertyInfo(BeanInfo bean, PropertyInfo parent, String name, MethodInfo getter, MethodInfo setter, MultiValueKind kind, V value) {
    super(bean, parent, name, getter, setter, value);

    //
    this.kind = kind;
  }

  public MultiValueKind getKind() {
    return kind;
  }
}
