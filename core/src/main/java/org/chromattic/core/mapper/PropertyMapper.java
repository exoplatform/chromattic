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

import org.chromattic.core.MethodInvoker;
import org.chromattic.core.ObjectContext;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PropertyMapper<P extends PropertyInfo<V, K>, V extends ValueInfo, O extends ObjectContext<O>, K extends ValueKind> {

  /** . */
  protected final Class<O> contextType;

  /** . */
  protected final PropertyMapping<P, V, K> info;

  public PropertyMapper(Class<O> contextType, PropertyMapping<P, V, K> info) {
    this.contextType = contextType;
    this.info = info;
  }

  public Class<O> getType() {
    return contextType;
  }

  public PropertyMapping<P, V, K> getInfo() {
    return info;
  }

  public Object get(O context) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public void set(O context, Object value) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public MethodInvoker<O> getGetter() {
    return getter;
  }

  public MethodInvoker<O> getSetter() {
    return setter;
  }

  private final MethodInvoker<O> getter = new MethodInvoker<O>() {
    public Object invoke(O ctx) throws Throwable {
      return get(ctx);
    }
    public Object invoke(O ctx, Object arg) throws Throwable {
      throw new AssertionError();
    }
    public Object invoke(O ctx, Object[] args) throws Throwable {
      throw new AssertionError();
    }
  };

  private final MethodInvoker<O> setter = new MethodInvoker<O>() {
    public Object invoke(O ctx) throws Throwable {
      throw new AssertionError();
    }
    public Object invoke(O ctx, Object arg) throws Throwable {
      set(ctx, arg);
      return null;
    }
    public Object invoke(O ctx, Object[] args) throws Throwable {
      throw new AssertionError();
    }
  };
}