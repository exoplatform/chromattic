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
package org.chromattic.test.format.transform.object;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.test.format.A;
import org.chromattic.test.format.B;
import org.chromattic.test.format.FooPrefixerFormatter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectNameTransformTestCase extends AbstractTestCase {

  protected void createDomain() {
    getBuilder().getConfiguration().setOptionValue(ChromatticBuilder.OBJECT_FORMATTER_CLASSNAME, FooPrefixerFormatter.class.getName());
    addClass(A.class);
    addClass(B.class);
    addClass(D1.class);
    addClass(D2.class);
  }

  public void testCreate() throws RepositoryException {
    ChromatticSessionImpl session = login();
    A a = session.create(A.class, "a");
    session.persist(a);
    Node aNode = session.getNode(a);
    assertEquals("foo_a", aNode.getName());
  }

  public void testSetNameAndPersist() throws RepositoryException {
    ChromatticSessionImpl session = login();
    A a = session.create(A.class);
    a.setName("a");
    session.persist(a);
    Node aNode = session.getNode(a);
    assertEquals("foo_a", aNode.getName());
  }

  public void testSetNameAndPersistAsChild() throws RepositoryException {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    B b = session.create(B.class);
    b.setName("b");
    session.persist(a, b);
    Node bNode = session.getNode(b);
    assertEquals("foo_b", bNode.getName());
  }

  public void testName() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    assertEquals("a", a.getName());
    a.setName("b");
    assertEquals("b", a.getName());
    assertEquals("foo_b", aNode.getName());
  }

  public void testSessionName() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    assertEquals("a", session.getName(a));
    session.setName(a, "b");
    assertEquals("b", session.getName(a));
    assertEquals("foo_b", aNode.getName());
  }

  public void testOneToManyPut() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    B b = session.create(B.class);
    a.getChildren().put("b", b);
    Node bNode = session.getNode(b);
    assertEquals("foo_b", bNode.getName());
  }

  public void testOneToManyMove() throws Exception {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    B b = session.create(B.class);
    a.getChildren().put("b", b);
    Node bNode = session.getNode(b);
    assertEquals("foo_b", bNode.getName());
    a.getChildren().put("c", b);
    assertEquals("foo_c", bNode.getName());
  }

  public void testOneToManyGet() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    Node bNode = aNode.addNode("foo_b", getNodeTypeName(B.class));
    B b = session.findByNode(B.class, bNode);
    assertSame(b, a.getChildren().get("b"));
  }

  public void testOneToManyKeySet() throws RepositoryException {
    ChromatticSessionImpl session = login();
    Node aNode = session.getRoot().addNode("foo_a", getNodeTypeName(A.class));
    A a = session.findByNode(A.class, aNode);
    Node bNode = aNode.addNode("foo_b", getNodeTypeName(B.class));
    session.findByNode(B.class, bNode);
    assertEquals(Collections.singleton("b"), a.getChildren().keySet());
  }

  public void testOneToOne1() throws RepositoryException {
    ChromatticSessionImpl session = login();
    D1 d1 = session.insert(D1.class, "d");
    D2 d2 = session.create(D2.class);
    d2.setParent(d1);
    assertSame(d1, d2.getParent());
  }

  public void testOneToOne2() throws RepositoryException {
    ChromatticSessionImpl session = login();
    D1 d1 = session.insert(D1.class, "d");
    D2 d2 = session.create(D2.class);
    d1.setChild(d2);
    assertSame(d2, d1.getChild());
  }
}
