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
package org.chromattic.common.collection.delta;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class DeltaList<E, L> implements List<E> {

  public static <E> List<E> create(List<E> list) {
    ListAdapter<E, List<E>> adapter = new ListAdapter<E, List<E>>() {
      public E get(List<E> list, int index) {
        return list.get(index);
      }
      public int size(List<E> list) {
        return list.size();
      }
      public void remove(List<E> list, int from, int to) {
        list.subList(from, to).clear();
      }
      public void insert(List<E> list, int index, List<E> elements) {
        list.addAll(index, elements);
      }
    };
    return new DeltaList<E, List<E>>(adapter, list);
  }

  /** . */
  private Segment<E> head;

  /** . */
  private Segment<E> tail;

  /** . */
  private final L list;

  /** . */
  private final ListAdapter<E, L> adapter;

  private DeltaList(ListAdapter<E, L> adapter, L list) {
    InPlaceSegment<E> segment = new InPlaceSegment<E>(this);
    segment.listIndex = 0;
    segment.listSize = adapter.size(list);

    //
    HeadSegment<E> head = new HeadSegment<E>();

    //
    TailSegment<E> tail = new TailSegment<E>();

    //
    head.addAfter(segment).addAfter(tail);

    //
    this.list = list;
    this.adapter = adapter;
    this.head = head;
    this.tail = tail;
  }

  E listget(int index) {
    return adapter.get(list, index);
  }

  public void save() {

    Segment<E> segment = head;
    int index = 0;

    //
    while (segment != null) {
      if (segment instanceof InPlaceSegment) {
        InPlaceSegment<E> inPlaceSegment = (InPlaceSegment<E>)segment;
        if (index < inPlaceSegment.listIndex) {
          // Need to remove elements
          adapter.remove(list, index, inPlaceSegment.listIndex);
        }
        index += inPlaceSegment.listSize;
      } else {
        AbstractInsertionSegment<E> insertionSegment = (AbstractInsertionSegment<E>)segment;
        int inSize = insertionSegment.insertions.size();
        if (inSize > 0) {
          adapter.insert(list, index, insertionSegment.insertions);
          index += inSize;
        }
      }
      segment = segment.getNext();
    }

    // Need to remove any trailing data
    int size = adapter.size(list);
    if (index < size) {
      adapter.remove(list, index, size); 
    }
  }

  public int complexity() {
    return head.complexity();
  }


  public E get(int index) {
    return head.get(index);
  }

  public void add(int index, E e) {
    head.add(index, e);
  }

  public E remove(int index) {
    return head.remove(index);
  }

  public int size() {
    return head.size();
  }

  public Iterator<E> iterator() {
    return head.iterator();
  }

  public String toString() {
    return head.format();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public boolean contains(Object o) {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  public <T> T[] toArray(T[] a) {
    throw new UnsupportedOperationException();
  }

  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public E set(int index, E element) {
    throw new UnsupportedOperationException();
  }

  public int indexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  public ListIterator<E> listIterator() {
    throw new UnsupportedOperationException();
  }

  public ListIterator<E> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }
}
