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

package org.chromattic.ext.groovy.typegen.onetoone.embedded;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.onetoone.embedded.EmbeddedTypeTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyEmbeddedTypeTestCase extends EmbeddedTypeTestCase {
  private final GroovyClassLoader aClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader bClassLoader = new GroovyClassLoader();

  private final GroovyShell aGroovyShell = new GroovyShell(aClassLoader);
  private final GroovyShell bGroovyShell = new GroovyShell(bClassLoader);

  public GroovyEmbeddedTypeTestCase() {
    aClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.RelationshipType\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a1\")\n" +
      "class A1 {\n" +
      "}\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a2\")\n" +
      "class A2\n {" +
      "  @Owner @OneToOne(type = RelationshipType.EMBEDDED) A1 superType" +
      "}\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a3\")\n" +
      "class A3\n {" +
      "}\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a4\")\n" +
      "class A4\n {" +
      "  @OneToOne(type = RelationshipType.EMBEDDED) A3 superType\n" +
      "}"
    );

  bClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MixinType\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "import org.chromattic.api.RelationshipType\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"b1\")\n" +
      "class B1 {\n" +
      "  @Owner @OneToOne(type = RelationshipType.EMBEDDED) B2 b2" +
      "}\n" +
      "@ChromatticSupport\n" +
      "@MixinType(name = \"b2\")\n" +
      "class B2\n {" +
      "  @Property(name = \"foo\") String foo" +
      "}\n"
    );
  }

  public void testOwnerMixinType() { testOwnerMixinType((Class<?>) bGroovyShell.evaluate("B1.class"), (Class<?>) bGroovyShell.evaluate("B2.class")); }
  public void testOwnerSuperType() { testOwnerSuperType((Class<?>) aGroovyShell.evaluate("A1.class"), (Class<?>) aGroovyShell.evaluate("A2.class")); }
  public void testOwnedSuperType() { testOwnedSuperType((Class<?>) aGroovyShell.evaluate("A3.class"), (Class<?>) aGroovyShell.evaluate("A4.class")); }
}
