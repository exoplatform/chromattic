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

package org.chromattic.test.onetomany.hierarchical.map;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.format.NameSyntaxException;

import javax.jcr.Node;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToManyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTMHM_A.class);
    addClass(TOTMHM_B.class);
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
    TOTMHM_A a = session.findById(TOTMHM_A.class, aId);
    assertNotNull(a);
    TOTMHM_B b = session.findById(TOTMHM_B.class, bId);
    assertEquals(a, b.getParent());
    assertEquals(a, b.getParent());
    Map<String, TOTMHM_B> children = a.getChildren();
    assertNotNull(children);
    assertTrue(children.containsKey("b"));
    assertEquals(b, children.get("b"));
  }

  public void testPut() throws Exception {
    ChromatticSession session = login();

    TOTMHM_A a = session.insert(TOTMHM_A.class, "totmhm_a");
    TOTMHM_B b = session.create(TOTMHM_B.class);

    assertNull(a.getChildren().put("totmhm_b", b));

    //
    assertTrue(a.getChildren().containsKey("totmhm_b"));
    assertSame(b, a.getChildren().get("totmhm_b"));
    assertEquals(1, a.getChildren().size());
  }

  public void testPutWithInvalidName() throws Exception {
    ChromatticSession session = login();
    TOTMHM_A a = session.insert(TOTMHM_A.class, "totmhm_a");
    TOTMHM_B b = session.create(TOTMHM_B.class);
    try {
      a.getChildren().put("/foo", b);
      fail();
    }
    catch (NameSyntaxException e) {
    }
  }
}
