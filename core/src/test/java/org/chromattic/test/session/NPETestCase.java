/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.test.session;

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NPETestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
  }

  public void testRemoveThrowsNPE() throws Exception {
    ChromatticSession session = login();
    try {
      session.remove(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testGetStatusThrowsNPE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getStatus(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testGetIdThrowsNPE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getId(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testGetNameThrowsNPE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getName(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testGetPathThrowsNPE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getPath(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }
}
