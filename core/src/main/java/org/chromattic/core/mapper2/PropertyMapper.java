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

package org.chromattic.core.mapper2;

import org.chromattic.core.MethodInvoker;
import org.chromattic.core.ObjectContext;
import org.chromattic.metamodel.bean2.PropertyInfo;
import org.chromattic.metamodel.bean2.ValueInfo;
import org.chromattic.metamodel.mapping2.PropertyMapping;
import org.reflext.api.MethodInfo;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PropertyMapper<P extends PropertyInfo<V>, V extends ValueInfo, O extends ObjectContext> implements MethodInvoker<O> {

  /** . */
  protected final Class<O> contextType;

  /** . */
  protected final PropertyMapping<P, V> info;

  public PropertyMapper(Class<O> contextType, PropertyMapping<P, V> info) {
    this.contextType = contextType;
    this.info = info;
  }

  public Class<O> getType() {
    return contextType;
  }

  public PropertyMapping<P, V> getInfo() {
    return info;
  }

  public Object get(O context) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public void set(O context, Object value) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public Object invoke(O ctx, Method method, Object[] args) throws Throwable {
    MethodInfo getter = info.getProperty().getGetter();
    if (getter != null && method.equals(getter.getMethod())) {
      return get(ctx);
    } else {
      MethodInfo setter = info.getProperty().getSetter();
      if (setter != null && method.equals(info.getProperty().getSetter().getMethod())) {
        set(ctx, args[0]);
        return null;
      } else {
        throw new AssertionError();
      }
    }
  }
}