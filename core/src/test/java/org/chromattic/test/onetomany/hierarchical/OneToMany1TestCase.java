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

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToMany1TestCase extends AbstractTestCase {


  protected void createDomain() {
    addClass(TOTM_A_3.class);
    addClass(TOTM_B_3.class);
  }

  public void testAdd() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    TOTM_A_3 a = session.findByNode(TOTM_A_3.class, aNode);
    assertNotNull(a);
    Collection<TOTM_B_3> children = a.getChildren();
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    Node bNode = aNode.addNode("b", "totm_b");
    TOTM_B_3 b = session.findByNode(TOTM_B_3.class, bNode);
    assertEquals(a, b.getParent());
    assertTrue(children.contains(b));
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
    TOTM_A_3 a = session.findById(TOTM_A_3.class, aId);
    assertNotNull(a);
    TOTM_B_3 b = session.findById(TOTM_B_3.class, bId);
    assertEquals(a, b.getParent());
    assertEquals(a, b.getParent());
    Collection<TOTM_B_3> children = a.getChildren();
    assertNotNull(children);
    assertTrue(children.contains(b));
  }
}