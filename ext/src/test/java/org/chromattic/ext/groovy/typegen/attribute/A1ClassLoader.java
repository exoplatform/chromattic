package org.chromattic.ext.groovy.typegen.attribute;

import groovy.lang.GroovyClassLoader;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class A1ClassLoader extends GroovyClassLoader {
  public A1ClassLoader() {
    parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.Path\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a1\")\n" +
      "class A1 {\n" +
      "  @Path String path;\n" +
      "}"
    );
  }
}
