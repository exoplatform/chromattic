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

package org.chromattic.test.nodeattribute;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeAttributeTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TNA_A.class);
  }

  public void testInvalidName() throws Exception {
    ChromatticSessionImpl session = login();
    TNA_A a = session.create(TNA_A.class);
    a.setName(".");
    try {
      session.persist(a);
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testPersistent() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("tna_a_a", "tna_a");
    String aName = aNode.getName();
    String aId = aNode.getUUID();
    String aPath = aNode.getPath();
    String workspaceName = session.getJCRSession().getWorkspace().getName();

    //
    TNA_A a = session.findById(TNA_A.class, aId);
    assertEquals(aName, a.getName());
    assertEquals(aId, a.getId());
    assertEquals(aPath, a.getPath());
    assertEquals(workspaceName, a.getWorkspace());

    //
    a.setName("foo");
    try {
      a.setId("foo");
      fail();
    }
    catch (UnsupportedOperationException e) { }
    try {
      a.setPath("foo");
      fail();
    }
    catch (UnsupportedOperationException e) { }
    try {
      a.setWorkspace("foo");
      fail();
    }
    catch (UnsupportedOperationException e) { }

    //
    String newPath = aPath.substring(0, aPath.lastIndexOf('/')) + "/foo";

    // Check state has not changed
    assertEquals("foo", a.getName());
    assertEquals(aId, a.getId());
    assertEquals(newPath, a.getPath());
    assertEquals(workspaceName, a.getWorkspace());

    //
    session.save();

    //
    session = login();
    a = session.findById(TNA_A.class, aId);
    assertEquals("foo", a.getName());
    assertEquals(aId, a.getId());
    assertEquals(newPath, a.getPath());
    assertEquals(workspaceName, a.getWorkspace());
  }

  public void testTransient() throws Exception {
    ChromatticSession session = login();
    TNA_A a = session.create(TNA_A.class);
    a.setName("tna_a_b");
    assertEquals("tna_a_b", a.getName());
    try {
      a.setId("foo");
      fail();
    }
    catch (UnsupportedOperationException e) { }
    try {
      a.setPath("foo");
      fail();
    }
    catch (UnsupportedOperationException e) { }
  }

  public void testRemoved() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("tna_a_a", "tna_a");
    String aId = aNode.getUUID();
    session.save();

    //
    session = login();
    TNA_A a = session.findById(TNA_A.class, aId);
    session.remove(a);

    //
    try {
      a.getName();
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.getId();
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.getPath();
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.getWorkspace();
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.setName("foo");
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.setId("foo");
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.setPath("foo");
      fail();
    }
    catch (IllegalStateException e) { }
    try {
      a.setWorkspace("foo");
      fail();
    }
    catch (IllegalStateException e) { }
  }
}