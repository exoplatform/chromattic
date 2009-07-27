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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;
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
    addClass(TOTO_A_1.class);
    addClass(TOTO_B_1.class);
  }

  public void testJCR() throws Exception {
    DomainSession session = login();

    //
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("toto_a_a", "toto_a");
    String aId = aNode.getUUID();
    TOTO_A_1 a = session.findByNode(TOTO_A_1.class, aNode);
    assertNotNull(a);
    assertNull(a.getB());

    //
    Node bNode = aNode.addNode("b", "toto_b");
    String bId = bNode.getUUID();
    TOTO_B_1 b = a.getB();
    assertNotNull(b);

    //
    rootNode.save();

    session = login();
    a = session.findById(TOTO_A_1.class, aId);
    assertNotNull(a);
    b = a.getB();
    assertSame(b, session.findById(TOTO_B_1.class, bId));

    //
    bNode = session.getJCRSession().getNodeByUUID(bId);
    bNode.remove();
    assertNull(a.getB());

    //
    session.getJCRSession().save();
    assertNull(a.getB());

    //
    session = login();
    a = session.findById(TOTO_A_1.class, aId);
    b = a.getB();
    assertNull(b);
  }

  public void testTyped() throws Exception {
    ChromatticSession session = login();

    //
    TOTO_A_1 a = session.create(TOTO_A_1.class, "toto_a_a");
    String aId = session.persist(a);
    Node aNode = session.getJCRSession().getNodeByUUID(aId);
    assertNotNull(aNode);

    //
    TOTO_B_1 b = session.create(TOTO_B_1.class);
    a.setB(b);
    assertSame(b, a.getB());
    Node bNode = aNode.getNode("b");
    assertNotNull(bNode);
    String bId = bNode.getUUID();

    //
    TOTO_B_1 b2 = session.create(TOTO_B_1.class);
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
