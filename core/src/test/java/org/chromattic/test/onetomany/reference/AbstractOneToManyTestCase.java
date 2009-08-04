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

import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractOneToManyTestCase<O, M> extends AbstractLinkTestCase<O, M> {

  protected abstract O getOne(M many);

  protected abstract void setOne(M many, O one);

  protected abstract Collection<M> getMany(O one);

  public void testAddPersistent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode1 = rootNode.addNode("totmr_a_1", oneNT);
    Node aNode2 = rootNode.addNode("totmr_a_2", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a1 = session.findByNode(oneClass, aNode1);
    O a2 = session.findByNode(oneClass, aNode2);
    M b = session.findByNode(manyClass, bNode);

    //
    setOne(b, a1);
    assertSame(a1, getOne(b));
    assertEquals(1, getMany(a1).size());
    assertTrue(getMany(a1).contains(b));
    assertEquals(0, getMany(a2).size());
    assertFalse(getMany(a2).contains(b));

    //
    setOne(b, a2);
    assertSame(a2, getOne(b));
    assertEquals(0, getMany(a1).size());
    assertFalse(getMany(a1).contains(b));
    assertEquals(1, getMany(a2).size());
    assertTrue(getMany(a2).contains(b));

    //
    getMany(a1).add(b);
    assertSame(a1, getOne(b));
    assertEquals(1, getMany(a1).size());
    assertTrue(getMany(a1).contains(b));
    assertEquals(0, getMany(a2).size());
    assertFalse(getMany(a2).contains(b));

    //
    getMany(a2).add(b);
    assertSame(a2, getOne(b));
    assertEquals(0, getMany(a1).size());
    assertFalse(getMany(a1).contains(b));
    assertEquals(1, getMany(a2).size());
    assertTrue(getMany(a2).contains(b));
  }

  public void testRemoveReferent() throws Exception {
    ChromatticSession session = login();

    //
    O a = session.insert(oneClass, "totmr_a_d");
    M b = session.insert(manyClass, "totmr_b_d");
    Collection<M> bs = getMany(a);
    bs.add(b);

    //
    session.remove(b);
    assertTrue(bs.isEmpty());
  }

  public void testRemoveReferenced() throws Exception {
    ChromatticSession session = login();

    //
    O a = session.insert(oneClass, "totmr_a_d");
    M b = session.insert(manyClass, "totmr_b_d");
    Collection<M> bs = getMany(a);
    bs.add(b);

    //
    session.remove(a);
    assertNull(getOne(b));
  }
}