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

package org.chromattic.core.mapper.property;

import org.chromattic.core.ArrayType;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metatype.DataType;
import org.chromattic.metamodel.mapping.ValueMapping;
import org.chromattic.spi.type.SimpleTypeProvider;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyMultiValuedPropertyMapper<O extends ObjectContext<O>, E, I, K extends ValueKind.Multi>
  extends PropertyMapper<PropertyInfo<SimpleValueInfo<K>, ValueKind.Single>, SimpleValueInfo<K>, O, ValueKind.Single> {

  /** . */
  private final String jcrPropertyName;

  /** . */
  private final ArrayType<?, E> arrayType;

  /** . */
  private final SimpleValueInfo elementType;

  /** . */
  private final ValueDefinition<I, E> vt;

  public JCRPropertyMultiValuedPropertyMapper(
      Class<O> contextType,
      SimpleTypeProvider<I, E> vt,
      ValueMapping<K> info) {
    super(contextType, info);

    //
    Class effective = (Class) info.getValue().getEffectiveType().unwrap();

    //
    ArrayType<?, E> arrayType;
    ValueKind.Multi valueKind = info.getValue().getValueKind();
    if (valueKind == ValueKind.ARRAY) {
      if (effective.isPrimitive()) {
        arrayType = ArrayType.primitiveArray(effective);
      } else {
        arrayType = ArrayType.array(effective);
      }
    } else if (valueKind == ValueKind.LIST) {
      arrayType = ArrayType.list(effective);
    } else {
      throw new AssertionError();
    }

    //
    this.arrayType = arrayType;
    this.jcrPropertyName = info.getPropertyDefinition().getName();
    this.elementType = info.getValue();
    this.vt = new ValueDefinition<I, E>(effective, (DataType<I>)info.getPropertyDefinition().getMetaType(), vt, info.getPropertyDefinition().getDefaultValue());
  }

  @Override
  public Object get(O context) throws Throwable {
    return context.getPropertyValues(jcrPropertyName, vt, arrayType);
  }

  @Override
  public void set(O context, Object value) throws Throwable {
    context.setPropertyValues(jcrPropertyName, vt, (ArrayType)arrayType, value);
  }
}