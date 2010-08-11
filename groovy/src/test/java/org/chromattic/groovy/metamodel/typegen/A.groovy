package org.chromattic.groovy.metamodel.typegen

import org.chromattic.api.annotations.PrimaryType
import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Property

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@PrimaryType(name="a")
public class A {
  public def dynamicTyped
  public def String stringTyped
  public @Name def String stringTypedChromattic
  public @Property def String stringTypedChromatticExplicitGetter
  public String getStringTypedChromatticExplicitGetter() {
    return stringTypedChromatticExplicitGetter
  }
}
