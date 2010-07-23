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

package org.chromattic.groovy.typegen;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

/**
 * Test that
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class TypeTest extends TestCase {
  private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
  private static final GroovyShell groovyShell = new GroovyShell(groovyClassLoader);

  public TypeTest() {
    groovyClassLoader.parseClass(
      "import org.chromattic.api.annotations.Name\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "class A {\n" +
      "  public def dynamicTyped\n" +
      "  public def String stringTyped\n" +
      "  public @Name def String stringTypedChromattic\n" +
      "  public @Property def String stringTypedChromatticExplicitGetter\n" +
      "  public String getStringTypedChromatticExplicitGetter() {\n" +
      "    return stringTypedChromatticExplicitGetter\n" +
      "  }\n" +
      "}"
    );

  }

  public void testWithoutChromatticDynamicType() {
    Object eval = groovyShell.evaluate(
      "A a = new A();\n" +
      "a.dynamicTyped = new Object();\n" +
      "a.dynamicTyped.getClass()"
      );
    assertEquals(eval, Object.class);
  }

  public void testWithoutChromatticStaticType() {
    Object eval = groovyShell.evaluate(
      "A a = new A();\n" +
      "a.stringTyped = \"\"\n" +
      "a.stringTyped.getClass()"
      );
    assertEquals(eval, String.class);
  }

  public void testWithChromatticDynamicType() {
    try {
      new GroovyClassLoader().parseClass(
        "import org.chromattic.api.annotations.Name" +
        "class B { @Name def dynamicTypedChromattic }"
      );
      fail("Compilation must fails");
    } catch (Exception e) {
      // If compilation fails, the test must success
    }
  }

  public void testWithChromatticStaticType() {
    Object eval = groovyShell.evaluate(
      "A a = new A();\n" +
      "a.stringTypedChromattic = \"\"\n" +
      "a.stringTypedChromattic.getClass()"
      );
    assertEquals(eval, String.class);
  }
}
