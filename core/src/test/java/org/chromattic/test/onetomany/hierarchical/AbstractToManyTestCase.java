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
package org.chromattic.test.onetomany.hierarchical;

import org.chromattic.common.TypeLiteral;
import org.chromattic.core.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.Node;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractToManyTestCase<O, M> extends AbstractTestCase {

  /** . */
  private final Class<O> oneSide = TypeLiteral.get(getClass(), 0);

  /** . */
  private final Class<M> manySide = TypeLiteral.get(getClass(), 1);

  protected void createDomain() {
    addClass(oneSide);
    addClass(manySide);
  }

  public abstract Collection<M> getMany(O many);

  public abstract void add(O o, M m);

  public abstract boolean supportsAddToCollection();

  public void testAdd() throws Exception {
    ChromatticSessionImpl session = login();

    //
    O o = session.insert(oneSide, "o");
    assertNotNull(o);
    Collection<M> ms = getMany(o);
    assertNotNull(ms);
    assertEquals(0, ms.size());

    //
    M m = session.create(manySide, "m");
    add(o, m);
    assertEquals(1, ms.size());
    assertTrue(ms.contains(m));
  }

  public void testAddRemovedChild() {
    if (supportsAddToCollection()) {
      ChromatticSessionImpl session = login();

      //
      M m = session.create(manySide, "m");
      O o1 = session.insert(oneSide, "o1");
      O o2 = session.insert(oneSide, "o2");

      //
      add(o1, m);
      session.remove(m);
      try {
        add(o2, m);
        fail();
      }
      catch (IllegalArgumentException ignore) {
      }
    } else {
      // todo: we cannot get name from removed child as this would throw an ISE, need to investigate
    }
  }

  public void addToRemovedParent() {
    ChromatticSessionImpl session = login();

    //
    M m = session.create(manySide, "m");
    O o = session.insert(oneSide, "o");

    //
    session.remove(o);
    try {
      add(o, m);
      fail();
    }
    catch (IllegalStateException ignore) {
    }
  }

  public void testLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node oNode = rootNode.addNode("o", "totm_a");
    String oId = oNode.getUUID();
    Node mNode = oNode.addNode("m", "totm_b");
    String mId = mNode.getUUID();
    rootNode.save();

    //
    session = login();
    O o = session.findById(oneSide, oId);
    assertNotNull(o);
    Collection<M> children = getMany(o);
    assertNotNull(children);
    M b = session.findById(manySide, mId);
    assertTrue(children.contains(b));
  }

  public void testRemoveChild() throws Exception {
    ChromatticSession session = login();

    //
    O o = session.insert(oneSide, "o");
    String oId = session.getId(o);
    M m = session.create(manySide, "m");
    add(o, m);
    session.save();

    //
    session = login();

    //
    o = session.findById(oneSide, oId);
    m = getMany(o).iterator().next();
    assertNotNull(o);
    session.remove(o);
    assertEquals(Status.REMOVED, session.getStatus(o));
    assertEquals(Status.REMOVED, session.getStatus(m));
  }

  public void testMove() throws Exception {
    ChromatticSessionImpl session = login();
    O o1 = session.insert(oneSide, "o1");
    String o1Id = session.getId(o1);
    O o2 = session.insert(oneSide, "o2");
    String o2Id = session.getId(o2);
    M m = session.insert(o1, manySide, "m");
    String mId = session.getId(m);
    Collection<M> ms1 = getMany(o1);
    Collection<M> ms2 = getMany(o2);
    add(o2, m);
    assertEquals(Collections.<Object>emptySet(), new HashSet<Object>(ms1));
    assertEquals(Collections.singleton(m), new HashSet<Object>(ms2));
    session.save();

    //
    o1 = session.findById(oneSide, o1Id);
    o2 = session.findById(oneSide, o2Id);
    ms1 = getMany(o1);
    ms2 = getMany(o2);
    m = session.findById(manySide, mId);
    assertEquals(Collections.<Object>emptySet(), new HashSet<Object>(ms1));
    assertEquals(Collections.singleton(m), new HashSet<Object>(ms2));
  }

  public void testTypeSafety() throws Exception {
    if (supportsAddToCollection()) {
      ChromatticSessionImpl session = login();
      O o1 = session.insert(oneSide, "o1");
      O o2 = session.insert(oneSide, "o2");
      Collection m = getMany(o1);
      try {
        m.add(o2);
        fail();
      }
      catch (ClassCastException e) {
      }
    }
  }

  public void testAddNull() throws Exception {
    if (supportsAddToCollection()) {
      ChromatticSessionImpl session = login();
      O o1 = session.insert(oneSide, "o1");
      Collection<M> m = getMany(o1);
      try {
        m.add(null);
        fail();
      }
      catch (NullPointerException e) {
      }
    }
  }

  public void testTransientCollectionClear() throws Exception {
    testCollectionClear(false);
  }

  public void testTransientCollectionRemove() throws Exception {
    testCollectionRemove(false);
  }

  public void testTransientCollectionIterator() throws Exception {
    testTransientCollectionIterator(false);
  }

  public void testPersistentCollectionClear() throws Exception {
    testCollectionClear(true);
  }

  public void testPersistentCollectionRemove() throws Exception {
    testCollectionRemove(true);
  }

  public void testPersistentCollectionIterator() throws Exception {
    testTransientCollectionIterator(true);
  }

  private void testCollectionRemove(boolean save) throws Exception {
    ChromatticSession session = login();
    O one = session.insert(oneSide, "totm_d");
    M many = session.create(manySide, "totm_e");
    Collection<M> c = getMany(one);
    add(one, many);
    if (save) session.save();
    c.remove(many);
    assertEquals(Status.REMOVED, session.getStatus(many));
    assertTrue(c.isEmpty());
  }

  private void testCollectionClear(boolean save) throws Exception {
    ChromatticSession session = login();
    O o = session.insert(oneSide, "o");
    M m = session.create(manySide, "m");
    Collection<M> c = getMany(o);
    add(o, m);
    if (save) session.save();
    c.clear();
    assertEquals(Status.REMOVED, session.getStatus(m));
    assertTrue(c.isEmpty());
  }

  private void testTransientCollectionIterator(boolean save) throws Exception {
    ChromatticSession session = login();
    O o = session.insert(oneSide, "p");
    M m = session.create(manySide, "m");
    Collection<M> c = getMany(o);
    add(o, m);
    if (save) session.save();
    Iterator<M> i = c.iterator();
    try {
      i.remove();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
    assertSame(m, i.next());
    i.remove();
    assertEquals(Status.REMOVED, session.getStatus(m));
    assertTrue(c.isEmpty());
    try {
      i.remove();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
  }
}
