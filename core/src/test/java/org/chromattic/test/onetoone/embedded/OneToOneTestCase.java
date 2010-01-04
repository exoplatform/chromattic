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

package org.chromattic.test.onetoone.embedded;

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
    addClass(C.class);
  }

  public void testAddMixinToEntity() throws Exception {
    DomainSession session = login();
    B b = session.insert(B.class, "b");
    C c = session.create(C.class);
    assertNull(b.getMixin());
    assertNull(c.getEntity());
    b.setMixin(c);
    assertSame(c, b.getMixin());
    assertSame(b, c.getEntity());
    Node node = session.getNode(b);
    assertTrue(JCR.hasMixin(node, "otoe_c"));
    session.save();
    session.close();
    session = login();
    b = session.findByPath(B.class, "b");
    c = b.getMixin();
    assertNotNull(c);
  }

  public void testAddEntityToMixin() throws Exception {
    DomainSession session = login();
    B b = session.insert(B.class, "b");
    C c = session.create(C.class);
    assertNull(b.getMixin());
    assertNull(c.getEntity());
    c.setEntity(b);
    assertSame(c, b.getMixin());
    assertSame(b, c.getEntity());
    Node node = session.getNode(b);
    assertTrue(JCR.hasMixin(node, "otoe_c"));
    session.save();
    session.close();
    session = login();
    b = session.findByPath(B.class, "b");
    c = b.getMixin();
    assertNotNull(c);
  }

  public void testGetSuper() throws Exception {
    DomainSession session = login();
    B b = session.insert(B.class, "b");
    A a = b.getSuper();
    assertNotNull(a);
  }

  public void testSetSuper() throws Exception {
    DomainSession session = login();
    B b1 = session.insert(B.class, "b1");
    B b2 = session.insert(B.class, "b2");
    try {
      b2.setSuper(b1.getSuper());
      fail();
    }
    catch (IllegalArgumentException expected) {
    }
  }

  public void testMixinProperty() throws Exception {
    DomainSession session = login();
    B a = session.insert(B.class, "b");
    C b = session.create(C.class);
    a.setMixin(b);
    b.setFoo("bar");
    assertEquals("bar", b.getFoo());
  }

  public void testMixinChild() throws Exception {
    DomainSession session = login();
    B a1 = session.insert(B.class, "b");
    C b = session.create(C.class);
    a1.setMixin(b);
    B a2 = session.create(B.class);
    b.setB(a2);
    assertSame(b, a2.getParent());
  }
}