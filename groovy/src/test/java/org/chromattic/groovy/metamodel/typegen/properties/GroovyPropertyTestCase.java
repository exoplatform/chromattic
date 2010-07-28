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

package org.chromattic.groovy.typegen.properties;

import groovy.lang.GroovyClassLoader;
import org.chromattic.metamodel.typegen.properties.PropertiesTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyPropertyTestCase extends PropertiesTestCase {

  private final Class<?> aclass = new GroovyClassLoader().parseClass(
    "import java.util.Map\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Properties\n" +
    "@PrimaryType(name = \"a\")\n" +
    "class A {\n" +
    "  @Properties() Map<String, String> properties;\n" +
    "}"
  );

  private final Class<?> bclass = new GroovyClassLoader().parseClass(
    "import java.util.Map\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Properties\n" +
    "@PrimaryType(name = \"b\")\n" +
    "class B {\n" +
    "  @Properties() Map<String, Object> properties;\n" +
    "}"
  );

  private final Class<?> cclass = new GroovyClassLoader().parseClass(
    "import java.util.Map\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Properties\n" +
    "@PrimaryType(name = \"c\")\n" +
    "class C {\n" +
    "  @Properties() Map<String, String> stringProperties;\n" +
    "  @Properties() Map<String, Integer> integerProperties;\n" +
    "}"
  );
  
  public void testStringProperties() throws Exception { testStringProperties(aclass); }
  public void testObjectProperties() throws Exception { testObjectProperties(bclass); }
  public void testAnyProperties() throws Exception { testAnyProperties(cclass); }
}
