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
import org.chromattic.test.support.EventQueue;
import org.chromattic.test.support.LifeCycleEventType;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.jcr.ItemNotFoundException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class LifeCycleTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testSameClass() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.create(A.class, "tlf_a_c");
    A b = session.create(A.class, "tlf_a_d");
    assertSame(a.getClass(), b.getClass());
  }

  public void testLoad() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    String id = rootNode.addNode("tlf_a_a", "lifecycle:a").getUUID();
    rootNode.save();

    //
    session = login();
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    A.constructed = 0;
    A a = session.findById(A.class, id);
    listener.assertLifeCycleEvent(LifeCycleEventType.LOADED, session.getId(a), session.getPath(a), session.getName(a), a);
    listener.assertEmpty();
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(1, A.constructed);
    assertNotNull(a);
  }

  public void testAdd() throws RepositoryException {
    ChromatticSession session = login();
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    A.constructed = 0;
    A a = session.insert(A.class, "tlf_a_b");
    listener.assertLifeCycleEvent(LifeCycleEventType.CREATED, null, null, null, a);
    listener.assertLifeCycleEvent(LifeCycleEventType.ADDED, session.getId(a), session.getPath(a), session.getName(a), a);
    listener.assertEmpty();
    assertEquals(1, A.constructed);
    String id = session.getId(a);
    A b = session.findById(A.class, id);
    listener.assertEmpty();
    assertNotNull(b);
    assertEquals(1, A.constructed);
    assertEquals(Status.PERSISTENT, session.getStatus(b));
  }

  public void testPersist() throws Exception {
    ChromatticSession session = login();
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    A a = session.create(A.class, "tlf_a_c");
    listener.assertLifeCycleEvent(LifeCycleEventType.CREATED, null, null, null, a);
    listener.assertEmpty();
    assertEquals(Status.TRANSIENT, session.getStatus(a));
    String id = session.persist(a);
    listener.assertLifeCycleEvent(LifeCycleEventType.ADDED, session.getId(a), session.getPath(a), session.getName(a), a);
    listener.assertEmpty();
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    A a2 = session.findById(A.class, id);
    assertSame(a2, a);
  }

  public void testRemoveTransient() throws Exception {
    testRemoveTransient(false);
  }

  public void testDestroyTransient() throws Exception {
    testRemoveTransient(true);
  }

  public void testRemovePersistentUnsaved() throws Exception {
    testRemovePersistentUnsaved(false);
  }

  public void testDestroyPersistentUnsaved() throws Exception {
    testRemovePersistentUnsaved(true);
  }

  public void testRemovePersistentSaved() throws Exception {
    testRemovePersistentSaved(false);
  }

  public void testDestroyPersistentSaved() throws Exception {
    testRemovePersistentSaved(true);
  }

  private void testRemoveTransient(boolean withMethod) throws Exception {
    ChromatticSession session = login();
    A a = session.create(A.class, "tlf_a_c");
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    try {
      if (withMethod) {
        a.destroy();
      } else {
        session.remove(a);
      }
      fail();
    }
    catch (IllegalStateException e) { }
    listener.assertEmpty();
  }

  private void testRemovePersistentUnsaved(boolean withMethod) throws Exception {
    ChromatticSession session = login();
    A a = session.create(A.class, "tlf_a_c");
    String aId = session.persist(a);
    String aPath = session.getPath(a);
    String aName = session.getName(a);
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    if (withMethod) {
      a.destroy();
    } else {
      session.remove(a);
    }
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, aId, aPath, aName, a);
    listener.assertEmpty();
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertNull(session.findById(A.class, aId));
    try {
      session.getJCRSession().getNodeByUUID(aId);
      fail();
    }
    catch (ItemNotFoundException e) { }
    try {
      a.destroy();
      fail();
    }
    catch (IllegalStateException e) { }
  }

  private void testRemovePersistentSaved(boolean withMethod) throws Exception {
    ChromatticSession session = login();
    A a = session.create(A.class, "tlf_a_c");
    String aId = session.persist(a);
    String aPath = session.getPath(a);
    String aName = session.getName(a);
    session.save();

    //
    session = login() ;
    a = session.findById(A.class, aId);
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    listener.assertEmpty();
    if (withMethod) {
      a.destroy();
    } else {
      session.remove(a);
    }
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, aId, aPath, aName, a);
    listener.assertEmpty();
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertNull(session.findById(A.class, aId));
    try {
      session.getJCRSession().getNodeByUUID(aId);
      fail();
    }
    catch (ItemNotFoundException e) {
    }
    try {
      if (withMethod) {
        a.destroy();
      } else {
        session.remove(a);
      }
      fail();
    }
    catch (IllegalStateException e) { }
  }
}
