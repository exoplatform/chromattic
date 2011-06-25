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

package org.chromattic.metamodel.mapping;

import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueInfo;
import org.chromattic.metamodel.bean.ValueKind;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PropertyMapping<P extends PropertyInfo<V, K>, V extends ValueInfo, K extends ValueKind> {

  /** . */
  BeanMapping owner;

  /** The optional parent. */
  PropertyMapping parent;

  /** . */
  final P property;

  public PropertyMapping(P property) {
    this.property = property;
  }

  public PropertyMapping getParent() {
    return parent;
  }

  public BeanMapping getOwner() {
    return owner;
  }

  public String getName() {
    return property.getName();
  }

  public P getProperty() {
    return property;
  }

  public V getValue() {
    return property.getValue();
  }

  public abstract void accept(MappingVisitor visitor);

  /**
   * Returns true if the property type is covariant, meaning that it redefines the type from an ancestor
   * with a subclass.
   *
   * @return true if the property is type covariant
   */
  public abstract boolean isTypeCovariant();
}
