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
package org.chromattic.ntdef;

import junit.framework.TestCase;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;

import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NTHierarchyTestCase extends TestCase {

  /** . */
  private Chromattic chromattic;

  @Override
  protected void setUp() throws Exception {
    ChromatticBuilder builder = ChromatticBuilder.create();
    builder.add(NTFolder.class);
    builder.add(NTFile.class);
    builder.add(NTResource.class);
    chromattic = builder.build();
  }

  public void testNTHierarchy() throws Exception {
    ChromatticSession session = chromattic.openSession();
    NTFolder folder = session.insert(NTFolder.class, "folder");
    NTFile autoexec = folder.createFile("autoexec.bat", Resource.createPlainText("foo"));
    assertNotNull(autoexec);
    assertEquals("autoexec.bat", autoexec.getName());
  }

  public void testNTFile() throws Exception {
    ChromatticSession session = chromattic.openSession();  
    NTFile file = session.insert(NTFile.class, "file");

    // Check initial state
    assertNull(file.getContent());
    assertNull(file.getContentResource());

    // Update
    Date d1 = new Date();
    file.setContentResource(new Resource("text/plain", "UTF-8", "foo".getBytes("UTF-8")));
    Date d2 = new Date();

    // Get data
    Date lastModified = file.getLastModified();
    Resource res = file.getContentResource();

    //
    assertNotNull(lastModified);
    assertTrue(d1.compareTo(lastModified) <= 0);
    assertTrue(d2.compareTo(lastModified) >= 0);
    assertEquals("text/plain", res.getMimeType());
    assertEquals("UTF-8", res.getEncoding());
    assertEquals("foo", new String(res.getData(), "UTF-8"));

    // Try save
    session.save();
    session.close();

    // Reopen
    session = chromattic.openSession();

    // Get same data
    file = session.findByPath(NTFile.class, "file");
    lastModified = file.getLastModified();
    res = file.getContentResource();

    //
    assertTrue(d1.compareTo(lastModified) <= 0);
    assertTrue(d2.compareTo(lastModified) >= 0);
    assertEquals("text/plain", res.getMimeType());
    assertEquals("UTF-8", res.getEncoding());
    assertEquals("foo", new String(res.getData(), "UTF-8"));

  }

}
