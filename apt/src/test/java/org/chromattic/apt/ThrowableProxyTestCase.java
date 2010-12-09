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

import junit.framework.TestCase;
import org.chromattic.spi.instrument.MethodHandler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ThrowableProxyTestCase extends TestCase {

  /** . */
  private Handler handler;

  /** . */
  private B_1_0_X proxy;

  @Override
  protected void setUp() throws Exception {
    ProxyFactoryImpl<B_1_0_X> pf = new ProxyFactoryImpl<B_1_0_X>(B_1_0_X.class);
    handler = new Handler();
    proxy = pf.createProxy(handler);
  }

  private static class MethodInvocation {

    /** . */
    private final Throwable throwed;

    private MethodInvocation(Throwable throwed) {
      this.throwed = throwed;
    }
  }

  private static class Handler implements MethodHandler {

    /** . */
    private LinkedList<MethodInvocation> expectedMethods = new LinkedList<MethodInvocation>();

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      assertTrue(expectedMethods.size() > 0);
      MethodInvocation invocation = expectedMethods.removeFirst();
      throw invocation.throwed;
    }

    public <T extends Throwable> T addExpectedInvocation(T throwed) {
      expectedMethods.add(new MethodInvocation(throwed));
      return throwed;
    }
  }

  public void testA() {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.a();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.a();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.a();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testB() throws IOException {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.b();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    IOException expectedIOEx = handler.addExpectedInvocation(new IOException());
    try {
      proxy.b();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedIOEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.b();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.b();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testC() throws Exception {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.c();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.c();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.c();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testD() {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.d();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.d();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.d();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testE() {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.e();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.e();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.e();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testF() {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.f();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.f();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.f();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }

  public void testG() {
    Exception expectedEx = handler.addExpectedInvocation(new Exception());
    try {
      proxy.g();
      fail();
    }
    catch (UndeclaredThrowableException t) {
      assertSame(expectedEx, t.getCause());
    }
    Error expectedErr = handler.addExpectedInvocation(new Error());
    try {
      proxy.g();
      fail();
    }
    catch (Error t) {
      assertSame(expectedErr, t);
    }
    RuntimeException expectedREx = handler.addExpectedInvocation(new RuntimeException());
    try {
      proxy.g();
      fail();
    }
    catch (RuntimeException t) {
      assertSame(expectedREx, t);
    }
  }
}
