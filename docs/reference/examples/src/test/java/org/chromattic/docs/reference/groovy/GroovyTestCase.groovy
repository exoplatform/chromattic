package org.chromattic.docs.reference.groovy

import junit.framework.TestCase
import org.chromattic.api.ChromatticBuilder
import org.chromattic.api.Chromattic
import org.chromattic.api.ChromatticSession
import org.chromattic.docs.reference.groovy.Page
/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
class GroovyTestCase extends TestCase {
  void testGroovy() {
    ChromatticBuilder builder = ChromatticBuilder.create(); // <> Creates the builder object
    builder.add(org.chromattic.docs.reference.groovy.Page.class); // <> We add the Page class to the builder object
    Chromattic chromattic = builder.build(); // <> Now the Chromattic object can be created
    
    ChromatticSession session = chromattic.openSession(); // <> Any Chromattic interaction requires to open a session
    try
    {
      Page page = session.insert(Page.class, "index"); // <> A new page is inserted under the /index path
      page.setTitle("Hello Page"); // <> Set the title property with setter
      page.content = "Hello World"; // <> Set the content property without setter
      session.save(); // <> Saves the session to persist changes in the repository

      String title = page.title; // <> Get the title property without getter
      String content = page.getContent(); // <> Get the title property with getter
    }
    finally
    {
      session.close(); // <> We must close the session to properly release the session
    }
  }
}
