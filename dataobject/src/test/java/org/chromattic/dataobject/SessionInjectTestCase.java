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

package org.chromattic.dataobject;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class SessionInjectTestCase extends TestCase {
  private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

  private GroovyShell groovyShell = new GroovyShell(groovyClassLoader);

   @Override
   protected void setUp() throws Exception
   {
     super.setUp();
     groovyClassLoader.parseClass(
         "@org.chromattic.api.annotations.PrimaryType(name=\"\") class D {}\n" +
         "class A { @javax.inject.Inject Integer i; }\n" +
         "class B { @javax.inject.Inject Integer i; public void setI(Integer i) {this.i = i} }"
     );
   }

   public void testDelegateA() throws Exception {
     assertTrue(true);
     groovyShell.setProperty("a", groovyShell.evaluate("new A()"));

     // Setter
     groovyShell.evaluate("a.setI(42)");
     assertEquals(42, groovyShell.evaluate("a.i"));

     groovyShell.evaluate("a.setI(45)");
     assertEquals(45, groovyShell.evaluate("a.i"));


     // Property
     groovyShell.evaluate("a.i = 42");
     assertEquals(42, groovyShell.evaluate("a.i"));

     groovyShell.evaluate("a.i = 45");
     assertEquals(45, groovyShell.evaluate("a.i"));
  }

   public void testDelegateB() throws Exception {
     groovyShell.setProperty("b", groovyShell.evaluate("new B()"));

     // Setter
     groovyShell.evaluate("b.setI(42)");
     assertEquals(42, groovyShell.evaluate("b.i"));

     groovyShell.evaluate("b.setI(45)");
     assertEquals(45, groovyShell.evaluate("b.i"));

     // Property
     groovyShell.evaluate("b.i = 42");
     assertEquals(42, groovyShell.evaluate("b.i"));

     groovyShell.evaluate("b.i = 45");
     assertEquals(45, groovyShell.evaluate("b.i"));
   }

   public void testInjectChromatticClasses() {
      groovyShell.setProperty("a", groovyShell.evaluate("new A()"));

      assertEquals(1, groovyShell.evaluate("a.CHROMATTIC_CLASSES.length"));
      assertEquals(groovyShell.evaluate("D.class"), groovyShell.evaluate("a.CHROMATTIC_CLASSES[0]"));

      groovyShell.setProperty("b", groovyShell.evaluate("new B()"));

      assertEquals(1, groovyShell.evaluate("b.CHROMATTIC_CLASSES.length"));
      assertEquals(groovyShell.evaluate("D.class"), groovyShell.evaluate("b.CHROMATTIC_CLASSES[0]"));
   }
}
