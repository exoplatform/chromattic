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

package org.chromattic.docs.reference.gettingstarted;

import junit.framework.TestCase;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class GettingStartedTestCase extends TestCase {

  public void testClient() {
    // -1-
    ChromatticBuilder builder = ChromatticBuilder.create(); // <> Creates the builder object
    builder.add(Page.class); // <> We add the Page class to the builder object
    Chromattic chromattic = builder.build(); // <> Now the Chromattic object can be created

    //
    assertEquals("", "");

    // -2-
    ChromatticSession session = chromattic.openSession(); // <> Any Chromattic interaction requires to open a session
    try
    {
      Page page = session.insert(Page.class, "index"); // <> A new page is inserted under the /index path
      page.setTitle("Hello Page"); // <> Set the title property
      page.setContent("Hello World"); // <> Set the content property
      session.save(); // <> Saves the session to persist changes in the repository
    }
    finally
    {
      session.close(); // <> We must close the session to properly release the session
    }
  }
}
