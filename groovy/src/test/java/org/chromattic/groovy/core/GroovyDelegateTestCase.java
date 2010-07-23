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

package org.chromattic.groovy.core;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyDelegateTestCase extends TestCase {
  public void testIsGroovyInterceptable() throws Exception {
    Class<?> clazz = new GroovyClassLoader().parseClass(
      "import org.chromattic.api.annotations.Name\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "@PrimaryType(name = \"a\")\n" +
      "class A {\n" +
      "  @Name String name;" +
      "}"
    );
    assertTrue(Arrays.asList(clazz.getInterfaces()).contains(GroovyInterceptable.class));
  }

  public void testDynamicInvoke() throws Exception {
    GroovyClassLoader classLoader = new GroovyClassLoader();
    classLoader.parseClass(
      "import org.chromattic.api.annotations.Name\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "@PrimaryType(name = \"b\")\n" +
      "class B {\n" +
      "  @Name String name;\n" +
         "public String foo(String s) { return s; }" +
      "}"
    );
    System.out.println(new GroovyShell(classLoader).evaluate("new B().foo(\"plop\")"));
  }
}
