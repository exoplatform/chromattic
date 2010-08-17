package org.chromattic.groovy.metamodel

import org.chromattic.api.annotations.Name

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
class B implements GroovyInterceptable {
  @Name public String name;
  public Integer m() { return 3; }
  public def f = 5;
}
