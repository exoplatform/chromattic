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
package org.chromattic.test.format.transform;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.core.DomainSession;
import org.chromattic.test.format.A;
import org.chromattic.test.format.B;
import org.chromattic.test.format.DelegatingObjectFormatter;
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
    getBuilder().setOption(ChromatticBuilder.OBJECT_FORMATTER_CLASSNAME, FooPrefixerFormatter.class.getName());
    addClass(A.class);
    addClass(B.class);
  }

  public void testCreate() throws RepositoryException {
    DomainSession session = login();
    A a = session.create(A.class, "a");
    session.persist(a);
    Node aNode = session.getNode(a);
    assertEquals("foo_a", aNode.getName());
  }

  public void testSetNameAndPersist() throws RepositoryException {
    DomainSession session = login();
    A a = session.create(A.class);
    a.setName("a");
    session.persist(a);
    Node aNode = session.getNode(a);
    assertEquals("foo_a", aNode.getName());
  }

  public void testSetNameAndPersistAsChild() throws RepositoryException {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    B b = session.create(B.class);
    b.setName("b");
    session.persist(a, b);
    Node bNode = session.getNode(b);
    assertEquals("foo_b", bNode.getName());
  }

  public void testGetName() throws RepositoryException {
    DomainSession session = login();
    Node aNode = session.getRoot().addNode("foo_a", "format_a");
    A a = session.findByNode(A.class, aNode);
    assertEquals("a", a.getName());
  }

  public void testSessionGetName() throws RepositoryException {
    DomainSession session = login();
    Node aNode = session.getRoot().addNode("foo_a", "format_a");
    A a = session.findByNode(A.class, aNode);
    assertEquals("a", session.getName(a));
  }

  public void testOneToManyPut() throws RepositoryException {
    DomainSession session = login();
    Node aNode = session.getRoot().addNode("foo_a", "format_a");
    A a = session.findByNode(A.class, aNode);
    B b = session.create(B.class);
    a.getChildren().put("b", b);
    Node bNode = session.getNode(b);
    assertEquals("foo_b", bNode.getName());
  }

  public void testOneToManyGet() throws RepositoryException {
    DomainSession session = login();
    Node aNode = session.getRoot().addNode("foo_a", "format_a");
    A a = session.findByNode(A.class, aNode);
    Node bNode = aNode.addNode("foo_b", "format_b");
    B b = session.findByNode(B.class, bNode);
    assertSame(b, a.getChildren().get("b"));
  }

  public void testOneToManyKeySet() throws RepositoryException {
    DomainSession session = login();
    Node aNode = session.getRoot().addNode("foo_a", "format_a");
    A a = session.findByNode(A.class, aNode);
    Node bNode = aNode.addNode("foo_b", "format_b");
    session.findByNode(B.class, bNode);
    assertEquals(Collections.singleton("b"), a.getChildren().keySet());
  }
}
