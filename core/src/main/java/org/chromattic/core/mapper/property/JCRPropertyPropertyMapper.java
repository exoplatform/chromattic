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
import org.chromattic.core.vt2.ValueTypeFactory;
import org.chromattic.metamodel.bean.SingleValuedPropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.spi.type.ValueType;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyPropertyMapper<O extends ObjectContext, E, I> extends PropertyMapper<SingleValuedPropertyInfo<SimpleValueInfo>, O> {

  /** . */
  private final String jcrPropertyName;

  /** . */
  private final ValueDefinition<I, E> vt;

  public JCRPropertyPropertyMapper(
    Class<O> contextType,
    SingleValuedPropertyInfo<SimpleValueInfo> info,
    String jcrPropertyName,
    List<String> defaultValue,
    JCRPropertyType<I> jcrType) {
    super(contextType, info);

    // YES IT'S UGLY BUT FOR NOW IT'S OK
    ValueType<I, E> vt = (ValueType<I,E>)ValueTypeFactory.create(info.getValue().getTypeInfo(), jcrType);

    //
    this.jcrPropertyName = jcrPropertyName;
    this.vt = new ValueDefinition<I, E>((Class)info.getValue().getTypeInfo().getType(), jcrType, vt, defaultValue);
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
    V v = javaType.cast(o);
    context.setPropertyValue(jcrPropertyName, vt, v);
  }
}
