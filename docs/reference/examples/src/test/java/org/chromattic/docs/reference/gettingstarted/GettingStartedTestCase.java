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
    ChromatticBuilder builder = ChromatticBuilder.create(); // <1> Create a new builder
    builder.add(Page.class); // <2> We add the Page class to the build
    Chromattic chromattic = builder.build(); // <3> Now the Chromattic object can be created

    //
    assertEquals("", "");

    // -2-
    ChromatticSession session = chromattic.openSession(); // <4> Any Chromattic interaction requires to open a session
    try
    {
      Page page = session.insert(Page.class, "index"); // <5> A new page is inserted under the /index path
      page.setContent("Hello World"); // <6> Set the content property
      session.save(); // <7> Saves the session to persist changes in the repository
    }
    finally
    {
      session.close(); // <8> We must close the session to properly release the session
    }
  }
}
