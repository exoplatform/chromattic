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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ImplementTest extends TestCase {
  private static final GroovyClassLoader aClassLoader = new GroovyClassLoader();
  private static final GroovyClassLoader bClassLoader = new GroovyClassLoader();
  private static final GroovyClassLoader cClassLoader = new GroovyClassLoader();

  private static final GroovyShell aShell = new GroovyShell(aClassLoader);
  private static final GroovyShell bShell = new GroovyShell(bClassLoader);
  private static final GroovyShell cShell = new GroovyShell(cClassLoader);

  public ImplementTest() {
    aClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "@PrimaryType( name=\"a\")" +
      "class A { public A() {} }\n"
    );
    
    bClassLoader.parseClass(
      "import org.chromattic.api.annotations.Name\n" +
      "class A {\n" +
      "  @Name String name\n" +
      "  public Integer m() {\n" +
      "    return 3\n" +
      "  }\n" +
      "  def f = 5\n" +
      "}\n"
    );
  }

  public void testGroovyInterceptableWithPrimaryType() throws Exception {
    assertEquals(1, aShell.evaluate("new A().getClass().getInterfaces().length"));
    assertTrue((Boolean) aShell.evaluate("new A().getClass().getInterfaces()[0].equals(GroovyInterceptable.class)"));
  }

  public void testGroovyInterceptableWithChromatticChildren() throws Exception {
    assertEquals(1, bShell.evaluate("new A().getClass().getInterfaces().length"));
    assertTrue((Boolean) bShell.evaluate("new A().getClass().getInterfaces()[0].equals(GroovyInterceptable.class)"));
  }

  public void testPrivateConstructor() throws Exception {
    assertEquals(Modifier.PROTECTED, aShell.evaluate("A.class.getDeclaredConstructor().getModifiers()"));
    assertEquals(Modifier.PROTECTED, bShell.evaluate("A.class.getDeclaredConstructor().getModifiers()"));
  }

  public void testNoChromatticMethod() throws Exception {
    assertEquals(3, bShell.evaluate("new A().m()"));
  }

  public void testNoChromatticField() throws Exception {
    assertEquals(5, bShell.evaluate("new A().f"));
  }
}
