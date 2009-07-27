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
package org.chromattic.test.lifecycle;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InsertTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TLF_A.class);
  }

  public void testWithRelativePathFromRoot() throws Exception {
    ChromatticSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    TLF_A b = session.insert(TLF_A.class, "tlf_a/b");
    assertNotNull(b);
    assertEquals("b", session.getName(b));
    assertEquals(Status.PERSISTENT, session.getStatus(b));

    //
    Node aNode = session.getJCRSession().getRootNode().getNode("tlf_a");
    Node bNode = aNode.getNode("b");

    //
    assertEquals(session.getId(a), aNode.getUUID());
    assertEquals(session.getId(b), bNode.getUUID());
  }

  public void testWithRelativePathParent() throws Exception {
    ChromatticSession session = login();

    //
    TLF_A a = session.insert(TLF_A.class, "tlf_a");
    assertNotNull(a);
    assertEquals("tlf_a", session.getName(a));
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    TLF_A b = session.insert(a, TLF_A.class, "b");
    assertNotNull(b);
    assertEquals("b", session.getName(b));
    assertEquals(Status.PERSISTENT, session.getStatus(b));
    TLF_A c = session.insert(b, TLF_A.class, "../c");
    assertNotNull(c);
    assertEquals("c", session.getName(c));
    assertEquals(Status.PERSISTENT, session.getStatus(c));

    //
    Node aNode = session.getJCRSession().getRootNode().getNode("tlf_a");
    Node bNode = aNode.getNode("b");
    Node cNode = aNode.getNode("c");

    //
    assertEquals(session.getId(a), aNode.getUUID());
    assertEquals(session.getId(b), bNode.getUUID());
    assertEquals(session.getId(c), cNode.getUUID());
  }
}
