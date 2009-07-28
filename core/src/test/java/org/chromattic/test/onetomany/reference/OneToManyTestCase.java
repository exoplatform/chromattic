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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToManyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTMR_A_3.class);
    addClass(TOTMR_B_3.class);
  }

  public void testAddPersistent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode1 = rootNode.addNode("totmr_a_1", "totmr_a");
    Node aNode2 = rootNode.addNode("totmr_a_2", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_3 a1 = session.findByNode(TOTMR_A_3.class, aNode1);
    TOTMR_A_3 a2 = session.findByNode(TOTMR_A_3.class, aNode2);
    TOTMR_B_3 b = session.findByNode(TOTMR_B_3.class, bNode);

    //
    b.setA(a1);
    assertSame(a1, b.getA());
    assertEquals(1, a1.getBs().size());
    assertTrue(a1.getBs().contains(b));
    assertEquals(0, a2.getBs().size());
    assertFalse(a2.getBs().contains(b));

    //
    b.setA(a2);
    assertSame(a2, b.getA());
    assertEquals(0, a1.getBs().size());
    assertFalse(a1.getBs().contains(b));
    assertEquals(1, a2.getBs().size());
    assertTrue(a2.getBs().contains(b));

    //
    a1.getBs().add(b);
    assertSame(a1, b.getA());
    assertEquals(1, a1.getBs().size());
    assertTrue(a1.getBs().contains(b));
    assertEquals(0, a2.getBs().size());
    assertFalse(a2.getBs().contains(b));

    //
    a2.getBs().add(b);
    assertSame(a2, b.getA());
    assertEquals(0, a1.getBs().size());
    assertFalse(a1.getBs().contains(b));
    assertEquals(1, a2.getBs().size());
    assertTrue(a2.getBs().contains(b));
  }
}