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

package org.chromattic.groovy.metamodel.typegen;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class TypeTestCase extends TestCase {

  public void testWithoutChromatticDynamicType() throws Exception {
    assertEquals(Object.class, A.class.getField("dynamicTyped").getType());
  }

  public void testWithoutChromatticStaticType() throws Exception {
    assertEquals(String.class, A.class.getField("stringTyped").getType());
  }

  public void testWithChromatticDynamicType() throws Exception {
    try {
      new GroovyClassLoader().parseClass(
        "import org.chromattic.api.annotations.Name" +
        "class A { @Name def dynamicTypedChromattic }"
      );
      fail("Compilation must fails");
    } catch (Exception e) {
      // If compilation fails, the test must success
    }
  }

  public void testWithChromatticStaticType() throws Exception {
    try {
      B.class.getDeclaredField("stringTypedChromattic");
      fail();
    } catch (NoSuchFieldException e)
    {
       // It's ok
    }
    assertEquals(String.class, A.class.getMethod("getStringTypedChromattic").getReturnType());
  }
}