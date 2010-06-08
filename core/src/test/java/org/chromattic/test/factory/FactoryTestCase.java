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

package org.chromattic.test.factory;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.RepositoryException;
import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class FactoryTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TF_A.class);
    addClass(TF_B.class);
  }

  public void testCreate() throws RepositoryException {
    ChromatticSessionImpl session = login();

    //
    Node aNode = session.getRoot().addNode("a", "factory:a");
    TF_A a = session.findByNode(TF_A.class, aNode);
    TF_B.constructed = 0;
    TF_B b = a.create();
    assertEquals(1, TF_B.constructed);
    assertNotNull(b);
    assertEquals(null, session.getName(b));
  }

  public void testCreateWithName() throws RepositoryException {
    ChromatticSessionImpl session = login();

    //
    Node aNode = session.getRoot().addNode("a", "factory:a");
    TF_A a = session.findByNode(TF_A.class, aNode);
    TF_B.constructed = 0;
    TF_B b = a.create("b");
    assertEquals(1, TF_B.constructed);
    assertNotNull(b);
    assertEquals("b", session.getName(b));
  }

  public void testLifeCycle() throws RepositoryException {
    ChromatticSessionImpl session = login();

    //
    TF_B.constructed = 0;
    TF_B b = session.create(TF_B.class);
    assertNotNull(b);
    assertFalse(session.getJCRSession().hasPendingChanges());
    assertEquals(1, TF_B.constructed);

/*

    //
    TF_A a = factory.create(TF_A.class, "tf_a");
    assertNotNull(a);
    assertFalse(session.getJCRSession().hasPendingChanges());
    assertEquals(1, TF_A.constructed);
    assertEquals(0, TF_A.called);

    try {
      a.m();
      fail();
    }
    catch (IllegalStateException ignore) {
    }

    //
    Node rootNode = session.getRoot();
    Node aNode = session.insert(rootNode, a);
    assertEquals(3, TF_A.constructed);
    assertEquals(0, TF_A.called);
    assertNotNull(aNode);
    assertEquals("tf_a", aNode.getPrimaryNodeType().getName());
    assertEquals(rootNode, aNode.getParent());

    //
    a.m();
    assertEquals(3, TF_A.constructed);
    assertEquals(1, TF_A.called);
*/
  }

}
