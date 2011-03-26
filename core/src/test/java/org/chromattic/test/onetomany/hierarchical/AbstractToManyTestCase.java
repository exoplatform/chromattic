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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.Node;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractToManyTestCase<O, M> extends AbstractTestCase {

  /** . */
  private Class<O> oneSide = getOneSideClass();

  /** . */
  private Class<M> manySide = getManySideClass();

  protected void createDomain() {
    addClass(oneSide);
    addClass(manySide);
  }

  protected abstract Class<O> getOneSideClass();

  protected abstract Class<M> getManySideClass();

  protected abstract Collection<M> getMany(O many);

  public void testAdd1() throws Exception {

    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    O a = session.findByNode(oneSide, aNode);
    assertNotNull(a);
    Collection<M> children = getMany(a);
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    Node bNode = aNode.addNode("b", "totm_b");
    assertEquals(1, children.size());
    M b = session.findByNode(manySide, bNode);
    assertTrue(children.contains(b));
  }

  public void testAdd2() throws Exception {

    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    O a = session.findByNode(oneSide, aNode);
    assertNotNull(a);
    Collection<M> children = getMany(a);
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    M b = session.create(manySide, "totm_b_a");
    assertTrue(children.add(b));
    assertEquals(1, children.size());
    assertTrue(children.contains(b));

    // Need to check underlying nodes
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    O a = session.findById(oneSide, aId);
    assertNotNull(a);
    Collection<M> children = getMany(a);
    assertNotNull(children);
    M b = session.findById(manySide, bId);
    assertTrue(children.contains(b));
  }

  public void testRemove() throws Exception {

    ChromatticSession session = login();

    O a = session.insert(oneSide, "totm_a_c");
    String aId = session.getId(a);
    M b = session.create(manySide, "totm_b_c");
    getMany(a).add(b);
    session.save();

    session = login();

    a = session.findById(oneSide, aId);
    b = getMany(a).iterator().next();
    assertNotNull(a);
    session.remove(a);
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
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
    c.add(many);
    if (save) session.save();
    c.remove(many);
    assertEquals(Status.REMOVED, session.getStatus(many));
    assertTrue(c.isEmpty());
  }

  private void testCollectionClear(boolean save) throws Exception {
    ChromatticSession session = login();
    O one = session.insert(oneSide, "totm_d");
    M many = session.create(manySide, "totm_e");
    Collection<M> c = getMany(one);
    c.add(many);
    if (save) session.save();
    c.clear();
    assertEquals(Status.REMOVED, session.getStatus(many));
    assertTrue(c.isEmpty());
  }

  private void testTransientCollectionIterator(boolean save) throws Exception {
    ChromatticSession session = login();
    O one = session.insert(oneSide, "totm_d");
    M many = session.create(manySide, "totm_e");
    Collection<M> c = getMany(one);
    c.add(many);
    if (save) session.save();
    Iterator<M> i = c.iterator();
    try {
      i.remove();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
    assertSame(many, i.next());
    i.remove();
    assertEquals(Status.REMOVED, session.getStatus(many));
    assertTrue(c.isEmpty());
    try {
      i.remove();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
  }
}
