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

package org.chromattic.groovy.typegen.onetoone.hierarchical;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.onetoone.hierarchical.MappingTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyMappingTestCase extends MappingTestCase {
  private final GroovyClassLoader aClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader bClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader cClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader dClassLoader = new GroovyClassLoader();

  private final GroovyShell aGroovyShell = new GroovyShell(aClassLoader);
  private final GroovyShell bGroovyShell = new GroovyShell(bClassLoader);
  private final GroovyShell cGroovyShell = new GroovyShell(cClassLoader);
  private final GroovyShell dGroovyShell = new GroovyShell(dClassLoader);

  public GroovyMappingTestCase() {
    aClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "@PrimaryType(name = \"1\")\n" +
      "class A1 {\n" +
      "  @MappedBy(\"child\") @Owner @OneToOne A2 child" +
      "}\n" +
      "@PrimaryType(name = \"2\")\n" +
      "class A2\n {" +
      "}\n"
    );

    bClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "@PrimaryType(name = \"1\")\n" +
      "class B1 {\n" +
      "}\n" +
      "@PrimaryType(name = \"2\")\n" +
      "class B2\n {" +
      "  @MappedBy(\"child\") @OneToOne B1 parent" +
      "}\n"
    );

    cClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "@PrimaryType(name = \"1\")\n" +
      "class C1 {\n" +
      "  @MappedBy(\"child\") @Owner @OneToOne C2 child" +
      "}\n" +
      "@PrimaryType(name = \"2\")\n" +
      "class C2\n {" +
      "  @MappedBy(\"child\") @OneToOne C1 parent" +
      "}\n"
    );

    dClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "@PrimaryType(name = \"1\")\n" +
      "class D {\n" +
      "  @MappedBy(\"child\") @Owner @OneToOne D child\n" +
      "  @MappedBy(\"child\") @OneToOne D parent\n" +
      "}\n"
    );
  }

  public void testA() { testA((Class<?>) aGroovyShell.evaluate("A1.class"), (Class<?>) aGroovyShell.evaluate("A2.class")); }
  public void testB() { testB((Class<?>) bGroovyShell.evaluate("B1.class"), (Class<?>) bGroovyShell.evaluate("B2.class")); }
  public void testC() { testC((Class<?>) cGroovyShell.evaluate("C1.class"), (Class<?>) cGroovyShell.evaluate("C2.class")); }
  public void testD() { testD((Class<?>) dGroovyShell.evaluate("D.class")); }
}
