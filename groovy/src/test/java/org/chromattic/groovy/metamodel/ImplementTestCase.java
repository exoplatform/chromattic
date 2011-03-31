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

package org.chromattic.groovy.metamodel;

import groovy.lang.GroovyInterceptable;
import junit.framework.TestCase;
import org.chromattic.spi.instrument.MethodHandler;

import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ImplementTestCase extends TestCase {

  public void testGroovyInterceptableWithPrimaryType() throws Exception {
    assertEquals(1, A.class.getInterfaces().length);
    assertTrue(A.class.getInterfaces()[0].equals(GroovyInterceptable.class));
  }

  public void testGroovyInterceptableWithChromatticChildren() throws Exception {
    assertEquals(1, B.class.getInterfaces().length);
    assertTrue((B.class.getInterfaces()[0].equals(GroovyInterceptable.class)));
  }

  public void testConstructors() throws Exception {
    assertEquals(Modifier.PROTECTED, A.class.getDeclaredConstructor().getModifiers());
    assertEquals(Modifier.PUBLIC, A.class.getDeclaredConstructor(MethodHandler.class).getModifiers());
    assertEquals(Modifier.PROTECTED, B.class.getDeclaredConstructor().getModifiers());
    assertEquals(Modifier.PUBLIC, B.class.getDeclaredConstructor(MethodHandler.class).getModifiers());
  }

  public void testNoChromatticMethod() throws Exception {
    assertEquals(3, (int) new B().m());
  }

  public void testNoChromatticField() throws Exception {
    assertEquals(5, new B().f);
  }

  public void testExistInvokeMethod() throws Exception {
    assertNotNull(A.class.getMethod("invokeMethod", String.class, Object.class));
  }
}
