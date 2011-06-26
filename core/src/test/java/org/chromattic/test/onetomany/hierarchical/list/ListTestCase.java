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

import org.chromattic.api.ChromatticSession;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A3.class, B3.class})
public class ListTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A3.class);
    addClass(B3.class);
  }

  public void testWeird() throws Exception {
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    bs.add(session.create(B3.class, "1"));
    bs.add(session.create(B3.class, "2"));
    bs.add(session.create(B3.class, "3"));
    Iterator<B3> i = a.getChildren().iterator();
    assertEquals("1", session.getName(i.next()));
    assertEquals("2", session.getName(i.next()));
    assertEquals("3", session.getName(i.next()));
    assertFalse(i.hasNext());
    session.save();
    session.close();

    session = login();
    a = session.findByPath(A3.class, "aaa");
    bs = a.getChildren();
    bs.remove(0);
    bs.remove(0);
    i = a.getChildren().iterator();
    assertEquals("3", session.getName(i.next()));
    assertFalse(i.hasNext());
    session.save();
    session.close();

    session = login();
    a = session.findByPath(A3.class, "aaa");
    bs = a.getChildren();
    B3 foo = session.create(B3.class, "4");
    bs.add(foo);

    i = a.getChildren().iterator();
    assertEquals("3", session.getName(i.next()));
    assertEquals("4", session.getName(i.next()));
    assertFalse(i.hasNext());

    session.save();
    session.close();
  }

  public void testAddSingleton() throws Exception {
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b = session.create(B3.class, "1");
    bs.add(b);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("1", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testAddLast() throws Exception {
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
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
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
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
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    B3 b3 = session.create(B3.class, "3");
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
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    B3 b3 = session.create(B3.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(b3);

    //
    ListIterator<B3> i = bs.listIterator();
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
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    bs.add(b1);
    bs.set(0, b2);

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testSetWithExisting() throws Exception {
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    B3 b3 = session.create(B3.class, "3");
    bs.add(b1);
    bs.add(b2);
    bs.add(b3);

    //
    bs.set(0, bs.get(2));

    //
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("3", i.nextNode().getName());
    assertEquals("2", i.nextNode().getName());
    assertFalse(i.hasNext());
  }

  public void testSetToTransient() throws Exception {
    ChromatticSession session = login();
    A3 a = session.create(A3.class, "a");
    List<B3> bs = a.getChildren();
    B3 b = session.create(B3.class, "1");
    try {
      bs.set(0, b);
      fail();
    } catch (IllegalStateException ignore) {
    }
  }

  public void testSetToRemoved() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "a");
    List<B3> bs = a.getChildren();
    session.remove(a);
    B3 b = session.create(B3.class, "1");
    try {
      bs.set(0, b);
      fail();
    } catch (IllegalStateException ignore) {
    }
  }

  public void testSetRemoved() throws Exception {
    ChromatticSession session = login();
    A3 a = session.insert(A3.class, "a");
    List<B3> bs = a.getChildren();
    B3 b1 = session.insert(B3.class, "1");
    bs.add(b1);
    B3 b2 = session.insert(B3.class, "2");
    session.remove(b2);
    try {
      bs.set(0, b2);
      fail();
    } catch (IllegalArgumentException ignore) {
    }
  }

  public void _testMoveFirst() throws Exception {
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    B3 b3 = session.create(B3.class, "3");
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
    ChromatticSessionImpl session = login();
    A3 a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    B3 b1 = session.create(B3.class, "1");
    B3 b2 = session.create(B3.class, "2");
    B3 b3 = session.create(B3.class, "3");
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

/*
  public void testSort() throws Exception {
    DomainSession session = login();
    A a = session.insert(A3.class, "aaa");
    List<B3> bs = a.getChildren();
    bs.add(session.create(B3.class, "3"));
    bs.add(session.create(B3.class, "2"));
    bs.add(session.create(B3.class, "1"));

    Comparator<B3> comparator = new Comparator<B3>() {
      public int compare(B o1, B o2) {
        return o1.getName().compareTo(o2.getName());
      }
    };
//    Collections.sort(bs, comparator);
    Object[] aa = bs.toArray();
    Arrays.sort(aa, (Comparator)comparator);
    ListIterator i = bs.listIterator();
    for (int j=0; j<aa.length; j++) {
        i.next();
        i.set(aa[j]);
    }

    //
*/
/*
    Node aNode = session.getNode(a);
    NodeIterator i = aNode.getNodes();
    assertEquals("1", i.nextNode().getName());
    assertEquals("2", i.nextNode().getName());
    assertEquals("3", i.nextNode().getName());
    assertFalse(i.hasNext());
*/
/*
  }
*/
}
