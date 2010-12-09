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

  /** . */
  private Handler handler;

  /** . */
  private A_1_0_X proxy;

  @Override
  protected void setUp() throws Exception {
    ProxyFactoryImpl<A_1_0_X> pf = new ProxyFactoryImpl<A_1_0_X>(A_1_0_X.class);
    handler = new Handler();
    proxy = pf.createProxy(handler);
  }

  private static class MethodInvocation {

    /** . */
    private final String id;

    /** . */
    private final List<Object> args;

    /** . */
    private final Object returnValue;

    private MethodInvocation(String id, Object returnValue, Object[] args) {
      this.id = id;
      this.args = Arrays.asList(args);
      this.returnValue = returnValue;
    }
  }

  private static class Handler implements MethodHandler {

    /** . */
    private LinkedList<MethodInvocation> expectedMethods = new LinkedList<MethodInvocation>();

    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      assertTrue(expectedMethods.size() > 0);
      MethodInvocation expectedInvocation = expectedMethods.removeFirst();
      assertEquals(expectedInvocation.id, method.toString());
      assertEquals(expectedInvocation.args, Arrays.asList(args));
      return expectedInvocation.returnValue;
    }

    public void addExpectedInvocation(String expectedMethodId, Object returnValue, Object... expectedArgs) {
      expectedMethods.add(new MethodInvocation(expectedMethodId, returnValue, expectedArgs));
    }

    public void assertEmpty() {
      assertEquals(Collections.<MethodInvocation>emptyList(), expectedMethods);
    }
  }

  public void testA() {
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a()", null);
    proxy.a();
    handler.assertEmpty();

    //
    Object o = new Object();
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(java.lang.Object)", null, o);
    proxy.a(o);
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(int)", null, 3);
    proxy.a(3);
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(boolean)", null, true);
    proxy.a(true);
    handler.assertEmpty();
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(boolean)", null, false);
    proxy.a(false);
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(int,boolean)", null, 3, true);
    proxy.a(3, true);
    handler.assertEmpty();

    //
    Object[] objectArray = new Object[0];
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(java.lang.Object[])", null, (Object)objectArray);
    proxy.a(objectArray);
    handler.assertEmpty();

    //
    int[] intArray = new int[0];
    handler.addExpectedInvocation("public abstract void org.chromattic.apt.A.a(int[])", null, (Object)intArray);
    proxy.a(intArray);
    handler.assertEmpty();
  }

  public void testB() {
    Object ret = new Object();
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b()", ret);
    assertEquals(ret, proxy.b());
    handler.assertEmpty();

    //
    Object o = new Object();
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(java.lang.Object)", ret, o);
    assertEquals(ret, proxy.b(o));
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(int)", ret, 3);
    assertEquals(ret, proxy.b(3));
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(boolean)", ret, true);
    assertEquals(ret, proxy.b(true));
    handler.assertEmpty();
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(boolean)", ret, false);
    assertEquals(ret, proxy.b(false));
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(int,boolean)", ret, 3, true);
    assertEquals(ret, proxy.b(3, true));
    handler.assertEmpty();

    //
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(java.lang.Object,long)", ret, o, 5L);
    assertEquals(ret, proxy.b(o, 5L));
    handler.assertEmpty();

    //
    Object[] objectArray = new Object[0];
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(java.lang.Object[])", ret, (Object)objectArray);
    assertEquals(ret, proxy.b(objectArray));
    handler.assertEmpty();

    //
    int[] intArray = new int[0];
    handler.addExpectedInvocation("public abstract java.lang.Object org.chromattic.apt.A.b(int[])", ret, (Object)intArray);
    assertEquals(ret, proxy.b(intArray));
    handler.assertEmpty();
  }
}
