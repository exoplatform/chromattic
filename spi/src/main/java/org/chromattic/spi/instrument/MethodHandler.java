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

package org.chromattic.spi.instrument;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface MethodHandler {

  /**
   * Invokes a zero argument method.
   *
   * @param o the target
   * @param method the method to invoke
   * @return the invocation returned value
   * @throws Throwable any throwable
   */
  Object invoke(Object o, Method method) throws Throwable;

  /**
   * Invokes a one argument method.
   *
   * @param o the target
   * @param method the method to invoke
   * @param arg the method argument
   * @return the invocation returned value
   * @throws Throwable any throwable
   */
  Object invoke(Object o, Method method, Object arg) throws Throwable;

  /**
   * Invokes a multi argument method.
   *
   * @param o the target
   * @param method the method to invoke
   * @param args the method arguments packed in an array
   * @return the invocation returned value
   * @throws Throwable any throwable
   */
  Object invoke(Object o, Method method, Object[] args) throws Throwable;

}
