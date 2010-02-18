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
import org.chromattic.metamodel.bean.SingleValuedPropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.core.vt.ValueType;
import org.chromattic.core.vt.ValueTypeFactory;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyPropertyMapper<O extends ObjectContext> extends PropertyMapper<SingleValuedPropertyInfo<SimpleValueInfo>, O> {

  /** . */
  private final String jcrPropertyName;

  /** . */
  private final ValueType<O> vt;

  public JCRPropertyPropertyMapper(Class<O> contextType, SingleValuedPropertyInfo<SimpleValueInfo> info, String jcrPropertyName) {
    super(contextType, info);

    //
    this.jcrPropertyName = jcrPropertyName;
    this.vt = ValueTypeFactory.create(info.getValue());
  }

  @Override
  public Object get(O context) throws Throwable {
    return get(context, vt);
  }

  private <V> V get(O context, ValueType<V> d) throws Throwable {
    return context.getPropertyValue(jcrPropertyName, d);
  }

  @Override
  public void set(O context, Object o) throws Throwable {
    set(context, vt, o);
  }

  private <V> void set(O context, ValueType<V> vt, Object o) throws Throwable {
    Class<V> javaType = vt.getObjectType();
    V v = javaType.cast(o);
    context.setPropertyValue(jcrPropertyName, vt, v);
  }
}
