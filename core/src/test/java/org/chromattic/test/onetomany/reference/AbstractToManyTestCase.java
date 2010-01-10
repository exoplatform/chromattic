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

import org.chromattic.core.api.ChromatticSessionImpl;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractToManyTestCase<O, M> extends AbstractLinkTestCase<O, M> {

  protected abstract O getOne(M many);

  protected abstract void setOne(M many, O one);

  public void testLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);
    createLink(bNode, "ref", aNode);

    O a = session.findByNode(oneClass, aNode);
    M b = session.findByNode(manyClass, bNode);
    assertSame(a, getOne(b));
  }

  public void testAddPersistent() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a = session.findByNode(oneClass, aNode);
    M b = session.findByNode(manyClass, bNode);

    //
    setOne(b, a);
    assertSame(a, getOne(b));
  }

  public void testAddTransient() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a = session.create(oneClass);
    M b = session.findByNode(manyClass, bNode);

    //
    try {
      setOne(b, a);
    }
    catch (IllegalStateException e) {
    }
    assertNull(getOne(b));
  }

  public void testRemove() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totmr_a", oneNT);
    Node bNode = rootNode.addNode("totmr_b", manyNT);

    //
    O a = session.findByNode(oneClass, aNode);
    M b = session.findByNode(manyClass, bNode);

    //
    setOne(b, a);

    //
    setOne(b, null);
    assertEquals(null, getOne(b));
  }
}
