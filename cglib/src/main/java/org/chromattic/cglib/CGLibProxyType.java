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

package org.chromattic.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import org.chromattic.spi.instrument.ProxyType;
import org.chromattic.spi.instrument.MethodHandler;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CGLibProxyType<O> implements ProxyType<O> {

  /** . */
  private final Class<O> objectClass;

  /** . */
  private final Factory factory;

  public CGLibProxyType(Class<O> objectClass) {

    Enhancer enhancer = new Enhancer();
    if (objectClass.isInterface()) {
      enhancer.setSuperclass(Object.class);
      enhancer.setInterfaces(new Class[]{objectClass});
    } else {
      enhancer.setSuperclass(objectClass);
    }

    //
    enhancer.setUseFactory(true);
    enhancer.setUseCache(false);
    enhancer.setCallback(new MethodInterceptorInvoker());
    Factory factory = (Factory)enhancer.create();

    //
    this.objectClass = objectClass;
    this.factory = factory;
  }


  public O createProxy(MethodHandler invoker) {
    return (O)factory.newInstance(new MethodInterceptorInvoker(invoker));
  }

  public Class<? extends O> getType() {
    return objectClass;
  }
}
