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
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.support.EventQueue;
import org.chromattic.test.support.LifeCycleEventType;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.nodetype.ConstraintViolationException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DestroyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TLF_A.class);
    addClass(M1.class);
    addClass(M2.class);
  }

  public void testTransitiveDestroy() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.insert(TLF_A.class, "bar");
    String aId = session.getId(a);
    String aPath = session.getPath(a);
    String aName = session.getName(a);
    TLF_A b = session.insert(a, TLF_A.class, "foo");
    String bId = session.getId(b);
    String bPath = session.getPath(b);
    String bName = session.getName(b);
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    session.remove(a);
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, bId, bPath, bName, b);
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, aId, aPath, aName, a);
    listener.assertEmpty();
    session.close();
  }

  public void testDestroyTransitiveAbsentChild() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.insert(TLF_A.class, "bar");
    String aId = session.getId(a);
    String aPath = session.getPath(a);
    String aName = session.getName(a);
    TLF_A b = session.insert(a, TLF_A.class, "foo");
    session.save();
    session.close();

    session = login();
    a = session.findByPath(TLF_A.class, "bar");
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    session.remove(a);
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, aId, aPath, aName, a);
    listener.assertEmpty();
    session.save();
  }

  public void testDestroyTransitiveLoadedDescendantWithAbsentParent() throws Exception {
    ChromatticSession session = login();
    TLF_A a = session.insert(TLF_A.class, "bar");
    String aId = session.getId(a);
    String aPath = session.getPath(a);
    String aName = session.getName(a);
    TLF_A b = session.insert(a, TLF_A.class, "foo");
    TLF_A c = session.insert(b, TLF_A.class, "foo");
    String cId = session.getId(c);
    String cPath = session.getPath(c);
    String cName = session.getName(c);
    session.save();
    session.close();

    session = login();
    a = session.findByPath(TLF_A.class, "bar");
    c = session.findById(TLF_A.class, cId);
    EventQueue listener = new EventQueue();
    session.addEventListener(listener);
    session.remove(a);
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(c));
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, cId, cPath, cName, c);
    listener.assertLifeCycleEvent(LifeCycleEventType.REMOVED, aId, aPath, aName, a);
    listener.assertEmpty();
    session.save();
  }

  public void testDestroyWithMandatoryChild() throws Exception {
    ChromatticSession session = login();

    M1 m1 = session.insert(M1.class, "m");
    M2 m2 = session.create(M2.class);
    m1.setMandatory(m2);

    session.save();

    try {
      session.remove(m2);
      fail();
    }
    catch (UndeclaredRepositoryException e) {
      assertTrue(e.getCause() instanceof ConstraintViolationException);
    }

    //
    session.remove(m1);

    //
    session.save();
  }
}
