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

package org.chromattic.apt;

import org.chromattic.spi.instrument.ProxyType;
import org.chromattic.spi.instrument.MethodHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Implements the SPI {@link ProxyType} interface.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ProxyTypeImpl<O> implements ProxyType<O> {

  /** . */
  private final Constructor<? extends O> ctor;

  private final Field f;

  public ProxyTypeImpl(Class<O> objectClass) {

    Constructor<? extends O> ctor;
    Field handler;
    try {
      ClassLoader classLoader = objectClass.getClassLoader();
      Class<? extends O> proxyClass = (Class<? extends O>)classLoader.loadClass(objectClass.getName() + "_Chromattic");
      ctor = proxyClass.getConstructor(MethodHandler.class);
      handler = proxyClass.getField("handler");
    }
    catch (Exception e) {
      AssertionError ae = new AssertionError("Could not create proxy type for " + objectClass);
      ae.initCause(e);
      throw ae;
    }

    this.ctor = ctor;
    this.f = handler;
  }

  public MethodHandler getInvoker(Object proxy) {
    if (proxy instanceof Instrumented) {
      try {
        return (MethodHandler)f.get(proxy);
      }
      catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    } else {
      return null;
    }
  }

  public O createProxy(MethodHandler handler) {
    try {
      return ctor.newInstance(handler);
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  public Class<? extends O> getType() {
    return ctor.getDeclaringClass();
  }
}
