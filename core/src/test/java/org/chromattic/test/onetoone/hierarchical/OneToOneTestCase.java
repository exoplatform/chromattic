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

package org.chromattic.test.onetoone.hierarchical;

import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;
import java.lang.IllegalStateException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A3.class, B3.class})
public class OneToOneTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A3.class);
    addClass(B3.class);
  }

  public void testChildAndParentAdd() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("toto_a_a", getNodeTypeName(A3.class));
    A3 a = session.findByNode(A3.class, aNode);
    assertNotNull(a);
    assertNull(a.getB());

    //
    Node bNode = aNode.addNode("b", getNodeTypeName(B3.class));
    B3 b = session.findByNode(B3.class, bNode);
    assertNotNull(b);
    assertSame(a, b.getA());
    assertSame(b, a.getB());
  }

  public void testChildAndParentLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("toto_a_b", getNodeTypeName(A3.class));
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", getNodeTypeName(B3.class));
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    B3 b = session.findById(B3.class, bId);
    assertNotNull(b);
    A3 a = b.getA();
    assertNotNull(a);
    assertSame(a, session.findById(A3.class, aId));
    assertSame(b, a.getB());
  }

  public void testSetRemoved() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "a");
    B3 b = session.insert(B3.class, "b");
    session.remove(b);
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
    try {
      a.setB(b);
      fail("Was expecting ISE");
    } catch (IllegalStateException expected) {
    }
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
    try {
      b.setA(a);
      fail("Was expecting ISE");
    } catch (IllegalStateException expected) {
    }
    assertEquals(Status.PERSISTENT, session.getStatus(a));
    assertEquals(Status.REMOVED, session.getStatus(b));
  }
}
