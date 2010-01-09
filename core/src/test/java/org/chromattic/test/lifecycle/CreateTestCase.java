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

package org.chromattic.test.lifecycle;

import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CreateTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TLF_A.class);
  }

  public void testCreateWithWrongName() throws Exception {
    ChromatticSession session = login();

    //
    TLF_A a = session.create(TLF_A.class, "./foo");
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    assertEquals("./foo", session.getName(a));
  }

  public void testCreateWithName() throws Exception {
    ChromatticSession session = login();

    //
    TLF_A a = session.create(TLF_A.class, "foo");
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    assertEquals("foo", session.getName(a));
  }

  public void testCreate() throws Exception {
    ChromatticSession session = login();

    //
    TLF_A a = session.create(TLF_A.class);
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    assertEquals(null, session.getName(a));
  }

  public void testNonChromatticClass() throws Exception {
    DomainSession session = login();
    try {
      session.create(Object.class);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException e) {
    }
    try {
      session.create(Object.class, "a");
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException e) {
    }
  }
}
