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

package org.chromattic.test.methodinvocation;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.ChromatticSessionImpl;

import java.io.IOException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MethodInvocationTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(C.class);
  }

  public void testInvocation() throws Exception {
    ChromatticSessionImpl session = login();
    C a = session.insert(C.class, "tmi_a_a");

    //
    a.noopCalled = 0;
    a.noop();
    assertEquals(1, a.noopCalled);

    //
    a.throwIOExceptionCalled = 0;
    try {
      a.throwIOException();
      fail();
    }
    catch (IOException expected) {
    }
    assertEquals(1, a.throwIOExceptionCalled);

    //
    a.throwErrorCalled = 0;
    try {
      a.throwError();
      fail();
    }
    catch (Error expected) {
    }
    assertEquals(1, a.throwErrorCalled);

    //
    a.throwRuntimeExceptionCalled = 0;
    try {
      a.throwRuntimeException();
      fail();
    }
    catch (RuntimeException expected) {
    }
    assertEquals(1, a.throwRuntimeExceptionCalled);
  }

  public void testInterfaceMethod() {
    ChromatticSessionImpl session = login();
    A a = session.insert(C.class, "tmi_a_a");

    //
    ((C)a).m1Called = 0;
    a.m1();
    assertEquals(1, ((C)a).m1Called);

    //
    ((C)a).setFooCalled = 0;
    a.setFoo("foo");
    assertEquals(1, ((C)a).setFooCalled);
  }
}
