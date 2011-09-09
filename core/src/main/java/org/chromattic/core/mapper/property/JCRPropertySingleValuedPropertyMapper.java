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

import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.ValueMapping;
import org.chromattic.metatype.ValueType;
import org.chromattic.spi.type.SimpleTypeProvider;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertySingleValuedPropertyMapper<O extends ObjectContext<O>, E, I> extends PropertyMapper<PropertyInfo<SimpleValueInfo<ValueKind.Single>, ValueKind.Single>, SimpleValueInfo<ValueKind.Single>, O, ValueKind.Single> {

  /** . */
  private final String jcrPropertyName;

  /** . */
  private final ValueDefinition<I, E> vt;

  public JCRPropertySingleValuedPropertyMapper(
      Class<O> contextType,
      SimpleTypeProvider<I, E> vt,
      ValueMapping<ValueKind.Single> info) {
    super(contextType, info);

    //
    this.jcrPropertyName = info.getPropertyDefinition().getName();
    this.vt = new ValueDefinition<I, E>(
        (Class)info.getValue().getEffectiveType().unwrap(),
        (ValueType<I>)info.getPropertyDefinition().getMetaType(),
        vt,
        info.getPropertyDefinition().getDefaultValue());
  }

  @Override
  public Object get(O context) throws Throwable {
    return get(context, vt);
  }

  private <V> V get(O context, ValueDefinition<?, V> d) throws Throwable {
    return context.getPropertyValue(jcrPropertyName, d);
  }

  @Override
  public void set(O context, Object o) throws Throwable {
    set(context, vt, o);
  }

  private <V> void set(O context, ValueDefinition<?, V> vt, Object o) throws Throwable {
    Class<V> javaType = vt.getObjectType();
    if (o == null) {
      context.setPropertyValue(jcrPropertyName, vt, null);
    } else if (javaType.isInstance(o)) {
      V v = javaType.cast(o);
      context.setPropertyValue(jcrPropertyName, vt, v);
    } else {
      throw new ClassCastException("Cannot cast " + o.getClass().getName() + " to " + javaType.getName());
    }
  }
}