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

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ThrowableProxyTestCase extends TestCase {

  private static class MethodInvocation {

    /** . */
    private final Throwable throwed;

    private MethodInvocation(Throwable throwed) {
      this.throwed = throwed;
    }
  }

  private static class Handler<P> {

    /** . */
    private LinkedList<MethodInvocation> expectedMethods = new LinkedList<MethodInvocation>();

    /** . */
    private final Class<P> proxiedType;

    /** . */
    private final P proxy;

    /** . */
    private final MethodHandler handler = new MethodHandler() {
      public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        assertTrue(expectedMethods.size() > 0);
        MethodInvocation invocation = expectedMethods.removeFirst();
        throw invocation.throwed;
      }
      public Object invoke(Object o, Method method) throws Throwable {
        return invoke(o, method, new Object[0]);
      }
      public Object invoke(Object o, Method method, Object arg) throws Throwable {
        return invoke(o, method, new Object[]{arg});
      }
    };

    public Handler(Class<P> proxiedType) {
      ProxyTypeImpl<P> pf = new ProxyTypeImpl<P>(proxiedType);
      this.proxy = pf.createProxy(handler);
      this.proxiedType = proxiedType;
    }

    public <T extends Throwable> void invoke(MethodSignature sign, T throwable, Object... args) {
      addExpectedInvocation(throwable);
      Method m;
      try {
        m = sign.getMethod(proxiedType);
      }
      catch (NoSuchMethodException e) {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
      try {
        m.invoke(proxy, args);
        fail();
      }
      catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        HashSet<Class<?>> notUndeclaredList = new HashSet<Class<?>>(Arrays.asList(m.getExceptionTypes()));
        notUndeclaredList.add(RuntimeException.class);
        notUndeclaredList.add(Error.class);
        boolean expectingUndeclared = true;
        for (Class<?> notUndeclared : notUndeclaredList) {
          if (notUndeclared.isInstance(throwable)) {
            expectingUndeclared = false;
          }
        }
        if (cause instanceof UndeclaredThrowableException) {
          cause = cause.getCause();
        } else {
          if (expectingUndeclared) {
            fail();
          }
        }
        assertEquals(throwable, cause);
      }
      catch (Exception e) {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
      assertEquals(Collections.<MethodInvocation>emptyList(), expectedMethods);
    }

    public void addExpectedInvocation(Throwable throwed) {
      expectedMethods.add(new MethodInvocation(throwed));
    }
  }

  public void testB_1_0_X() throws ClassNotFoundException {
    Class type = Thread.currentThread().getContextClassLoader().loadClass("org.chromattic.apt.B_1_0_X");
    Handler<?> proxy = new Handler<Object>(type);
    for (String s : new String[]{"a","b","c","d","e","f"}) {
      MethodSignature sign = MethodSignature.get(s);
      proxy.invoke(sign, new Exception());
      proxy.invoke(sign, new IOException());
      proxy.invoke(sign, new Error());
      proxy.invoke(sign, new IOError(new RuntimeException()));
      proxy.invoke(sign, new RuntimeException());
      proxy.invoke(sign, new IndexOutOfBoundsException());
    }
  }

  public void testB_1_1_XNoArg() throws Throwable {
    Handler<B_1_1_X> proxy = new Handler<B_1_1_X>(B_1_1_X.class);
    for (String s : new String[]{"a1","b1","c1","d1","e1","f1","g1", "a2","b2","c2","d2","e2","f2","g2"}) {
      MethodSignature sign = MethodSignature.get(s);
      proxy.invoke(sign, new Exception());
      proxy.invoke(sign, new IOException());
      proxy.invoke(sign, new Error());
      proxy.invoke(sign, new IOError(new RuntimeException()));
      proxy.invoke(sign, new RuntimeException());
      proxy.invoke(sign, new IndexOutOfBoundsException());
    }
  }

  public void testB_1_1_XOneArg() throws Throwable {
    Handler<B_1_1_X> proxy = new Handler<B_1_1_X>(B_1_1_X.class);
    Object o = new Object();
    for (String s : new String[]{"a1","b1","c1","d1","e1","f1","g1", "a2","b2","c2","d2","e2","f2","g2"}) {
      MethodSignature sign = MethodSignature.get(s, Object.class);
      proxy.invoke(sign, new Exception(), o);
      proxy.invoke(sign, new IOException(), o);
      proxy.invoke(sign, new Error(), o);
      proxy.invoke(sign, new IOError(new RuntimeException()), o);
      proxy.invoke(sign, new RuntimeException(), o);
      proxy.invoke(sign, new IndexOutOfBoundsException(), o);
    }
  }

  public void testB_1_1_XArgs() throws Throwable {
    Handler<B_1_1_X> proxy = new Handler<B_1_1_X>(B_1_1_X.class);
    Object o1 = new Object();
    Object o2 = new Object();
    for (String s : new String[]{"a1","b1","c1","d1","e1","f1","g1", "a2","b2","c2","d2","e2","f2","g2"}) {
      MethodSignature sign = MethodSignature.get(s, Object.class, Object.class);
      proxy.invoke(sign, new Exception(), o1, o2);
      proxy.invoke(sign, new IOException(), o1, o2);
      proxy.invoke(sign, new Error(), o1, o2);
      proxy.invoke(sign, new IOError(new RuntimeException()), o1, o2);
      proxy.invoke(sign, new RuntimeException(), o1, o2);
      proxy.invoke(sign, new IndexOutOfBoundsException(), o1, o2);
    }
  }
}
