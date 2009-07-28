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

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.util.ConcurrentModificationException;

import org.chromattic.common.FilterIterator;
import org.chromattic.common.AbstractFilterIterator;
import org.chromattic.common.IteratorFilter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class FilterIteratorTestCase extends TestCase {

  private static final List<Integer> integers = Arrays.asList(0, 1, 2, 3);

  private static class Bilto extends AbstractFilterIterator<Integer, Integer> {
    private Bilto(Iterator<Integer> integerIterator) throws NullPointerException {
      super(integerIterator);
    }
    protected Integer adapt(Integer internal) {
      return internal;
    }
  }

  public void testConcurrentModification1() {
    LinkedList<Integer> tmp = new LinkedList<Integer>(integers);
    Bilto iterator = new Bilto(tmp.iterator());

    //
    tmp.removeLast();
    try {
      iterator.hasNext();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
    try {
      iterator.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
    try {
      iterator.remove();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testConcurrentModification2() {
    LinkedList<Integer> tmp = new LinkedList<Integer>(integers);
    Bilto iterator = new Bilto(tmp.iterator());
    iterator.hasNext();

    //
    tmp.removeLast();
    iterator.next();
    try {
      iterator.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
    try {
      iterator.remove();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

  public void testFilterAll() {
    assertEquals(Collections.emptyList(), toList(integers, new IteratorFilter<Integer, Integer>() {
      public Integer adapt(Integer integer) {
        return null;
      }
    }));
  }

  public void testFilterSome() {
    assertEquals(Arrays.asList(0, 2), toList(integers, new IteratorFilter<Integer, Integer>() {
      public Integer adapt(Integer integer) {
        return integer % 2 == 0 ? integer : null;
      }
    }));
  }

  public void testFilterNone() {
    assertEquals(integers, toList(integers, new IteratorFilter<Integer, Integer>() {
      public Integer adapt(Integer integer) {
        return integer;
      }
    }));
  }

  public void testThrowNSEE() {
    IteratorFilter<Integer, Integer> filter = new IteratorFilter<Integer, Integer>() {
      public Integer adapt(Integer integer) {
        return null;
      }
    };
    Iterator<Integer> iterator = new FilterIterator<Integer, Integer>(integers.iterator(), filter);
    try {
      iterator.next();
      fail();
    }
    catch (NoSuchElementException expected) {
    }
  }

  private static <E> List<E> toList(Iterable<E> iterable, IteratorFilter<E, E> filter) {
    Iterator<E> iterator = new FilterIterator<E, E>(iterable.iterator(), filter);
    List<E> list = new ArrayList<E>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }

}
