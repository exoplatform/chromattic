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
import java.util.LinkedList;

import org.chromattic.common.BufferingListIterator;
import org.chromattic.common.ListModel;
import org.chromattic.common.ElementInsertion;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BufferingListIteratorTestCase extends TestCase {

  private static class State implements ListModel<Integer> {

    public static class Event {

      public static class Added extends Event {

        /** . */
        private final ElementInsertion insertion;

        public Added(ElementInsertion insertion) {
          this.insertion = insertion;
        }

        @Override
        public boolean equals(Object obj) {
          if (obj instanceof Added) {
            Added that = (Added)obj;
            return (insertion == null ? that.insertion == null : insertion.equals(that.insertion));
          }
          return false;
        }

        @Override
        public String toString() {
          return "Added[insertion=" + insertion + "]";
        }
      }

      public static class Removed extends Event {

        /** . */
        private final int position;

        /** . */
        private final int element;

        public Removed(int position, int element) {
          this.position = position;
          this.element = element;
        }

        @Override
        public boolean equals(Object obj) {
          if (obj instanceof Removed) {
            Removed that = (Removed)obj;
            return position == that.position && element == that.element;
          }
          return false;
        }

        @Override
        public String toString() {
          return "Removed[element=" + element + ",position=" + position + "]";
        }
      }

      public static class Replaced extends Event {

        /** . */
        private final int position;

        /** . */
        private final int oldElement;

        /** . */
        private final int newElement;

        public Replaced(int position, int oldElement, int newElement) {
          this.position = position;
          this.oldElement = oldElement;
          this.newElement = newElement;
        }

        @Override
        public boolean equals(Object obj) {
          if (obj instanceof Replaced) {
            Replaced that = (Replaced)obj;
            return position == that.position &&
              oldElement == that.oldElement &&
              newElement == that.newElement;
          }
          return false;
        }

        @Override
        public String toString() {
          return "Replaced[oldElement=" + oldElement + ",newElement=" + newElement + ",position=" + position + "]";
        }
      }
    }

    /** . */
    private BufferingListIterator<Integer> i;

    /** . */
    private int expectedPreviousIndex;

    /** . */
    private int expectedNextIndex;

    /** . */
    private List<Integer> list;

    /** . */
    private final LinkedList<Event> events;

    /** . */
    private Boolean forward;

    public State(Integer... values) {
      list = new ArrayList<Integer>(Arrays.asList(values));
      i = new BufferingListIterator<Integer>(this);
      expectedPreviousIndex = -1;
      expectedNextIndex = 0;
      events = new LinkedList<Event>();
      this.forward = null;
    }

    public void assertInvariants() {
      assertEquals(expectedPreviousIndex, i.previousIndex());
      assertEquals(expectedNextIndex, i.nextIndex());
      assertEquals(expectedPreviousIndex > -1, i.hasPrevious());
      assertEquals(expectedNextIndex < list.size(), i.hasNext());
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
      assertEquals(expectedNextIndex < list.size(), i.hasNext());
    }

    public void assertNext() {
      assertInvariants();
      try {
        int next = i.next();
        expectedPreviousIndex++;
        expectedNextIndex++;
        forward = true;
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
        forward = false;
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
        if (forward) {
          expectedNextIndex--;
          expectedPreviousIndex--;
        }
      } finally {
        assertInvariants();
      }
    }

    public void assertNoEvent() {
      assertTrue(events.size() == 0);
    }

    public void assertEvent(Event expectedEvent) {
      assertTrue(events.size() > 0);
      Event event = events.removeFirst();
      assertEquals("Event " + event + " is not equals to expected event " + expectedEvent, expectedEvent, event);
    }

    public void assertAddedEvent(ElementInsertion position) {
      assertEvent(new Event.Added(position));
    }

    public void assertRemovedEvent(int position, int removedElement) {
      assertEvent(new Event.Removed(position, removedElement));
    }

    public void assertReplacedEvent(int position, int removedElement, int addedElement) {
      assertEvent(new Event.Replaced(position, removedElement, addedElement));
    }

    public Iterator<Integer> iterator() {
      return list.iterator();
    }

    public void add(ElementInsertion<Integer> insertion) {
      if (insertion != null) {
        if (insertion instanceof ElementInsertion.First) {
          ElementInsertion.First<Integer> first = (ElementInsertion.First<Integer>)insertion;
          assertEquals(0, (int)first.getNext());
          assertEquals(list.get(0), first.getNext());
        } else if (insertion instanceof ElementInsertion.Middle) {
          ElementInsertion.Middle<Integer> middle = (ElementInsertion.Middle<Integer>)insertion;
          int index = middle.getIndex();
          assertEquals(list.get(index - 1), middle.getPrevious());
          assertEquals(list.get(index), middle.getNext());
        } else if (insertion instanceof ElementInsertion.Last) {
          ElementInsertion.Last<Integer> last = (ElementInsertion.Last<Integer>)insertion;
          assertEquals(list.size() - 1  , last.getIndex() - 1);
          assertEquals(list.get(last.getIndex() - 1), last.getPrevious());
        } else {
          ElementInsertion.Singleton<Integer> last = (ElementInsertion.Singleton<Integer>)insertion;
          assertTrue(list.isEmpty());
        }
      }
      events.add(new Event.Added(insertion));
      list.add(insertion.getIndex(), insertion.getElement());
    }

    public void set(int index, Integer removedElement, Integer addedElement) {
      assertEquals(list.get(index), removedElement);
      events.add(new Event.Replaced(index, removedElement, addedElement));
      list.set(index, addedElement);
    }

    public void remove(int position, Integer removedElement) {
      assertEquals(list.get(position), removedElement);
      events.add(new Event.Removed(position, removedElement));
      list.remove(position);
    }

    public int size() {
      return list.size();
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
    state.assertRemovedEvent(0, 0);
    state.assertNoEvent();
  }

  public void testRemoveFirst2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertPrevious();
    state.assertRemove();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertRemovedEvent(0, 0);
    state.assertNoEvent();
  }

  public void testRemoveSecond1() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertRemove();
    state.assertNext();
    state.assertState(true, false);
    state.assertRemovedEvent(1, 1);
    state.assertNoEvent();
  }

  public void testRemoveSecond2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertRemove();
    state.assertPrevious();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertRemovedEvent(1, 1);
    state.assertNoEvent();
  }

  public void testRemoveSecond3() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertPrevious();
    state.assertRemove();
    state.assertPrevious();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertRemovedEvent(1, 1);
    state.assertNoEvent();
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
    state.assertRemovedEvent(2, 2);
    state.assertNoEvent();
  }

  public void testRemoveLast2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertPrevious();
    state.assertRemove();
    state.assertState(true, false);
    state.assertRemovedEvent(2, 2);
    state.assertNoEvent();
  }

  public void testAddEmpty() {
    State state = new State();
    state.assertAdd(-1);
    state.assertState(false, true);
    state.assertNext();
    state.assertState(true, false);
    state.assertAddedEvent(new ElementInsertion.Singleton<Integer>(-1));
    state.assertNoEvent();
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
    state.assertAddedEvent(new ElementInsertion.First<Integer>(-1, 0));
    state.assertNoEvent();
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
    state.assertAddedEvent(new ElementInsertion.First<Integer>(-1, 0));
    state.assertNoEvent();
  }

  public void testAddFirst3() {
    State state = new State(0);
    state.assertNext();
    state.assertPrevious();
    state.assertAdd(-1);
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertAddedEvent(new ElementInsertion.First<Integer>(-1, 0));
    state.assertNoEvent();
  }

  public void testAddMiddle() {
    State state = new State(0, 2, 3);
    state.assertNext();
    state.assertAdd(1);
    state.assertPrevious();
    state.assertState(false, true);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertAddedEvent(new ElementInsertion.Middle<Integer>(1, 0, 1, 2));
    state.assertNoEvent();
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
    state.assertAddedEvent(new ElementInsertion.Last<Integer>(2, 2, 3));
    state.assertNoEvent();
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
    state.assertReplacedEvent(0, 0, -1);
    state.assertNoEvent();
  }

  public void testSetFirst2() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertPrevious();
    state.assertSet(-1);
    state.assertNext();
    state.assertNext();
    state.assertNext();
    state.assertState(true, false);
    state.assertPrevious();
    state.assertPrevious();
    state.assertPrevious();
    state.assertState(false, true);
    state.assertReplacedEvent(0, 0, -1);
    state.assertNoEvent();
  }

  public void testRemoveThrowsISEAfterAdd() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertAdd(10);
    try {
      state.assertRemove();
      fail();
    }
    catch (IllegalStateException e) {
    }
  }

  public void testRemoveThrowsISEIfNoCallToNext() {
    State state = new State(0, 1, 2);
    try {
      state.assertRemove();
      fail();
    }
    catch (IllegalStateException e) {
    }
  }

  public void testSetThrowsISEAfterAdd() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertAdd(10);
    try {
      state.assertSet(2);
      fail();
    }
    catch (IllegalStateException e) {
    }
  }

  public void testSetThrowsISEAfterRemove() {
    State state = new State(0, 1, 2);
    state.assertNext();
    state.assertRemove();
    try {
      state.assertSet(2);
      fail();
    }
    catch (IllegalStateException e) {
    }
  }
}
