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

package org.chromattic.ext.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class AnnotationPresenceTest extends TestCase {
  private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
  private static final GroovyShell groovyShell = new GroovyShell(groovyClassLoader);

  public AnnotationPresenceTest() {
    groovyClassLoader.parseClass(
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "import org.chromattic.api.annotations.Name\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "@ChromatticSupport\n" +
      "class A {\n" +
      "  public @Name def String stringTypedChromattic\n" +
      "  public @Property def String stringTypedChromatticExplicitGetter\n" +
      "  public String getStringTypedChromatticExplicitGetter() {\n" +
      "    return stringTypedChromatticExplicitGetter\n" +
      "  }\n" +
      "}"
    );
  }

  public void testAnnotationFieldPresent() {
    Object eval = groovyShell.evaluate("new A().getClass().getDeclaredField(\"stringTypedChromattic\").getAnnotations().length");
    assertEquals(eval, 0);
  }

  public void testAnnotationImplicitGetterPresent() {
    Object eval = groovyShell.evaluate("new A().getClass().getDeclaredMethod(\"getStringTypedChromattic\").getAnnotations().length");
    assertEquals(eval, 1);
  }

  public void testAnnotationExplicitGetterPresent() {
    Object eval = groovyShell.evaluate("new A().getClass().getDeclaredMethod(\"getStringTypedChromatticExplicitGetter\").getAnnotations().length");
    assertEquals(eval, 1);
  }
    
}
