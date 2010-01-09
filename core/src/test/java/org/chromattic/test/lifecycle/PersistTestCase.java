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

import org.chromattic.api.ChromatticException;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PersistTestCase extends AbstractTestCase {

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
    TLF_A b = session.create(TLF_A.class, "foo");

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

  public void testFromParent() throws Exception {
    DomainSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));

    //
    TLF_A b = session.create(TLF_A.class);
    session.persist(a, b, "b");
    assertNotNull(b);
    assertEquals("b", session.getName(b));
    assertEquals(Status.PERSISTENT, session.getStatus(b));
    Node bNode = session.getRoot().getNode("tlf_a/b");
    assertEquals(session.getId(b), bNode.getUUID());

    //
    TLF_A c = session.create(TLF_A.class);

    //
    try {
      session.persist(a, c, "/");
      fail();
    }
    catch (IllegalArgumentException e) {
    }

    //
    try {
      session.persist(a, c, ".");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testWithParentWithImplicitName() throws Exception {
    DomainSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    TLF_A b = session.create(TLF_A.class, "b");
    assertEquals("b", session.getName(b));
    assertEquals(Status.TRANSIENT, session.getStatus(b));
    String bId = session.persist(a, b);
    assertNotNull(b);
    assertEquals("b", session.getName(b));
    assertEquals(Status.PERSISTENT, session.getStatus(b));
    assertEquals(bId, session.getId(b));

    //
    Node aNode = session.getRoot().getNode("tlf_a");
    Node bNode = aNode.getNode("b");

    //
    assertEquals(session.getId(a), aNode.getUUID());
    assertEquals(session.getId(b), bNode.getUUID());
  }

  public void testWithImplicitName() throws Exception {
    DomainSession session = login();

    //
    TLF_A a = session.create(TLF_A.class, "tlf_a");
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
    TLF_A a = session.create(TLF_A.class);
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
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    TLF_A b = session.create(TLF_A.class);
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
    TLF_A a = session.create(TLF_A.class);
    assertThrowsIAE(session, new Object(), a);
    assertThrowsIAE(session, new Object(), a, "a");
  }

  public void testTransientParent() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.create(TLF_A.class);
    TLF_A b = session.create(TLF_A.class);
    assertThrowsIAE(session, a, b);
    assertThrowsIAE(session, a, b, "a");
  }

  public void testPersistentObject() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.insert(TLF_A.class, "a");
    TLF_A b = session.insert(TLF_A.class, "b");
    assertThrowsIAE(session, a, b);
    assertThrowsIAE(session, a, b, "a");
  }

  public void testNonChromatticObject() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
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