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

import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.core.EntityContext;
import org.chromattic.core.bean.SingleValuedPropertyInfo;
import org.chromattic.core.bean.SimpleValueInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyPropertyMapper extends PropertyMapper<SingleValuedPropertyInfo<SimpleValueInfo>> {

  /** . */
  private final String jcrPropertyName;

  public JCRPropertyPropertyMapper(SingleValuedPropertyInfo<SimpleValueInfo> info, String jcrPropertyName) {
    super(info);

    //
    this.jcrPropertyName = jcrPropertyName;
  }

  @Override
  public Object get(EntityContext context) throws Throwable {
    SimpleValueInfo<?> simpleValueInfo = info.getValue();
    return get(context, simpleValueInfo);
  }

  private <V> V get(EntityContext context, SimpleValueInfo<V> d) throws Throwable {
    return context.getPropertyValue(jcrPropertyName, d);
  }

  @Override
  public void set(EntityContext context, Object o) throws Throwable {
    SimpleValueInfo<?> simpleValueInfo = info.getValue();
    set(context, simpleValueInfo, o);
  }

  private <V> void set(EntityContext context, SimpleValueInfo<V> simpleValueInfo, Object o) throws Throwable {
    Class<V> javaType = simpleValueInfo.getSimpleType().getJavaType();
    V v = javaType.cast(o);
    context.setPropertyValue(jcrPropertyName, simpleValueInfo, v);
  }
}
