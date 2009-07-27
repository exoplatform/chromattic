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

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ToOneTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTO_A_2.class);
    addClass(TOTO_B_2.class);
  }

  public void testChildToParentAdd() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();

    //
    Node aNode = rootNode.addNode("toto_a_a", "toto_a");
    TOTO_A_2 a = session.findByNode(TOTO_A_2.class, aNode);
    assertNotNull(a);

    //
    Node bNode = aNode.addNode("b", "toto_b");
    TOTO_B_2 b = session.findByNode(TOTO_B_2.class, bNode);
    assertNotNull(b);
    assertSame(a, b.getA());
  }

  public void testChildToParentLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("toto_a_b", "toto_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "toto_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    TOTO_B_2 b = session.findById(TOTO_B_2.class, bId);
    assertNotNull(b);
    TOTO_A_2 a = b.getA();
    assertNotNull(a);
    assertSame(a, session.findById(TOTO_A_2.class, aId));
  }

  public void testTyped() throws Exception {
    ChromatticSession session = login();

    //
    TOTO_A_2 a = session.create(TOTO_A_2.class, "toto_a_a");
    String aId = session.persist(a);
    Node aNode = session.getJCRSession().getNodeByUUID(aId);
    assertNotNull(aNode);

    //
    TOTO_B_2 b = session.create(TOTO_B_2.class);
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