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
package org.chromattic.test.onetomany.hierarchical.generic.map;

import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;
import org.chromattic.test.AbstractTestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DuplicateTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A4.class);
    addClass(B4.class);
  }

  public void testDuplicatePutSucceeds() throws Exception {
    ChromatticSession session = login();
    A4 a = session.insert(A4.class, "a");
    B4 b1 = session.create(B4.class);
    B4 b2 = session.create(B4.class);
    a.getChildren().put("b", b1);
    assertSame(b1, a.getChildren().put("b", b2));
    assertSame(b2, a.getChildren().get("b"));
    assertEquals(Status.REMOVED, session.getStatus(b1));
  }
}
