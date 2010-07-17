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

package org.chromattic.ext.groovy.typegen.inheritance;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.inheritance.InheritanceTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyInheritanceTestCase extends InheritanceTestCase {
  private final GroovyClassLoader aClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader bClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader dClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader eClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader fClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader hClassLoader = new GroovyClassLoader();
  private final GroovyClassLoader iClassLoader = new GroovyClassLoader();

  private final GroovyShell aGroovyShell = new GroovyShell(aClassLoader);
  private final GroovyShell bGroovyShell = new GroovyShell(bClassLoader);
  private final GroovyShell dGroovyShell = new GroovyShell(dClassLoader);
  private final GroovyShell eGroovyShell = new GroovyShell(eClassLoader);
  private final GroovyShell fGroovyShell = new GroovyShell(fClassLoader);
  private final GroovyShell hGroovyShell = new GroovyShell(hClassLoader);
  private final GroovyShell iGroovyShell = new GroovyShell(iClassLoader);

  public GroovyInheritanceTestCase() {
    aClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.annotations.OneToMany\n" +
      "import org.chromattic.api.annotations.ManyToOne\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a1\")\n" +
      "class A1 {\n" +
      "  @Property(name = \"foo\") String foo\n" +
      "  @OneToOne @Owner @MappedBy(\"bar\") A1 parent1\n" +
      "  @OneToOne @MappedBy(\"bar\") A1 child1\n" +
      "  @OneToMany Collection<A1> children2\n" +
      "  @ManyToOne A1 parent2\n" +
      "}\n" +
      "@PrimaryType(name = \"a2\")\n" +
      "class A2 extends A1\n {" +
      "}\n" +
      "@PrimaryType(name = \"a3\")\n" +
      "class A3 extends A2\n {" +
      "}\n" +
      "@PrimaryType(name = \"a4\")\n" +
      "class A4 extends A3\n {" +
      "}\n" +
      "@PrimaryType(name = \"a5\")\n" +
      "class A5 extends A1\n {" +
      "  String foo\n" +
      "  A5 parent1\n" +
      "  A5 child1\n" +
      "  Collection<A5> children2\n" +
      "  A5 parent2\n" +
      "}"
    );

    bClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.ManyToOne\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"b1\")\n" +
      "class B1 {\n" +
      "  @ManyToOne B1 parent2" +
      "}\n" +
      "@PrimaryType(name = \"b2\")\n" +
      "class B2 extends B1\n {" +
      "}"
    );

    dClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "class D1 {\n" +
      "  @OneToOne @Owner @MappedBy(\"bar\") D1 parent1" +
      "}\n" +
      "@PrimaryType(name = \"d2\")\n" +
      "class D2 extends D1\n {" +
      "}"
    );

    eClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "class E1 {\n" +
      "  @OneToOne @MappedBy(\"bar\") E1 child1" +
      "}\n" +
      "@PrimaryType(name = \"e2\")\n" +
      "class E2 extends E1\n {" +
      "}"
    );

    fClassLoader.parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.OneToMany\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "class F1 {\n" +
      "  @OneToMany Collection<F1> getChildren2" +
      "}\n" +
      "@PrimaryType(name = \"f2\")\n" +
      "class F2 extends F1\n {" +
      "}"
    );

    hClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.OneToMany\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"h1\")\n" +
      "class H1<T> {\n" +
      "  @OneToMany Collection<T> children" +
      "}\n" +
      "@PrimaryType(name = \"h2\")\n" +
      "class H2 extends H1<H3>\n {" +
      "}\n" +
      "@PrimaryType(name = \"h3\")\n" +
      "class H3\n {" +
      "}"
    );

    iClassLoader.parseClass(
      "import java.util.Collection\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Owner\n" +
      "import org.chromattic.api.annotations.OneToOne\n" +
      "import org.chromattic.api.annotations.MappedBy\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"i1\")\n" +
      "class I1<T extends I3> {\n" +
      "  @Owner @OneToOne @MappedBy(\"child\") T child" +
      "}\n" +
      "@PrimaryType(name = \"i2\")\n" +
      "class I2 extends I1<I4>\n {" +
      "}\n" +
      "@PrimaryType(name = \"i3\")\n" +
      "class I3\n {" +
      "}\n" +
      "@PrimaryType(name = \"i4\")\n" +
      "class I4 extends I3\n {" +
      "}"
    );
  }

  @Override
  protected void setUp() throws Exception {
    setUp(
            (Class<?>) aGroovyShell.evaluate("A1.class")
            , (Class<?>) aGroovyShell.evaluate("A3.class")
            , (Class<?>) aGroovyShell.evaluate("A4.class")
            , (Class<?>) aGroovyShell.evaluate("A5.class")
            
    );
  }


  public void testGenericRelationship() {
    testGenericRelationship(
            (Class<?>) hGroovyShell.evaluate("H1.class")
            , (Class<?>) hGroovyShell.evaluate("H2.class")
            , (Class<?>) hGroovyShell.evaluate("H3.class"));
  }

  public void testOneToOneGenericRelationship() {
    testOneToOneGenericRelationship(
            (Class<?>) iGroovyShell.evaluate("I1.class")
            , (Class<?>) iGroovyShell.evaluate("I2.class")
            , (Class<?>) iGroovyShell.evaluate("I3.class")
            , (Class<?>) iGroovyShell.evaluate("I4.class"));
  }
  
  public void testInvalidAbstractManyToOne() { testInvalidAbstractManyToOne((Class<?>) bGroovyShell.evaluate("B2.class")); }
  public void testInvalidAbstractOwnerOneToOne() { testInvalidAbstractOwnerOneToOne((Class<?>) dGroovyShell.evaluate("D2.class")); }
  public void testInvalidAbstractOneToOne() { testInvalidAbstractOneToOne((Class<?>) eGroovyShell.evaluate("E2.class")); }
  public void testInvalidAbstractOneToMany() { testInvalidAbstractOneToMany((Class<?>) fGroovyShell.evaluate("F2.class")); }

}
