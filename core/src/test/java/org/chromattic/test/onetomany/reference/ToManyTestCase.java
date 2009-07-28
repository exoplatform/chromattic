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
public class ToManyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTMR_A_2.class);
    addClass(TOTMR_B_2.class);
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");
    bNode.setProperty("ref", aNode);

    TOTMR_A_2 a = session.findByNode(TOTMR_A_2.class, aNode);
    TOTMR_B_2 b = session.findByNode(TOTMR_B_2.class, bNode);
    assertSame(a, b.getA());
  }

  public void testAddPersistent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_2 a = session.findByNode(TOTMR_A_2.class, aNode);
    TOTMR_B_2 b = session.findByNode(TOTMR_B_2.class, bNode);

    //
    b.setA(a);
    assertSame(a, b.getA());
  }

  public void testAddTransient() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_2 a = session.create(TOTMR_A_2.class);
    TOTMR_B_2 b = session.findByNode(TOTMR_B_2.class, bNode);

    //
    try {
      b.setA(a);
    }
    catch (IllegalStateException e) {
    }
    assertNull(b.getA());
  }

  public void testRemove() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_2 a = session.findByNode(TOTMR_A_2.class, aNode);
    TOTMR_B_2 b = session.findByNode(TOTMR_B_2.class, bNode);

    //
    b.setA(a);

    //
    b.setA(null);
    assertEquals(null, b.getA());
  }
}
