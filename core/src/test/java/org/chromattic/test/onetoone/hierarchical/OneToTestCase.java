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
import org.chromattic.api.Status;
import org.chromattic.api.DuplicateNameException;

import javax.jcr.Node;
import javax.jcr.ItemNotFoundException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A1.class);
    addClass(B1.class);
  }

  public void testJCR() throws Exception {
    ChromatticSessionImpl session = login();

    //
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("toto_a_a", getNodeTypeName(A1.class));
    String aId = aNode.getUUID();
    A1 a = session.findByNode(A1.class, aNode);
    assertNotNull(a);
    assertNull(a.getB());

    //
    Node bNode = aNode.addNode("b", getNodeTypeName(B1.class));
    String bId = bNode.getUUID();
    B1 b = a.getB();
    assertNotNull(b);

    //
    rootNode.save();

    session = login();
    a = session.findById(A1.class, aId);
    assertNotNull(a);
    b = a.getB();
    assertSame(b, session.findById(B1.class, bId));

    //
    bNode = session.getJCRSession().getNodeByUUID(bId);
    bNode.remove();
    assertNull(a.getB());

    //
    session.getJCRSession().save();
    assertNull(a.getB());

    //
    session = login();
    a = session.findById(A1.class, aId);
    b = a.getB();
    assertNull(b);
  }

  public void testTyped() throws Exception {
    ChromatticSession session = login();

    //
    A1 a = session.create(A1.class, getNodeTypeName(A1.class));
    String aId = session.persist(a);
    Node aNode = session.getJCRSession().getNodeByUUID(aId);
    assertNotNull(aNode);

    //
    B1 b = session.create(B1.class);
    a.setB(b);
    assertSame(b, a.getB());
    Node bNode = aNode.getNode("b");
    assertNotNull(bNode);
    String bId = bNode.getUUID();

    //
    B1 b2 = session.create(B1.class);
    try {
      a.setB(b2);
    }
    catch (DuplicateNameException e) {
    }

    //
    a.setB(null);
    assertNull(a.getB());
    assertNull(session.findById(Object.class, bId));
    assertEquals(Status.REMOVED, session.getStatus(b));
    session.getJCRSession().getNodeByUUID(aId);
    try {
      session.getJCRSession().getNodeByUUID(bId);
      fail();
    }
    catch (ItemNotFoundException expected) {
    }
  }
}
