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

package org.chromattic.groovy.metamodel.typegen.inheritance;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.inheritance.MappingTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyMappingTestCase extends MappingTestCase {

  private final GroovyClassLoader bClassLoader = new GroovyClassLoader();
  private final GroovyShell bGroovyShell = new GroovyShell(bClassLoader);

  public GroovyMappingTestCase() {
    bClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.ManyToOne\n" +
      "@PrimaryType(name = \"b1\")\n" +
      "class B1 {\n" +
      "  @ManyToOne B1 parent2\n" +
      "}\n" +
      "@PrimaryType(name = \"b2\")\n" +
      "class B2 extends B1\n {" +
      "}"
    );
  }


  public void testB() throws Exception { testB((Class<?>) bGroovyShell.evaluate("B1.class"), (Class<?>) bGroovyShell.evaluate("B2.class")); }
}
