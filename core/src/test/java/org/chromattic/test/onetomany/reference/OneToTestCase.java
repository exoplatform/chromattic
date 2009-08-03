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
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTMR_A_1.class);
    addClass(TOTMR_B_1.class);
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");
    bNode.setProperty("ref", aNode);
    rootNode.save(); // This is awkwardly required

    //
    TOTMR_A_1 a = session.findByNode(TOTMR_A_1.class, aNode);
    TOTMR_B_1 b = session.findByNode(TOTMR_B_1.class, bNode);
    Collection<TOTMR_B_1> bs = a.getBs();
    assertEquals(1, bs.size());
    assertTrue(bs.contains(b));

    //
    rootNode.save();

    //
    session = login();
    a = session.findByNode(TOTMR_A_1.class, aNode);
    bs = a.getBs();
    assertEquals(1, bs.size());
  }

  public void testPersistent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_1 a = session.findByNode(TOTMR_A_1.class, aNode);
    TOTMR_B_1 b = session.findByNode(TOTMR_B_1.class, bNode);
    Collection<TOTMR_B_1> bs = a.getBs();
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
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode1 = rootNode.addNode("totmr_a_1", "totmr_a");
    Node aNode2 = rootNode.addNode("totmr_a_2", "totmr_a");
    Node bNode = rootNode.addNode("totmr_b", "totmr_b");

    //
    TOTMR_A_1 a1 = session.findByNode(TOTMR_A_1.class, aNode1);
    TOTMR_A_1 a2 = session.findByNode(TOTMR_A_1.class, aNode2);
    TOTMR_B_1 b = session.findByNode(TOTMR_B_1.class, bNode);
    Collection<TOTMR_B_1> bs1 = a1.getBs();
    Collection<TOTMR_B_1> bs2 = a2.getBs();
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
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totmr_a", "totmr_a");

    //
    TOTMR_A_1 a = session.findByNode(TOTMR_A_1.class, aNode);
    TOTMR_B_1 b = session.create(TOTMR_B_1.class);
    Collection<TOTMR_B_1> bs = a.getBs();
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

    TOTMR_A_1 a = session.create(TOTMR_A_1.class, "totmr_a_c");
    String aId = session.persist(a);
    TOTMR_B_1 b = session.insert(TOTMR_B_1.class, "totmr_b_c");
    a.getBs().add(b);
    session.save();

    session = login();

    a = session.findById(TOTMR_A_1.class, aId);
    session.remove(a);
    session.save();
  }
}
