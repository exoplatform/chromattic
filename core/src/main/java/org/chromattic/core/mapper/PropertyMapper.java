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

package org.chromattic.core.mapper;

import org.chromattic.core.EntityContext;
import org.chromattic.core.MethodInvoker;
import org.chromattic.core.bean.PropertyInfo;
import org.reflext.api.MethodInfo;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyMapper<P extends PropertyInfo> implements MethodInvoker {

  TypeMapper mapper;

  protected final P info;

  public PropertyMapper(P info) {
    this.info = info;
  }

  public P getInfo() {
    return info;
  }

  public Object get(EntityContext context) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public void set(EntityContext context, Object value) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public Object invoke(EntityContext ctx, Method method, Object[] args) throws Throwable {
    MethodInfo getter = info.getGetter();
    if (getter != null && method.equals(getter.getMethod())) {
      return get(ctx);
    } else {
      MethodInfo setter = info.getSetter();
      if (setter != null && method.equals(info.getSetter().getMethod())) {
        set(ctx, args[0]);
        return null;
      } else {
        throw new AssertionError();
      }
    }
  }
}