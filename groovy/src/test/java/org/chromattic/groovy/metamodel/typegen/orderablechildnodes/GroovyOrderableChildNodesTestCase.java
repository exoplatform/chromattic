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

package org.chromattic.groovy.metamodel.typegen.orderablechildnodes;

import groovy.lang.GroovyClassLoader;
import org.chromattic.metamodel.typegen.orderablechildnodes.OrderableChildNodesTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyOrderableChildNodesTestCase extends OrderableChildNodesTestCase {

  private final Class<?> aclass = new GroovyClassLoader().parseClass(
      "import java.util.Map\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "@PrimaryType(name = \"a\", orderable = true)\n" +
      "class A {\n" +
      "}"
    );

  public void testOrderable() throws Exception { testOrderable(aclass); }
}
