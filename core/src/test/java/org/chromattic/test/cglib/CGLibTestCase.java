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

package org.chromattic.test.cglib;

import junit.framework.TestCase;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CGLibTestCase extends TestCase {

  public void testFoo() {

    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Bean.class);
    enhancer.setCallback(new InvocationHandler() {
      public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("method.getName() = " + method.getName());
        System.out.println("Intercepted foo");
        return null;
      }
    });
    Bean bean = (Bean)enhancer.create();
    bean.foo();
  }

}
