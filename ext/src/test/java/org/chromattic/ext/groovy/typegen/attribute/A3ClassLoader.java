package org.chromattic.ext.groovy.typegen.attribute;

import groovy.lang.GroovyClassLoader;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class A3ClassLoader extends GroovyClassLoader {
  public A3ClassLoader() {
    parseClass(
      "import org.chromattic.api.annotations.PrimaryType\n" +
      "import org.chromattic.api.annotations.WorkspaceName\n" +
      "import org.chromattic.ext.groovy.annotations.ChromatticSupport\n" +
      "@ChromatticSupport\n" +
      "@PrimaryType(name = \"a3\")\n" +
      "class A3 {\n" +
      "  @WorkspaceName String workspaceName;\n" +
      "}"
    );
  }
}