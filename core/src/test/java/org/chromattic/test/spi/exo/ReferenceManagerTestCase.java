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
package org.chromattic.test.spi.exo;

import junit.framework.TestCase;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.Property;

import org.chromattic.exo.RepositoryBootstrap;
import org.chromattic.common.Collections;
import org.chromattic.util.JCR;
import org.chromattic.core.jcr.ReferenceManager;

import java.util.Iterator;
import java.util.ConcurrentModificationException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReferenceManagerTestCase extends TestCase {

  /** . */
  private Repository repo;

  @Override
  protected void setUp() throws Exception {
    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();

    //
    this.repo = repo;
  }

  public void testAdd() throws Exception {
    Session session = repo.login();
    ReferenceManager mgr = new ReferenceManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a5");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b5");

    //
    assertNull(mgr.setReference(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));
    
    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));
  }

  public void testRemoveTransient() throws Exception {
    Session session = repo.login();
    ReferenceManager mgr = new ReferenceManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a6");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b6");

    //
    assertEquals(null, mgr.setReference(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    assertEquals(a, mgr.setReference(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());
  }

  public void testRemovePersistent() throws Exception {
    Session session = repo.login();
    ReferenceManager mgr = new ReferenceManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a7");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b7");

    //
    assertEquals(null, mgr.setReference(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    assertEquals(a, mgr.setReference(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());
  }

  public void testReAddTransientlyRemovedPersistent() throws Exception {
    Session session = repo.login();
    ReferenceManager mgr = new ReferenceManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a8");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b8");

    //
    assertEquals(null, mgr.setReference(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    assertEquals(a, mgr.setReference(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());

    //
    assertEquals(null, mgr.setReference(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferences(a, "ref")));
  }

  public void testUpdate() throws Exception {
    Session session = repo.login();
    ReferenceManager mgr = new ReferenceManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a9");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b9");
    b.addMixin("mix:referenceable");
    Node c = root.addNode("c9");

    //
    assertEquals(null, mgr.setReference(c, "ref", a));
    assertEquals(Collections.set(c), Collections.set(mgr.getReferences(a, "ref")));
    assertEquals(0, Collections.set(mgr.getReferences(b, "ref")).size());

    //
    assertEquals(a, mgr.setReference(c, "ref", b));
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());
    assertEquals(Collections.set(c), Collections.set(mgr.getReferences(b, "ref")));

    //
    session.save();
    mgr = new ReferenceManager(session);
    assertEquals(0, Collections.set(mgr.getReferences(a, "ref")).size());
    assertEquals(Collections.set(c), Collections.set(mgr.getReferences(b, "ref")));
  }

  public void testPhantomConcurrentRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(b, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testPersistentConcurrentRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    b.setProperty("ref", a);
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(b, "ref", null);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testPersistentConcurrentIteratorRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    b.setProperty("ref", a);
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    Iterator i = mgr.getReferences(a, "ref");
    Iterator j = mgr.getReferences(a, "ref");
    j.next();
    j.remove();
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testPersistentConcurrentAddModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a11");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b11");
    Node c = root.addNode("c11");
    b.setProperty("ref", a);
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(c, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testPersistentConcurrentReAddModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    b.setProperty("ref", a);
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    mgr.setReference(b, "ref", null);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(b, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }







  public void testTransientConcurrentRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    mgr.setReference(b, "ref", a);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(b, "ref", null);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testTransientConcurrentIteratorRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    mgr.setReference(b, "ref", a);
    Iterator i = mgr.getReferences(a, "ref");
    Iterator j = mgr.getReferences(a, "ref");
    j.next();
    j.remove();
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testTransientConcurrentAddModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a11");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b11");
    Node c = root.addNode("c11");
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    mgr.setReference(b, "ref", a);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(c, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testTransientConcurrentReAddModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    session.save();

    ReferenceManager mgr = new ReferenceManager(session);
    mgr.setReference(b, "ref", a);
    mgr.setReference(b, "ref", null);
    Iterator i = mgr.getReferences(a, "ref");
    mgr.setReference(b, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testAddRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a1");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b1");

    //
    Property ref = b.setProperty("ref", a);
    assertTrue(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());

    //
    session.save();
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));
  }

  public void testRemoveRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a2");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b2");
    b.setProperty("ref", a);
    session.save();

    //
    session = repo.login();
    root = session.getRootNode();
    a = root.getNode("a2");
    b = root.getNode("b2");
    Property ref = b.getProperty("ref");
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    ref.remove();
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    session.save();
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
  }

  public void testReAddRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a3");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b3");
    b.setProperty("ref", a);
    session.save();

    //
    session = repo.login();
    root = session.getRootNode();
    a = root.getNode("a3");
    b = root.getNode("b3");
    Property ref = b.getProperty("ref");

    //
    ref.remove();
    b.setProperty("ref", a);
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    session.save();
    b.setProperty("ref", a);
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));
  }

  public void testUpdateRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a4");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b4");
    b.addMixin("mix:referenceable");
    Node c = root.addNode("c4");
    Property ref = c.setProperty("ref", a);

    //
    assertTrue(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(0, Collections.set(JCR.adapt(b.getReferences())).size());

    //
    c.setProperty("ref", b);
    assertTrue(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(0, Collections.set(JCR.adapt(b.getReferences())).size());

    //
    session.save();
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(b.getReferences())));
  }
}
