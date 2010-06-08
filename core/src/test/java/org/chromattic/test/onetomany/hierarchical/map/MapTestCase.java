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

import org.chromattic.api.ChromatticSession;
import org.chromattic.api.DuplicateNameException;
import org.chromattic.api.Status;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MapTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A3.class);
    addClass(B3.class);
  }

  public void testLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", getNodeTypeName(A3.class));
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", getNodeTypeName(B3.class));
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    A3 a = session.findById(A3.class, aId);
    assertNotNull(a);
    B3 b = session.findById(B3.class, bId);
    assertEquals(a, b.getParent());
    assertEquals(a, b.getParent());
    Map<String, B3> children = a.getChildren();
    assertNotNull(children);
    assertTrue(children.containsKey("b"));
    assertEquals(b, children.get("b"));
  }

  public void testPut() throws Exception {
    ChromatticSession session = login();

    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);

    assertNull(a.getChildren().put("totmhm_b", b));

    //
    assertTrue(a.getChildren().containsKey("totmhm_b"));
    assertSame(b, a.getChildren().get("totmhm_b"));
    assertEquals(1, a.getChildren().size());
  }

  public void testGetWithInvalidName() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    try {
      a.getChildren().get("/foo");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testGetWithNullName() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    assertNull(a.getChildren().get(null));
  }

  public void testPutWithInvalidName() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);
    try {
      a.getChildren().put("/foo", b);
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testPutWithNullName() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);
    try {
      a.getChildren().put(null, b);
      fail();
    }
    catch (NullPointerException e) {
    }
  }

  public void testRemoveTransient() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);
    Map<String, B3> children = a.getChildren();
    children.put("b", b);
    assertSame(b, children.remove("b"));
    assertEquals(Status.REMOVED, session.getStatus(b));
    assertTrue(children.isEmpty());
  }

  public void testRemovePersistent() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);
    a.getChildren().put("b", b);
    session.save();
    assertSame(b, a.getChildren().remove("b"));
    assertEquals(Status.REMOVED, session.getStatus(b));
    assertTrue(a.getChildren().isEmpty());
  }

  public void testDuplicatePutFails() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b1 = session.create(B3.class);
    B3 b2 = session.create(B3.class);
    a.getChildren().put("b", b1);
    try {
      a.getChildren().put("b", b2);
      fail();
    }
    catch (DuplicateNameException ignore) {
    }
    assertSame(b1, a.getChildren().get("b"));
  }

  public void testRename() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "totmhm_a");
    B3 b = session.create(B3.class);
    a.getChildren().put("b", b);
    assertEquals("b", b.getName());
    assertSame(a, b.getParent());
    a.getChildren().put("c", b);
    assertEquals("c", b.getName());
    assertSame(a, b.getParent());
  }
}
