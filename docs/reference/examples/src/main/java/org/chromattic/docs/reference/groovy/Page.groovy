package org.chromattic.docs.reference.groovy

import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Property
import org.chromattic.api.annotations.PrimaryType

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@PrimaryType(name = "gs:page")
class Page {
  /**
   * The page name.
   */
  @Name def String name // <> The name property is mapped to the node name

  /**
   * The page title.
   */
  @Property(name = "title") def String title // <> The title property is mapped to the title node property

  /**
   * The page content.
   */
  @Property(name = "content") def String content // <> The content property is mapped to the content node property
}
