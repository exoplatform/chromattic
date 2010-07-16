package org.chromattic.ext.groovy.typegen.attribute;

import groovy.lang.GroovyClassLoader;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class A2ClassLoader extends GroovyClassLoader {
  public A2ClassLoader() {
    parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Name\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a2\")\n" +
      "class A2 {\n" +
      "  @Name String name;\n" +
      "}"
    );
  }
}