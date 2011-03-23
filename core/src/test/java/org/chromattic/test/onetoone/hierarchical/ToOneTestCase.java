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

package org.chromattic.test.onetoone.hierarchical;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ToOneTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A2.class);
    addClass(B2.class);
  }

  public void testChildToParentAdd() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("toto_a_a", getNodeTypeName(A2.class));
    A2 a = session.findByNode(A2.class, aNode);
    assertNotNull(a);

    //
    Node bNode = aNode.addNode("b", getNodeTypeName(B2.class));
    B2 b = session.findByNode(B2.class, bNode);
    assertNotNull(b);
    assertSame(a, b.getA());
  }

  public void testChildToParentLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("toto_a_b", getNodeTypeName(A2.class));
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", getNodeTypeName(B2.class));
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    B2 b = session.findById(B2.class, bId);
    assertNotNull(b);
    A2 a = b.getA();
    assertNotNull(a);
    assertSame(a, session.findById(A2.class, aId));
  }

  public void testTyped() throws Exception {
    ChromatticSession session = login();

    //
    A2 a = session.create(A2.class, getNodeTypeName(A2.class));
    String aId = session.persist(a);
    Node aNode = session.getJCRSession().getNodeByUUID(aId);
    assertNotNull(aNode);

    //
    B2 b = session.create(B2.class);
    b.setA(a);

    //
    b.setA(null);
/*
    a.setB(b);
    assertSame(b, a.getB());
    Node bNode = aNode.getNode("b");
    assertNotNull(bNode);
    String bId = bNode.getUUID();

    //
    a.setB(null);
    assertNull(a.getB());
    assertNull(session.find(Object.class, bId));
    session.getJCRSession().getNodeByUUID(aId);
    try {
      session.getJCRSession().getNodeByUUID(bId);
      fail();
    }
    catch (ItemNotFoundException expected) {
    }
*/
  }
}