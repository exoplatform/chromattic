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

package org.chromattic.test.onetoone.mixin;

import org.chromattic.common.JCR;
import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToOneTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
  }

  public void testAddMixinToEntity() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    B b = session.create(B.class);
    assertNull(a.getMixin());
    assertNull(b.getEntity());
    a.setMixin(b);
    assertSame(b, a.getMixin());
    assertSame(a, b.getEntity());
    Node node = session.getNode(a);
    assertTrue(JCR.hasMixin(node, "otom_b"));
    session.save();
    session.close();
    session = login();
    a = session.findByPath(A.class, "a");
    b = a.getMixin();
    assertNotNull(b);
  }

  public void testAddEntityToMixin() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    B b = session.create(B.class);
    assertNull(a.getMixin());
    assertNull(b.getEntity());
    b.setEntity(a);
    assertSame(b, a.getMixin());
    assertSame(a, b.getEntity());
    Node node = session.getNode(a);
    assertTrue(JCR.hasMixin(node, "otom_b"));
    session.save();
    session.close();
    session = login();
    a = session.findByPath(A.class, "a");
    b = a.getMixin();
    assertNotNull(b);
  }

  public void testMixinProperty() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    B b = session.create(B.class);
    a.setMixin(b);
    b.setFoo("bar");
    assertEquals("bar", b.getFoo());
  }

  public void testMixinChild() throws Exception {
    DomainSession session = login();
    A a1 = session.insert(A.class, "a");
    B b = session.create(B.class);
    a1.setMixin(b);
    A a2 = session.create(A.class);
    b.setA(a2);
    assertSame(b, a2.getParent());
  }

/*
  public void testChildAndParentAdd() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("toto_a_a", "toto_a");
    TOTO_A_3 a = session.findByNode(TOTO_A_3.class, aNode);
    assertNotNull(a);
    assertNull(a.getB());

    //
    Node bNode = aNode.addNode("b", "toto_b");
    TOTO_B_3 b = session.findByNode(TOTO_B_3.class, bNode);
    assertNotNull(b);
    assertSame(a, b.getA());
    assertSame(b, a.getB());
  }

  public void testChildAndParentLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("toto_a_b", "toto_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "toto_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    TOTO_B_3 b = session.findById(TOTO_B_3.class, bId);
    assertNotNull(b);
    TOTO_A_3 a = b.getA();
    assertNotNull(a);
    assertSame(a, session.findById(TOTO_A_3.class, aId));
    assertSame(b, a.getB());
  }
*/
}