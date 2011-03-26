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

import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToTestCase extends AbstractOneToTestCase<TOTM_A_2, TOTM_B_2> {

  public Class<TOTM_A_2> getOneSideClass() {
    return TOTM_A_2.class;
  }

  public Class<TOTM_B_2> getManySideClass() {
    return TOTM_B_2.class;
  }

  public void setOne(TOTM_B_2 many, TOTM_A_2 one) {
    many.setParent(one);
  }

  public TOTM_A_2 getOne(TOTM_B_2 many) {
    return many.getParent();
  }

  public void testAdd() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    TOTM_A_2 a = session.findByNode(TOTM_A_2.class, aNode);
    assertNotNull(a);

    //
    Node bNode = aNode.addNode("b", "totm_b");
    TOTM_B_2 b = session.findByNode(TOTM_B_2.class, bNode);
    assertEquals(a, b.getParent());
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    TOTM_A_2 a = session.findById(TOTM_A_2.class, aId);
    assertNotNull(a);
    TOTM_B_2 b = session.findById(TOTM_B_2.class, bId);
    assertEquals(a, b.getParent());
  }

  public void testTransientGetParent() throws Exception {
    ChromatticSession session = login();
    TOTM_B_2 b = session.create(TOTM_B_2.class, "totm_b_c");
    try {
      b.getParent();
    }
    catch (IllegalStateException expected) {
    }
  }

  public void testRemovedGetParent() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    session = login();
    TOTM_B_2 b = session.findById(TOTM_B_2.class, bId);
    session.remove(b);
    try {
      b.getParent();
    }
    catch (IllegalStateException expected) {
    }
  }
}