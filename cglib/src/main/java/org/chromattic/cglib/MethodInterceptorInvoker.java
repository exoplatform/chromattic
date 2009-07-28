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

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.chromattic.spi.instrument.MethodHandler;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MethodInterceptorInvoker implements MethodInterceptor {

  /** .  */
  final MethodHandler invoker;

  public MethodInterceptorInvoker() {
    this.invoker = null; 
  }

  public MethodInterceptorInvoker(MethodHandler invoker) {
    this.invoker = invoker;
  }

  public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    if (Modifier.isAbstract(method.getModifiers())) {
      return invoker.invoke(o, method, args);
    } else {
      return methodProxy.invokeSuper(o, args);
    }
  }
}
