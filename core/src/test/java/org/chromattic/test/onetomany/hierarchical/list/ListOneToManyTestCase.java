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

package org.chromattic.test.onetomany.hierarchical.list;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.onetomany.hierarchical.map.TOTMHM_A;
import org.chromattic.test.onetomany.hierarchical.map.TOTMHM_B;
import org.chromattic.api.ChromatticSession;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ListOneToManyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
  }

  public void testAddSingleton() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b = session.create(B.class, "1");
    bs.add(b);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("1", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testAddLast() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    bs.add(b1);
    bs.add(b2);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("1", i.nextNode().getName());
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testAddFirst() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    bs.add(b1);
    bs.add(0, b2);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("2", i.nextNode().getName());
    assertEquals("1", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testAddMiddle() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    B b3 = session.create(B.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(1, b3);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("1", i.nextNode().getName());
    assertEquals("3", i.nextNode().getName());
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testIterator() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    B b3 = session.create(B.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(b3);

    //
    ListIterator<B> i = bs.listIterator();
    assertSame(b1, i.next());
    assertSame(b1, i.previous());
    assertSame(b1, i.next());
    assertSame(b2, i.next());
    assertSame(b2, i.previous());
    assertSame(b2, i.next());
    assertSame(b3, i.next());
    assertSame(b3, i.previous());
    assertSame(b3, i.next());
    assertSame(b3, i.previous());
    assertSame(b2, i.previous());
    assertSame(b1, i.previous());
  }

  public void testSet() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    bs.add(b1);
    bs.set(0, b2);

    //
    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testMoveFirst() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    B b3 = session.create(B.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(b3);

    // Move b3 to first position
    bs.add(0, bs.get(2));

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("3", i.nextNode().getName());
    assertEquals("1", i.nextNode().getName());
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testMoveLast() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "aaa");
    List<B> bs = a.getChildren();
    B b1 = session.create(B.class, "1");
    B b2 = session.create(B.class, "2");
    B b3 = session.create(B.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(b3);

    assertEquals(3, bs.size());

    // Move b3 to last position
    bs.add(3, bs.get(0));

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("2", i.nextNode().getName());
    assertEquals("3", i.nextNode().getName());
    assertEquals("1", i.nextNode().getName());
    assertFalse(i.hasNext());
  }
}