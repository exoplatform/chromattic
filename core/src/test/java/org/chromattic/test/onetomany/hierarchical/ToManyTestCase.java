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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ToManyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTM_A_1.class);
    addClass(TOTM_B_1.class);
  }

  public void testAdd1() throws Exception {

    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    TOTM_A_1 a = session.findByNode(TOTM_A_1.class, aNode);
    assertNotNull(a);
    Collection<TOTM_B_1> children = a.getChildren();
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    Node bNode = aNode.addNode("b", "totm_b");
    assertEquals(1, children.size());
    TOTM_B_1 b = session.findByNode(TOTM_B_1.class, bNode);
    assertTrue(children.contains(b));
  }

  public void testAdd2() throws Exception {

    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    TOTM_A_1 a = session.findByNode(TOTM_A_1.class, aNode);
    assertNotNull(a);
    Collection<TOTM_B_1> children = a.getChildren();
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    TOTM_B_1 b = session.create(TOTM_B_1.class);
    b.setName("totm_b_a");
    assertTrue(children.add(b));
    assertEquals(1, children.size());
    assertTrue(children.contains(b));

    // Need to check underlying nodes
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    TOTM_A_1 a = session.findById(TOTM_A_1.class, aId);
    assertNotNull(a);
    Collection<TOTM_B_1> children = a.getChildren();
    assertNotNull(children);
    TOTM_B_1 b = session.findById(TOTM_B_1.class, bId);
    assertTrue(children.contains(b));
  }

  public void testRemove() throws Exception {

    ChromatticSession session = login();

    TOTM_A_1 a = session.create(TOTM_A_1.class, "totm_a_c");
    String aId = session.persist(a);
    TOTM_B_1 b = session.create(TOTM_B_1.class, "totm_b_c");
    a.getChildren().add(b);
    session.save();

    session = login();

    a = session.findById(TOTM_A_1.class, aId);
    b = a.getChildren().iterator().next();
    assertNotNull(a);
    session.remove(a);
    assertEquals(Status.REMOVED, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
  }
}