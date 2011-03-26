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
package org.chromattic.test.common;

import junit.framework.TestCase;
import org.chromattic.api.BuilderException;
import org.chromattic.common.ObjectInstantiator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectInstantiatorTestCase extends TestCase {

  public static abstract class A { }

  public void testAbstractClass() {
    assertCannotInstantiate(A.class);
  }

  public static abstract class B {
    protected B(String foo) {
    }
  }

  public void testNoNoArgCtor() {
    assertCannotInstantiate(B.class);
  }

  private static abstract class C { }

  public void testPrivateClass() {
    assertCannotInstantiate(C.class);
  }

  protected static abstract class D { }

  public void testProtectedClass() {
    assertCannotInstantiate(D.class);
  }

  static abstract class E { }

  public void testPackageProtectedClass() {
    assertCannotInstantiate(E.class);
  }

  static abstract class F {
    protected F() throws Exception {
      throw new Exception();
    }
  }

  public void testCtorThrowsException() {
    assertCannotInstantiate(F.class);
  }

  static abstract class G {
    protected G() throws Error {
      throw new Error();
    }
  }

  public void testCtorThrowsError() {
    assertCannotInstantiate(G.class);
  }

  static abstract class H {
    protected H() throws RuntimeException {
      throw new RuntimeException();
    }
  }

  public void testCtorThrowsRuntimeException() {
    assertCannotInstantiate(H.class);
  }

  public void testNotExpectedSubclass() {
    try {
      ObjectInstantiator.newInstance(String.class.getName(), Integer.class);
      fail();
    }
    catch (BuilderException e) {
    }
  }

  public void testNCNFE() {
    try {
      ObjectInstantiator.newInstance("zoifejoziejfoizejf", Integer.class);
      fail();
    }
    catch (BuilderException e) {
    }
  }

  public void testNPE() {
    try {
      ObjectInstantiator.newInstance(null, Integer.class);
      fail();
    }
    catch (NullPointerException e) {
    }
    try {
      ObjectInstantiator.newInstance(String.class.getName(), null);
      fail();
    }
    catch (NullPointerException e) {
    }
    try {
      ObjectInstantiator.newInstance(null);
      fail();
    }
    catch (NullPointerException e) {
    }
  }

  private void assertCannotInstantiate(Class<?> clazz) {
    try {
      ObjectInstantiator.newInstance(clazz);
      fail();
    }
    catch (BuilderException e) {
    }
    try {
      String name = clazz.getName();
      ObjectInstantiator.newInstance(name, Object.class);
      fail();
    }
    catch (BuilderException e) {
    }
  }
}
