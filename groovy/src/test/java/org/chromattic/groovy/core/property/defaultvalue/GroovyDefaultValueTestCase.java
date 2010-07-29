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

package org.chromattic.groovy.core.property.defaultvalue;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.test.property.defaultvalue.DefaultValueTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyDefaultValueTestCase extends DefaultValueTestCase {
    GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    GroovyShell groovyShell = new GroovyShell(groovyClassLoader);

  public GroovyDefaultValueTestCase() {
    groovyClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "import org.chromattic.api.annotations.DefaultValue\n" +
      "import org.chromattic.api.annotations.SetterDelegation\n" +
      "import org.chromattic.groovy.ChromatticGroovyInvocation\n" +
      "@PrimaryType(name = \"property_defaultvalue:a\")\n" +
      "public class A {\n" +
      "  @Property(name = \"primitive_int_property\") @DefaultValue(\"5\") Integer primitiveInt\n" +
      "}"
    );
  }

  protected void createDomain() {
    addClass((Class<?>) groovyShell.evaluate("A.class"));
  }

  public void testPrimitiveInt2() throws Exception {
    /*groovyShell.setVariable("session", login());
    groovyShell.setVariable("a", groovyShell.evaluate("session.insert(A.class, \"a\")"));
    groovyShell.setVariable("aNode", groovyShell.evaluate("session.getRoot().getNode(\"a\")"));

    //
    assertEquals(5, groovyShell.evaluate("a.getPrimitiveInt()"));
    assertFalse((Boolean) groovyShell.evaluate("aNode.hasProperty(\"primitive_int_property\")"));

    //
    groovyShell.evaluate("a.setPrimitiveInt(3)");
    assertEquals(3, groovyShell.evaluate("a.getPrimitiveInt()"));
    assertEquals(3, groovyShell.evaluate("(int) aNode.getProperty(\"primitive_int_property\").getLong()"));
    assertEquals(3, groovyShell.evaluate("a.getPrimitiveInt()"));
    groovyShell.evaluate("a.setPrimitiveInt(5)");

    //
    groovyShell.evaluate("a.primitiveInt = 12");
    assertEquals(12, groovyShell.evaluate("a.primitiveInt"));
    groovyShell.evaluate("a.primitiveInt += 30");
    assertEquals(42, groovyShell.evaluate("a.primitiveInt"));*/
  }
}
