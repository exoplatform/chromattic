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
import org.chromattic.metatype.DataType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertiesMapping<V extends ValueInfo> extends PropertyMapping<PropertyInfo<V, ValueKind.Map>,V, ValueKind.Map> {

  /** . */
  private final DataType<?> metaType;

  /** . */
  private final ValueKind valueKind;

  /** . */
  private final String prefix;

  public PropertiesMapping(PropertyInfo<V, ValueKind.Map> property, String prefix, DataType<?> metaType, ValueKind valueKind) {
    super(property);

    //
    this.prefix = prefix;
    this.metaType = metaType;
    this.valueKind = valueKind;
  }

  public String getPrefix() {
    return prefix;
  }

  public DataType<?> getMetaType() {
    return metaType;
  }

  public ValueKind getValueKind() {
    return valueKind;
  }

  @Override
  public void accept(MappingVisitor visitor) {
    visitor.propertiesMapping(this);
  }

  public boolean isTypeCovariant() {
/*
    if (parent == null) {
      return true;
    } else {
      PropertiesMapping<?> a = null;
      return property.getValue().getBean() != a.property.getValue().getBean();
    }
*/
    // Implement that properly based on the type of "*"
    return true;
  }
}
