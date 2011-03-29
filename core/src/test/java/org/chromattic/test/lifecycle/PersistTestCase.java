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

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class PersistTestCase extends AbstractTestCase {

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
    A b = session.create(A.class, "foo");

    //
    try {
      session.persist(b, "/");
      fail();
    }
    catch (IllegalArgumentException e) {
    }

    //
    try {
      session.persist(b, ".");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testWithParent() throws Exception {
    testWithParent(false);
  }

  public void testWithNullParent() throws Exception {
    testWithParent(true);
  }

  public void testWithParent(boolean nullParent) throws Exception {
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
    A child = session.create(A.class);
    session.persist(parent, child, "child");
    assertNotNull(child);
    assertEquals("child", session.getName(child));
    assertEquals(Status.PERSISTENT, session.getStatus(child));
    Node bNode = session.getRoot().getNode(childPath);
    assertEquals(session.getId(child), bNode.getUUID());

    //
    A c = session.create(A.class);

    //
    try {
      session.persist(parent, c, "/");
      fail();
    }
    catch (IllegalArgumentException e) {
    }

    //
    try {
      session.persist(parent, c, ".");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testWithParentAndImplicitName() throws Exception {
    testWithParentWithImplicitName(false);
  }

  public void testWithNullParentAndImplicitName() throws Exception {
    testWithParentWithImplicitName(true);
  }

  public void testWithParentWithImplicitName(boolean nullParent) throws Exception {
    ChromatticSessionImpl session = login();

    //
    A parent;
    if (nullParent) {
      parent = null;
    } else {
      parent = session.insert(A.class, "parent");
      assertNotNull(parent);
      assertEquals("parent", session.getName(parent));
      assertEquals(Status.PERSISTENT, session.getStatus(parent));
    }

    //
    A child = session.create(A.class, "child");
    assertEquals("child", session.getName(child));
    assertEquals(Status.TRANSIENT, session.getStatus(child));

    //
    String bId = session.persist(parent, child);
    assertNotNull(child);
    assertEquals("child", session.getName(child));
    assertEquals(Status.PERSISTENT, session.getStatus(child));
    assertEquals(bId, session.getId(child));

    //
    Node parentNode;
    if (nullParent) {
      parentNode = session.getRoot();
    } else {
      parentNode = session.getRoot().getNode("parent");
      assertEquals(session.getId(parent), parentNode.getUUID());
    }

    //
    Node childNode = parentNode.getNode("child");
    assertEquals(session.getId(child), childNode.getUUID());
  }

  public void testWithImplicitName() throws Exception {
    ChromatticSessionImpl session = login();

    //
    A a = session.create(A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    String aId = session.persist(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(aId, session.getId(a));

    //
    Node aNode = session.getRoot().getNode("tlf_a");

    //
    assertEquals(session.getId(a), aNode.getUUID());
  }

  public void testWithNoImplicitName() throws Exception {
    ChromatticSession session = login();

    //
    A a = session.create(A.class);
    assertNotNull(a);
    assertEquals(null, session.getName(a));
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    try {
      session.persist(a);
      fail();
    }
    catch (IllegalArgumentException e) {
    }
    assertEquals(null, session.getName(a));
    assertEquals(Status.TRANSIENT, session.getStatus(a));
  }

  public void testWithParentWithNoImplicitName() throws Exception {
    ChromatticSession session = login();

    //
    A a = session.insert(A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    A b = session.create(A.class);
    assertEquals(null, session.getName(b));
    assertEquals(Status.TRANSIENT, session.getStatus(b));
    try {
      session.persist(a, b);
      fail();
    }
    catch (IllegalArgumentException e) {
    }
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(null, session.getName(b));
    assertEquals(Status.TRANSIENT, session.getStatus(b));
  }

  public void testNonChromatticParent() throws Exception {
    ChromatticSession session = login();
    A a = session.create(A.class);
    assertThrowsIAE(session, new Object(), a);
    assertThrowsIAE(session, new Object(), a, "a");
  }

  public void testTransientParent() throws Exception {
    ChromatticSession session = login();
    A a = session.create(A.class);
    A b = session.create(A.class);
    assertThrowsIAE(session, a, b);
    assertThrowsIAE(session, a, b, "a");
  }

  public void testPersistentObject() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    A b = session.insert(A.class, "b");
    assertThrowsIAE(session, a, b);
    assertThrowsIAE(session, a, b, "a");
  }

  public void testNonChromatticObject() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "tlf_a");
    assertThrowsIAE(session, new Object());
    assertThrowsIAE(session, new Object(), "a");
    assertThrowsIAE(session, a, new Object());
    assertThrowsIAE(session, a, new Object(), "a");
  }

  private void assertThrowsIAE(ChromatticSession session, Object object) {
    try {
      session.persist(object);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  private void assertThrowsIAE(ChromatticSession session, Object object, String name) {
    try {
      session.persist(object, name);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  private void assertThrowsIAE(ChromatticSession session, Object parent, Object object) {
    try {
      session.persist(parent, object);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  private void assertThrowsIAE(ChromatticSession session, Object parent, Object object, String name) {
    try {
      session.persist(parent, object, name);
      fail("Was expecting an exception");
    }
    catch (IllegalArgumentException ignore) {
    }
  }
}