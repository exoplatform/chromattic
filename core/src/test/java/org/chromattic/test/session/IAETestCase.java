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
public class IAETestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
  }

  public void testRemoveThrowsIAE() throws Exception {
    ChromatticSession session = login();
    try {
      session.remove(new Object());
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testGetStatusThrowsIAE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getStatus(new Object());
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testGetIdThrowsIAE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getId(new Object());
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testGetNameThrowsIAE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getName(new Object());
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testGetPathThrowsIAE() throws Exception {
    ChromatticSession session = login();
    try {
      session.getPath(new Object());
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }
}