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

import org.chromattic.api.ChromatticSession;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.Status;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InsertTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testFromRoot() throws Exception {
    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    Node aNode = session.getRoot().getNode("tlf_a");
    assertEquals(session.getId(a), aNode.getUUID());

    //
    try {
      session.insert(A.class, "/");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }

    //
    try {
      session.insert(A.class, ".");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testWithParent() throws Exception {
    testWithParent(false);
  }

  public void testWithNullParent() throws Exception {
    testWithParent(true);
  }

  private void testWithParent(boolean nullParent) throws Exception {
    ChromatticSessionImpl session = login();

    //
    A parent;
    String childPath;
    if (nullParent) {
      parent = null;
      childPath = "child";
    } else {
      parent = session.insert(A.class, "parent");
      assertNotNull(parent);
      assertEquals("parent", session.getName(parent));
      assertEquals(Status.PERSISTENT, session.getStatus(parent));
      childPath = "parent/child";
    }

    //
    A child = session.insert(parent, A.class, "child");
    assertNotNull(child);
    assertEquals("child", session.getName(child));
    assertEquals(Status.PERSISTENT, session.getStatus(child));
    Node bNode = session.getRoot().getNode(childPath);
    assertEquals(session.getId(child), bNode.getUUID());

    //
    try {
      session.insert(parent, A.class, "/");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }

    //
    try {
      session.insert(parent, A.class, ".");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public void testNonChromatticParent() throws Exception {
    ChromatticSessionImpl session = login();
    assertThrowsIAE(session, new Object(), Object.class, "a");
  }

  public void testNonPersistentParent() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.create(A.class);
    assertThrowsIAE(session, a, Object.class, "a");
  }

  public void testNonChromatticObject() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "tlf_a");
    assertThrowsIAE(session, Object.class, "a");
    assertThrowsIAE(session, a, Object.class, "a");
  }

  private void assertThrowsIAE(ChromatticSession session, Class<?> objectClass, String name) {
    try {
      session.insert(objectClass, name);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  private void assertThrowsIAE(ChromatticSession session, Object parent, Class<?> objectClass, String name) {
    try {
      session.insert(parent, objectClass, name);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }
}
