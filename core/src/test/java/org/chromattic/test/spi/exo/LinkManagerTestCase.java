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

import org.chromattic.exo.RepositoryBootstrap;
import org.chromattic.common.Collections;
import org.chromattic.core.jcr.AbstractLinkManager;

import java.util.Iterator;
import java.util.ConcurrentModificationException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class LinkManagerTestCase extends TestCase {

  protected abstract AbstractLinkManager createLinkManager(Session session);

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
    AbstractLinkManager mgr = createLinkManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a5");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b5");

    //
    assertNull(mgr.setReferenced(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));
    
    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));
  }

  public void testRemoveTransient() throws Exception {
    Session session = repo.login();
    AbstractLinkManager mgr = createLinkManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a6");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b6");

    //
    assertEquals(null, mgr.setReferenced(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    assertEquals(a, mgr.setReferenced(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());
  }

  public void testRemovePersistent() throws Exception {
    Session session = repo.login();
    AbstractLinkManager mgr = createLinkManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a7");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b7");

    //
    assertEquals(null, mgr.setReferenced(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    assertEquals(a, mgr.setReferenced(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());
  }

  public void testReAddTransientlyRemovedPersistent() throws Exception {
    Session session = repo.login();
    AbstractLinkManager mgr = createLinkManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a8");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b8");

    //
    assertEquals(null, mgr.setReferenced(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    assertEquals(a, mgr.setReferenced(b, "ref", null));
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());

    //
    assertEquals(null, mgr.setReferenced(b, "ref", a));
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(Collections.set(b), Collections.set(mgr.getReferents(a, "ref")));
  }

  public void testUpdate() throws Exception {
    Session session = repo.login();
    AbstractLinkManager mgr = createLinkManager(session);
    Node root = session.getRootNode();
    Node a = root.addNode("a9");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b9");
    b.addMixin("mix:referenceable");
    Node c = root.addNode("c9");

    //
    assertEquals(null, mgr.setReferenced(c, "ref", a));
    assertEquals(Collections.set(c), Collections.set(mgr.getReferents(a, "ref")));
    assertEquals(0, Collections.set(mgr.getReferents(b, "ref")).size());

    //
    assertEquals(a, mgr.setReferenced(c, "ref", b));
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());
    assertEquals(Collections.set(c), Collections.set(mgr.getReferents(b, "ref")));

    //
    session.save();
    mgr = createLinkManager(session);
    assertEquals(0, Collections.set(mgr.getReferents(a, "ref")).size());
    assertEquals(Collections.set(c), Collections.set(mgr.getReferents(b, "ref")));
  }

  public void testPhantomConcurrentRemoveModification() throws Exception {
    Session session = repo.login();
    Node root = session.getRootNode();
    Node a = root.addNode("a10");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b10");
    session.save();

    AbstractLinkManager mgr = createLinkManager(session);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(b, "ref", a);
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
    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    session.save();

    mgr = createLinkManager(session);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(b, "ref", null);
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
    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    session.save();

    mgr = createLinkManager(session);
    Iterator i = mgr.getReferents(a, "ref");
    Iterator j = mgr.getReferents(a, "ref");
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
    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    session.save();

    mgr = createLinkManager(session);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(c, "ref", a);
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
    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    session.save();

    mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", null);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(b, "ref", a);
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

    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(b, "ref", null);
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

    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    Iterator i = mgr.getReferents(a, "ref");
    Iterator j = mgr.getReferents(a, "ref");
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

    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(c, "ref", a);
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

    AbstractLinkManager mgr = createLinkManager(session);
    mgr.setReferenced(b, "ref", a);
    mgr.setReferenced(b, "ref", null);
    Iterator i = mgr.getReferents(a, "ref");
    mgr.setReferenced(b, "ref", a);
    try {
      i.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }
}
