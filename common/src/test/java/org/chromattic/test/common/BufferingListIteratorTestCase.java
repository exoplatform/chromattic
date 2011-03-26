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
package org.chromattic.test.common;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.chromattic.common.AbstractBufferingListIterator;
import org.chromattic.common.ListModel;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BufferingListIteratorTestCase extends TestCase {

  private static class State implements ListModel<Integer> {

    /** . */
    private AbstractBufferingListIterator<Integer> i;

    /** . */
    private int expectedPreviousIndex = -1;

    /** . */
    private int expectedNextIndex = 1;

    /** . */
    private List<Integer> list;

    public State(Integer... values) {
      list = new ArrayList<Integer>(Arrays.asList(values));
      i = new AbstractBufferingListIterator<Integer>(this);
      expectedPreviousIndex = -1;
      expectedNextIndex = 1;
    }

    public void assertInvariants() {
      assertEquals(expectedPreviousIndex, i.previousIndex());
      assertEquals(expectedNextIndex, i.nextIndex());
      assertEquals(expectedPreviousIndex > -1, i.hasPrevious());
      assertEquals(expectedNextIndex <= list.size(), i.hasNext());
      if (!i.hasPrevious()) {
        try {
          i.previous();
          fail();
        }
        catch (NoSuchElementException ignore) {
        }
      }
      if (!i.hasNext()) {
        try {
          i.next();
          fail();
        }
        catch (NoSuchElementException ignore) {
        }
      }
      assertEquals(expectedPreviousIndex, i.previousIndex());
      assertEquals(expectedNextIndex, i.nextIndex());
      assertEquals(expectedPreviousIndex > -1, i.hasPrevious());
      assertEquals(expectedNextIndex <= list.size(), i.hasNext());
    }

    public void assertNext() {
      assertInvariants();
      try {
        int next = i.next();
        expectedPreviousIndex++;
        expectedNextIndex++;
        int expectedNext = list.get(expectedPreviousIndex);
        assertEquals(expectedNext, next);
      }
      finally {
        assertInvariants();
      }
    }

    public void assertPrevious() {
      assertInvariants();
      try {
        int previous = i.previous();
        int expectedPrevious = list.get(expectedPreviousIndex);
        expectedPreviousIndex--;
        expectedNextIndex--;
        assertEquals(expectedPrevious, previous);
      }
      finally {
        assertInvariants();
      }
    }

    public void assertState(boolean hasPrevious, boolean hasNext) {
      assertInvariants();
      assertEquals(hasPrevious, i.hasPrevious());
      assertEquals(hasNext, i.hasNext());
    }

    public void assertAdd(int value) {
      assertInvariants();
      try {
        i.add(value);
      }
      finally {
        assertInvariants();
      }
    }

    public void assertSet(int value) {
      assertInvariants();
      try {
        i.set(value);
      }
      finally {
        assertInvariants();
      }
    }

    public void assertRemove() {
      assertInvariants();
      try {
        i.remove();
        expectedPreviousIndex--;
        expectedNextIndex--;
      } finally {
        assertInvariants();
      }
    }

    public Iterator<Integer> iterator() {
      return list.iterator();
    }

    public void set(int index, Integer integer, Integer newElement) {
      list.set(index, newElement);
    }

    public void add(int index, Integer integer) {
      list.add(index, integer);
    }

    public void remove(int index, Integer integer) {
      list.remove(index);
    }
  }

  public void testBrowse() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertPrevious();
    state.assertNext();
    state.assertNext();
  }

  public void testNoSuchPreviousElement() {
    State state = new State(0, 1, 2);
    try {
      state.assertPrevious();
      fail();
    }
    catch (NoSuchElementException ignore) {
    }
  }

  public void testNoSuchNextElement() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    try {
      state.assertNext();
      fail();
    }
    catch (NoSuchElementException ignore) {
    }
  }

  public void testRemoveFirst1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertRemove();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testRemoveFirst2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertRemove();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testRemoveFirst3() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertPrevious();
    state.assertRemove();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testRemoveSecond1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertRemove();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testRemoveSecond2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertRemove();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testRemoveLast1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertRemove();
    state.assertState(true, false);
    state.assertPrevious();
    state.assertPrevious();
    state.assertState(false, true);
  }

  public void testAddFirst1() {
    State state = new State(0, 1, 2);
    state.assertAdd(-1);
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testAddFirst2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertPrevious();
    state.assertAdd(-1);
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
  }

  public void testAddLast1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertAdd(3);
    state.assertNext();
    state.assertState(true, false);
    state.assertPrevious();
    state.assertPrevious();
    state.assertPrevious();
    state.assertPrevious();
    state.assertState(false, true);
  }

  public void testSetFirst1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertSet(-1);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertPrevious();
    state.assertPrevious();
    state.assertPrevious();
    state.assertState(false, true);
  }
}
