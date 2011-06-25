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

import org.chromattic.metamodel.bean.*;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueMapping<K extends ValueKind>
    extends PropertyMapping<PropertyInfo<SimpleValueInfo<K>, ValueKind.Single>, SimpleValueInfo<K>, ValueKind.Single> {

  /** . */
  final PropertyDefinitionMapping<?> propertyDefinition;

  public ValueMapping(PropertyInfo<SimpleValueInfo<K>, ValueKind.Single> property, PropertyDefinitionMapping propertyDefinition) {
    super(property);

    //
    this.propertyDefinition = propertyDefinition;
  }

  public boolean isTypeCovariant() {
    if (parent == null) {
      return true;
    } else {
      ValueMapping<?> a = (ValueMapping<?>)parent;
      return propertyDefinition.getMetaType() != a.propertyDefinition.getMetaType();
    }
  }

  public PropertyDefinitionMapping<?> getPropertyDefinition() {
    return propertyDefinition;
  }

  @Override
  public void accept(MappingVisitor visitor) {
    if (property.getValueKind() == ValueKind.SINGLE) {
      visitor.singleValueMapping((ValueMapping<ValueKind.Single>)this);
    } else {
      visitor.multiValueMapping((ValueMapping<ValueKind.Multi>)this);
    }
  }
}
