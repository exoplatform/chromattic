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

import org.chromattic.api.ChromatticSession;
import org.chromattic.common.JCR;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractOneToOneTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
    addClass(C.class);
  }

  protected abstract <E> void setEmbedded(ChromatticSession session, B b, Class<E> embeddedType, E e);

  protected abstract <E> E getEmbedded(ChromatticSession session, B b, Class<E> embeddedType);

  public void testAddMixinToEntity() throws Exception {
    ChromatticSessionImpl session = login();
    B b = session.insert(B.class, "b");
    C c = session.create(C.class);
    assertNull(b.getMixin());
    assertNull(c.getEntity());
    setEmbedded(session, b, C.class, c);
    assertSame(c, b.getMixin());
    assertSame(b, c.getEntity());
    Node node = session.getNode(b);
    assertTrue(JCR.hasMixin(node, getNodeTypeName(C.class)));
    session.save();
    session.close();
    session = login();
    b = session.findByPath(B.class, "b");
    c = getEmbedded(session, b, C.class);
    assertNotNull(c);
  }

  public void testAddEntityToMixin() throws Exception {
    ChromatticSessionImpl session = login();
    B b = session.insert(B.class, "b");
    C c = session.create(C.class);
    assertNull(getEmbedded(session, b, C.class));
    assertNull(c.getEntity());
    c.setEntity(b);
    assertSame(c, b.getMixin());
    assertSame(b, c.getEntity());
    Node node = session.getNode(b);
    assertTrue(JCR.hasMixin(node, getNodeTypeName(C.class)));
    session.save();
    session.close();
    session = login();
    b = session.findByPath(B.class, "b");
    c = getEmbedded(session, b, C.class);
    assertNotNull(c);
  }

  public void testGetSuper() throws Exception {
    ChromatticSessionImpl session = login();
    B b = session.insert(B.class, "b");
    A a = getEmbedded(session, b, A.class);
    assertNotNull(a);
  }

  public void testSetSuper() throws Exception {
    ChromatticSessionImpl session = login();
    B b1 = session.insert(B.class, "b1");
    B b2 = session.insert(B.class, "b2");
    A s = getEmbedded(session, b1, A.class);
    try {
      setEmbedded(session, b2, A.class, s);
      fail();
    }
    catch (IllegalArgumentException expected) {
    }
  }

  public void testMixinProperty() throws Exception {
    ChromatticSessionImpl session = login();
    B b = session.insert(B.class, "b");
    C c = session.create(C.class);
    setEmbedded(session, b, C.class, c);
    c.setFoo("bar");
    assertEquals("bar", c.getFoo());
  }

  public void testMixinChild() throws Exception {
    ChromatticSessionImpl session = login();
    B b1 = session.insert(B.class, "b");
    C c = session.create(C.class);
    setEmbedded(session, b1, C.class, c);
    B b2 = session.create(B.class);
    c.setB(b2);
    assertSame(c, b2.getParent());
  }
}