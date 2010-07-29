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

package org.chromattic.groovy.core.property.map;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.test.AbstractTestCase;

import java.util.HashMap;


/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyPropertiesTestCase extends AbstractTestCase {
  GroovyClassLoader classLoader = new GroovyClassLoader();

  GroovyShell groovyShell = new GroovyShell(classLoader);

  public GroovyPropertiesTestCase() {
    classLoader.parseClass(
      "import org.chromattic.metamodel.annotations.Skip\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Properties\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "import org.chromattic.groovy.GroovyUtils\n" +
      "@Skip\n" +
      "@PrimaryType(name = \"property_map:a\")\n" +
      "public class A {\n" +
      "  @Properties public abstract Map<String, Object> properties\n" +
      "  @Property(name = \"string_property\") String string\n" +
      "  public Object invokeMethod(String m, Object p) {\n" +
      "    if (this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).getAnnotations().any {it.toString().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE, 1)})\n" +
      "      return chromatticInvoker.invoke(this, this.class.getMethod(m, (Class<?>[]) p.collect { it.class }), p);\n" +
      "    return this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).invoke(this, p);\n" +
      "  }\n" +
      "}\n" +
      "@Skip\n" +
      "@PrimaryType(name = \"property_map:b\")\n" +
      "public class B {\n" +
      "  @Properties Map<String, Object> properties\n" +
      "  @Property(name = \"string_array_property\")\n String[] string\n" +
      "  public Object invokeMethod(String m, Object p) {\n" +
      "    if (this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).getAnnotations().any {it.toString().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE, 1)})\n" +
      "      return chromatticInvoker.invoke(this, this.class.getMethod(m, (Class<?>[]) p.collect { it.class }), p);\n" +
      "    return this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).invoke(this, p);\n" +
      "  }\n" +
      "}"
    );
  }

  protected void createDomain() {
    addClass((Class<?>) groovyShell.evaluate("A.class"));
    addClass((Class<?>) groovyShell.evaluate("B.class"));
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    groovyShell.setVariable("session", login());

    groovyShell.setVariable("b", groovyShell.evaluate("session.insert(A.class, \"a\")"));
    groovyShell.setVariable("c", groovyShell.evaluate("session.insert(B.class, \"b\")"));
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    //
    groovyShell.setVariable("b", null);
    groovyShell.evaluate("session.close()");
    groovyShell.setVariable("session", null);
  }

  public void testGetString() throws Exception {
    groovyShell.evaluate("b.setString(\"bar\");");
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    groovyShell.setVariable("value", groovyShell.evaluate("properties.get(\"string_property\")"));
    assertEquals("bar", groovyShell.evaluate("b.getProperties().get(\"string_property\")"));
  }

  public void testPutString() throws Exception {
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    groovyShell.evaluate("properties.put(\"string_property\", \"bar\")");
    assertEquals(null, groovyShell.getVariable("value"));
    assertEquals("bar",groovyShell.evaluate("b.getString()"));
  }

  public void testRemoveString() throws Exception {
    groovyShell.evaluate("b.setString(\"bar\")");
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    groovyShell.setVariable("value", groovyShell.evaluate("properties.remove(\"string_property\")"));
    assertEquals("bar", groovyShell.getVariable("value"));
    assertEquals(null,groovyShell.evaluate("b.getString()"));
  }

  public void testPutWrongType() throws Exception {
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    try {
      groovyShell.evaluate("properties.put(\"string_property\", 5)");
      fail();
    }
    catch (ClassCastException ignore) {
    }
  }

  public void testGetInvalidKey() throws Exception {
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    try {
      groovyShell.evaluate("properties.get(\"/invalid\")");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }


  public void testPutInvalidKey() throws Exception {
    groovyShell.setVariable("properties", groovyShell.evaluate("b.getProperties()"));
    try {
      groovyShell.evaluate("properties.put(\"/invalid\", \"foo\")");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testGetMultivaluedValue() throws Exception {
    groovyShell.evaluate("c.setString((String[])[\"a\",\"b\"])");
    groovyShell.setVariable("copy", new HashMap<String, Object>());
    groovyShell.evaluate(
      "for (Map.Entry<String, Object> entry : c.getProperties().entrySet()) {\n" +
      "  copy.put(entry.getKey(), entry.getValue());\n" +
      "}\n"
    );
    assertTrue((Boolean) groovyShell.evaluate("copy.containsKey(\"string_array_property\")"));
    assertEquals("a", groovyShell.evaluate("copy.get(\"string_array_property\")"));
  }
}
