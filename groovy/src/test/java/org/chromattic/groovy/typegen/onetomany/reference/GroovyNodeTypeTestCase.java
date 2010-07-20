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

package org.chromattic.groovy.typegen.onetomany.reference;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.onetomany.reference.NodeTypeTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyNodeTypeTestCase extends NodeTypeTestCase {
  private final GroovyClassLoader cClassLoader = new GroovyClassLoader();
  private final GroovyShell cGroovyShell = new GroovyShell(cClassLoader);

  public GroovyNodeTypeTestCase() {
    cClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.api.annotations.OneToMany\n" +
      "import org.chromattic.api.annotations.ManyToOne\n" +
      "import org.chromattic.api.RelationshipType\n" +
      "@PrimaryType(name = \"1\")\n" +
      "class C1 {\n" +
      "  @MappedBy(\"ref\") @OneToMany(type = RelationshipType.REFERENCE) Collection<C2> referents" +
      "}\n" +
      "@PrimaryType(name = \"2\")\n" +
      "class C2\n {" +
      "  @MappedBy(\"ref\") @ManyToOne(type = RelationshipType.REFERENCE) C1 referenced" +
      "}\n"
    );
  }

  public void testProperty() throws Exception { testProperty((Class<?>) cGroovyShell.evaluate("C1.class"), (Class<?>) cGroovyShell.evaluate("C2.class")); }
  
}
