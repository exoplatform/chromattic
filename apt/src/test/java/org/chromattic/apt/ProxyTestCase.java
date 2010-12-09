/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.chromattic.spi.instrument.MethodHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ProxyTestCase extends TestCase {

  public static class MethodId {

    /** . */
    private final String name;

    /** . */
    private final Class<?>[] types;

    private MethodId(String name, Class<?>... types) {
      this.name = name;
      this.types = types;
    }

    public static MethodId get(String name, Class<?>... types) {
      return new MethodId(name, types);
    }
  }

  private static class MethodInvocation {

    /** . */
    private final MethodId id;

    /** . */
    private final List<Object> args;

    /** . */
    private final Object returnValue;

    private MethodInvocation(MethodId id, Object returnValue, Object[] args) {
      this.id = id;
      this.args = Arrays.asList(args);
      this.returnValue = returnValue;
    }
  }

  private static class Proxy<P> {

    /** . */
    private LinkedList<MethodInvocation> expectedMethods = new LinkedList<MethodInvocation>();

    /** . */
    private final Class<P> proxiedType;

    /** . */
    private final P proxy;

    /** . */
    private final MethodHandler handler = new MethodHandler() {
      public Object invoke(Object o, Method method) throws Throwable {
        return invoke(o, method, new Object[0]);
      }

      public Object invoke(Object o, Method method, Object arg) throws Throwable {
        return invoke(o, method, new Object[]{arg});
      }

      public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        assertTrue(expectedMethods.size() > 0);
        MethodInvocation expectedInvocation = expectedMethods.removeFirst();
        assertEquals(expectedInvocation.id.name, method.getName());
        assertEquals(Arrays.asList(expectedInvocation.id.types), Arrays.asList(method.getParameterTypes()));
        assertEquals(expectedInvocation.args, Arrays.asList(args));
        return expectedInvocation.returnValue;
      }
    };

    public Proxy(Class<P> proxiedType) {
      ProxyTypeImpl<P> pf = new ProxyTypeImpl<P>(proxiedType);
      this.proxy = pf.createProxy(handler);
      this.proxiedType = proxiedType;
    }

    public void invoke(MethodId mid, Object ret, Object... args) {
      expectedMethods.add(new MethodInvocation(mid, ret, args));
      try {
        Method m = proxiedType.getMethod(mid.name, mid.types);
        Object actualRet = m.invoke(proxy, args);
        assertEquals(actualRet, ret);
        assertEquals(Collections.<MethodInvocation>emptyList(), expectedMethods);
      }
      catch (Exception e) {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
    }
  }

  public void testA_1_0_X() throws Exception {
    Class aType = Thread.currentThread().getContextClassLoader().loadClass("org.chromattic.apt.A_1_0_X");
    Proxy<?> proxy = new Proxy<Object>(aType);

    //
    proxy.invoke(MethodId.get("a"), null);
    proxy.invoke(MethodId.get("a", Object.class), null, new Object());
    proxy.invoke(MethodId.get("a", int.class), null, 3);
    proxy.invoke(MethodId.get("a", boolean.class), null, true);
    proxy.invoke(MethodId.get("a", boolean.class), null, false);
    proxy.invoke(MethodId.get("a", int.class, boolean.class), null, 3, true);
    proxy.invoke(MethodId.get("a", Object[].class), null, (Object)new Object[0]);
    proxy.invoke(MethodId.get("a", int[].class), null, new int[0]);

    //
    Object ret = new Object();
    proxy.invoke(MethodId.get("b"), ret);
    proxy.invoke(MethodId.get("b", Object.class), ret, new Object());
    proxy.invoke(MethodId.get("b", int.class), ret, 3);
    proxy.invoke(MethodId.get("b", boolean.class), ret, true);
    proxy.invoke(MethodId.get("b", boolean.class), ret, false);
    proxy.invoke(MethodId.get("b", int.class, boolean.class), ret, 3, true);
    proxy.invoke(MethodId.get("b", Object[].class), ret, (Object)new Object[0]);
    proxy.invoke(MethodId.get("b", int[].class), ret, new int[0]);
  }

  public void testA_1_1_X() throws Exception {
    Proxy<A_1_1_X> proxy = new Proxy<A_1_1_X>(A_1_1_X.class);

    //
    proxy.invoke(MethodId.get("a"), null);
    proxy.invoke(MethodId.get("a", Object.class), null, new Object());
    proxy.invoke(MethodId.get("a", int.class), null, 3);
    proxy.invoke(MethodId.get("a", boolean.class), null, true);
    proxy.invoke(MethodId.get("a", boolean.class), null, false);
    proxy.invoke(MethodId.get("a", int.class, boolean.class), null, 3, true);
    proxy.invoke(MethodId.get("a", Object[].class), null, (Object)new Object[0]);
    proxy.invoke(MethodId.get("a", int[].class), null, new int[0]);

    //
    Object ret = new Object();
    proxy.invoke(MethodId.get("b"), ret);
    proxy.invoke(MethodId.get("b", Object.class), ret, new Object());
    proxy.invoke(MethodId.get("b", int.class), ret, 3);
    proxy.invoke(MethodId.get("b", boolean.class), ret, true);
    proxy.invoke(MethodId.get("b", boolean.class), ret, false);
    proxy.invoke(MethodId.get("b", int.class, boolean.class), ret, 3, true);
    proxy.invoke(MethodId.get("b", Object[].class), ret, (Object)new Object[0]);
    proxy.invoke(MethodId.get("b", int[].class), ret, new int[0]);
  }
}
