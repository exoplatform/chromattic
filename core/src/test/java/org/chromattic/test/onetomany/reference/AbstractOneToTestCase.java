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

package org.chromattic.test.onetomany.reference;

import org.chromattic.core.ChromatticSessionImpl;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractOneToTestCase<O, M> extends AbstractLinkTestCase<O, M> {

  protected abstract Collection<M> getMany(O one);

  public void testLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);
    createLink(bNode, "ref", aNode);
    rootNode.save(); // This is awkwardly required

    //
    O a = session.findByNode(oneClass, aNode);
    M b = session.findByNode(manyClass, bNode);
    Collection<M> bs = getMany(a);
    assertEquals(1, bs.size());
    assertTrue(bs.contains(b));

    //
    rootNode.save();

    //
    session = login();
    a = session.findByNode(oneClass, aNode);
    bs = getMany(a);
    assertEquals(1, bs.size());
  }

  public void testPersistent() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a = session.findByNode(oneClass, aNode);
    M b = session.findByNode(manyClass, bNode);
    Collection<M> bs = getMany(a);
    assertEquals(0, bs.size());

    //
    assertTrue(bs.add(b));
    assertEquals(1, bs.size());
    assertTrue(bs.contains(b));
    assertFalse(bs.add(b));
    assertEquals(1, bs.size());
    assertTrue(bs.contains(b));
    assertTrue(bs.remove(b));
    assertEquals(0, bs.size());
  }

  public void testMove() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode1 = rootNode.addNode("totmr_a_1", oneNT);
    Node aNode2 = rootNode.addNode("totmr_a_2", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a1 = session.findByNode(oneClass, aNode1);
    O a2 = session.findByNode(oneClass, aNode2);
    M b = session.findByNode(manyClass, bNode);
    Collection<M> bs1 = getMany(a1);
    Collection<M> bs2 = getMany(a2);
    assertEquals(0, bs1.size());
    assertEquals(0, bs2.size());

    //
    assertTrue(bs1.add(b));
    int i = bs1.size();
    assertEquals(1, i);
    assertTrue(bs1.contains(b));
    assertEquals(0, bs2.size());
    assertFalse(bs2.contains(b));

    //
    assertTrue(bs2.add(b));
    assertEquals(1, bs2.size());
    assertTrue(bs2.contains(b));
    assertEquals(0, bs1.size());
    assertFalse(bs1.contains(b));
  }

  public void testTransient() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);

    //
    O a = session.findByNode(oneClass, aNode);
    M b = session.create(manyClass);
    Collection<M> bs = getMany(a);
    assertEquals(0, bs.size());

    //
    try {
      bs.add(b);
      fail();
    }
    catch (IllegalStateException e) {
    }
    assertEquals(0, bs.size());
  }

  public void testRemove() throws Exception {
    ChromatticSession session = login();

    O a = session.create(oneClass, "totmr_a_c");
    String aId = session.persist(a);
    M b = session.insert(manyClass, "totmr_b_c");
    getMany(a).add(b);
    session.save();

    session = login();

    a = session.findById(oneClass, aId);
    session.remove(a);
    session.save();
  }
}
