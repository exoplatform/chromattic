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

import org.chromattic.spi.instrument.ProxyFactory;
import org.chromattic.spi.instrument.MethodHandler;

import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ProxyFactoryImpl<O> implements ProxyFactory<O> {

  /** . */
  private final Constructor<? extends O> ctor;

  public ProxyFactoryImpl(Class<O> objectClass) {

    Constructor<? extends O> ctor;
    try {
      Class<? extends O> proxyClass = (Class<? extends O>)Thread.currentThread().getContextClassLoader().loadClass(objectClass.getName() + "_Chromattic");
      ctor = proxyClass.getConstructor(MethodHandler.class);
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }

    this.ctor = ctor;
  }

  public O createProxy(MethodHandler invoker) {
    try {
      return ctor.newInstance(invoker);
    }
    catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
