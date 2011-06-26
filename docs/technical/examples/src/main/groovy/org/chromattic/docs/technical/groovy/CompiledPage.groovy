package org.chromattic.docs.technical.groovy

import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Property
import org.chromattic.api.annotations.PrimaryType
import org.chromattic.spi.instrument.MethodHandler
import org.chromattic.groovy.ChromatticGroovyInvocation

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@PrimaryType(name = "gs:page")
class CompiledPage implements GroovyInterceptable { // <> Implements GroovyInterceptable
  /**
   * The page name.
   */
  // <> The annotations were moved to the getter
  def String name

  /**
   * The page title.
   */
  def String title

  /**
   * The page content.
   */
  def String content

  private MethodHandler chromatticInvoker_; // <> The method handler was created

  protected CompiledPage() {} // <> Default constructor become protected
  public CompiledPage(MethodHandler chromatticInvoker) { // <> Initializer constructor was created
    this.chromatticInvoker_ = chromatticInvoker;
  }

  // <> Getter and setter has been generated
  @Name
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Property(name = "title") 
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Property(name = "content") 
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content
  }

  // <> Create invokeMethod method for MOP interception
  public Object invokeMethod(String m, Object p) {
    ChromatticGroovyInvocation.invokeMethod(this, m, p, chromatticInvoker)
  }

  // <> Create getProperty & setProperty method for MOP interception
  public Object getProperty(String p) {
    return ChromatticGroovyInvocation.getProperty(this, p, chromatticInvoker)
  }

  public void setProperty(String p, Object v) {
    ChromatticGroovyInvocation.setProperty(this, p, v, chromatticInvoker)
  }
}
