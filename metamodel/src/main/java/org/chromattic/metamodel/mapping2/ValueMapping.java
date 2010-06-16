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

package org.chromattic.metamodel.mapping2;

import org.chromattic.metamodel.bean2.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean2.PropertyInfo;
import org.chromattic.metamodel.bean2.SimpleValueInfo;
import org.chromattic.metamodel.bean2.SingleValuedPropertyInfo;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueMapping<P extends PropertyInfo<SimpleValueInfo>> extends PropertyMapping<P, SimpleValueInfo> {

  /** . */
  final PropertyDefinitionMapping propertyDefinition;

  public ValueMapping(P property, PropertyDefinitionMapping propertyDefinition) {
    super(property);

    //
    this.propertyDefinition = propertyDefinition;
  }

  public boolean isNew() {
    if (parent == null) {
      return true;
    } else {
      ValueMapping<?> a = null;
      return propertyDefinition.getMetaType() != a.propertyDefinition.getMetaType();
    }
  }

  public PropertyDefinitionMapping getPropertyDefinition() {
    return propertyDefinition;
  }

  public static class Single extends ValueMapping<SingleValuedPropertyInfo<SimpleValueInfo>> {
    public Single(SingleValuedPropertyInfo<SimpleValueInfo> property, PropertyDefinitionMapping propertyDefinition) {
      super(property, propertyDefinition);
    }

    @Override
    public void accept(MappingVisitor visitor) {
      visitor.singleValueMapping(this);
    }
  }

  public static class Multi extends ValueMapping<MultiValuedPropertyInfo<SimpleValueInfo>> {
    public Multi(MultiValuedPropertyInfo<SimpleValueInfo> property, PropertyDefinitionMapping propertyDefinition) {
      super(property, propertyDefinition);
    }

    @Override
    public void accept(MappingVisitor visitor) {
      visitor.multiValueMapping(this);
    }
  }

}
