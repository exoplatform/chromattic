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

package org.chromattic.groovy.typegen.attribute;

import groovy.lang.GroovyClassLoader;
import org.chromattic.metamodel.typegen.attribute.MappingTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyMappingTestCase extends MappingTestCase {
  private final Class<?> a1class = new GroovyClassLoader().parseClass(
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Path\n" +
    "@PrimaryType(name = \"a1\")\n" +
    "class A1 {\n" +
    "  @Path String path;\n" +
    "}"
  );

  private final Class<?> a2class = new GroovyClassLoader().parseClass(
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Name\n" +
    "@PrimaryType(name = \"a2\")\n" +
    "class A2 {\n" +
    "  @Name String name;\n" +
    "}"
  );

  private final Class<?> a3class = new GroovyClassLoader().parseClass(
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.WorkspaceName\n" +
    "@PrimaryType(name = \"a3\")\n" +
    "class A3 {\n" +
    "  @WorkspaceName String workspaceName;\n" +
    "}"
  );

  public void testA1() throws Exception { testA1(a1class); }
  public void testA2() throws Exception { testA2(a2class); }
  public void testA3() throws Exception { testA3(a3class); }
  
}