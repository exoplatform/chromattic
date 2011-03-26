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

import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.Status;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InsertTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TLF_A.class);
  }

  public void testFromRoot() throws Exception {
    DomainSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    Node aNode = session.getRoot().getNode("tlf_a");
    assertEquals(session.getId(a), aNode.getUUID());

    //
    try {
      session.insert(TLF_A.class, "/");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }

    //
    try {
      session.insert(TLF_A.class, ".");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testFromRelative() throws Exception {
    DomainSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));

    //
    TLF_A b = session.insert(a, TLF_A.class, "b");
    assertNotNull(b);
    assertEquals("b", session.getName(b));
    assertEquals(Status.PERSISTENT, session.getStatus(b));
    Node bNode = session.getRoot().getNode("tlf_a/b");
    assertEquals(session.getId(b), bNode.getUUID());

    //
    try {
      session.insert(a, TLF_A.class, "/");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }

    //
    try {
      session.insert(a, TLF_A.class, ".");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }
}
