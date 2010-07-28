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

package org.chromattic.groovy.metamodel.typegen.property;

import groovy.lang.GroovyClassLoader;
import org.chromattic.metamodel.typegen.property.MappingTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyMappingTestCase extends MappingTestCase {
  private final Class<?> a1class = new GroovyClassLoader().parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "@PrimaryType(name = \"a1\")\n" +
      "class A1 {\n" +
      "  @Property(name = \"string\") String string;\n" +
      "}"
    );

  private final Class<?> a2class = new GroovyClassLoader().parseClass(
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.DefaultValue\n" +
    "import org.chromattic.api.annotations.Property\n" +
    "@PrimaryType(name = \"a2\")\n" +
    "class A2 {\n" +
    "  @DefaultValue(\"foo\") @Property(name = \"string\") String string;\n" +
    "}"
  );
  
  private final Class<?> b1class = new GroovyClassLoader().parseClass(
    "import java.util.List\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Property\n" +
    "@PrimaryType(name = \"b1\")\n" +
    "class B1 {\n" +
    "  @Property(name = \"strings\") List<String> strings;\n" +
    "}"
  );

  private final Class<?> b2class = new GroovyClassLoader().parseClass(
    "import java.util.List\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Property\n" +
    "import org.chromattic.api.annotations.DefaultValue\n" +
    "@PrimaryType(name = \"b2\")\n" +
    "class B2 {\n" +
    "  @DefaultValue([\"foo\",\"bar\"]) @Property(name = \"strings\") List<String> strings;\n" +
    "}"
  );

  private final Class<?> c1class = new GroovyClassLoader().parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "@PrimaryType(name = \"c1\")\n" +
      "class C1 {\n" +
      "  @Property(name = \"strings\") String[] strings;\n" +
      "}"
    );
  
  private final Class<?> c2class = new GroovyClassLoader().parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "import org.chromattic.api.annotations.DefaultValue\n" +
      "@PrimaryType(name = \"c2\")\n" +
      "class C2 {\n" +
      "  @DefaultValue([\"foo\",\"bar\"]) @Property(name = \"strings\") String[] strings;\n" +
      "}"
    );

  private final Class<?> d1class = new GroovyClassLoader().parseClass(
    "import java.util.Map\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Properties\n" +
    "@PrimaryType(name = \"d1\")\n" +
    "class D1 {\n" +
    "  @Properties Map<String, ?> properties;\n" +
    "}"
  );

  private final Class<?> d2class = new GroovyClassLoader().parseClass(
    "import java.util.Map\n" +
    "import org.chromattic.api.annotations.PrimaryType\n" +
    "import org.chromattic.api.annotations.Properties\n" +
    "@PrimaryType(name = \"d2\")\n" +
    "class D2 {\n" +
    "  @Properties Map<String, String> properties;\n" +
    "}"
  );

  private final Class<?> eclass = new GroovyClassLoader().parseClass(
      "import java.util.Map\n" +
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Property\n" +
      "@PrimaryType(name = \"e\")\n" +
      "class E {\n" +
      "  @Property(name = \"bytes\") byte[] bytes;\n" +
      "}"
    );
  
  public void testA1() throws Exception { testA1(a1class); }
  public void testA2() throws Exception { testA2(a2class); }
  public void testB1() throws Exception { testB1(b1class); }
  public void testB2() throws Exception { testB2(b2class); }
  public void testC1() throws Exception { testC1(c1class); }
  public void testC2() throws Exception { testC2(c2class); }
  public void testD1() throws Exception { testD1(d1class); }
  public void testD2() throws Exception { testD2(d2class); }
  public void testE() throws Exception { testE(eclass); }

}
