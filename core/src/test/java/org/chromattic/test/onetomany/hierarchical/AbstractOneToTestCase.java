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
import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractOneToTestCase<O, M> extends AbstractTestCase {

  /** . */
  private final Class<O> oneSide = TypeLiteral.get(getClass(), 0);

  /** . */
  private final Class<M> manySide = TypeLiteral.get(getClass(), 1);

  protected void createDomain() {
    addClass(oneSide);
    addClass(manySide);
  }

  public abstract void setOne(M many, O one);

  public abstract O getOne(M many);

  public final void testAddChild() throws Exception {
    DomainSession session = login();
    O o = session.insert(oneSide, "a");
    M m = session.create(manySide, "b");
    setOne(m, o);
    assertSame(o, getOne(m));
  }

  public final void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    O a = session.findById(oneSide, aId);
    assertNotNull(a);
    M b = session.findById(manySide, bId);
    assertEquals(a, getOne(b));
  }

  public final void testTransientGetParent() throws Exception {
    ChromatticSession session = login();
    M b = session.create(manySide, "totm_b_c");
    try {
      getOne(b);
    }
    catch (IllegalStateException expected) {
    }
  }

  public final void testMoveToNonPersistentParent() throws Exception {
    DomainSession session = login();
    O o = session.create(oneSide, "a");
    M m = session.insert(manySide, "b");
    try {
      setOne(m, o);
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }

  public final void testRemovedGetParent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    session = login();
    M b = session.findById(manySide, bId);
    session.remove(b);
    try {
      getOne(b);
    }
    catch (IllegalStateException expected) {
    }
  }

  public final void testMove() throws Exception {
    DomainSession session = login();
    O o1 = session.insert(oneSide, "o1");
    O o2 = session.insert(oneSide, "o2");
    String o2Id = session.getId(o2);
    M m = session.insert(o1, manySide, "m");
    String mId = session.getId(m);
    setOne(m, o2);
    assertSame(o2, getOne(m));
    session.save();

    //
    o2 = session.findById(oneSide, o2Id);
    m = session.findById(manySide, mId);
    assertSame(o2, getOne(m));
  }
}