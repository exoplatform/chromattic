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

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DeltaList<E> /*extends AbstractSequentialList<E>*/ {

  /** . */
  private final List<E> list;

  /** . */
  private final Delta<E> head;

  /** . */
  private final Delta<E> tail;

  public DeltaList(List<E> list) {

    Delta<E> head = new Delta<E>();
    head.listIndex = 0;
    head.listSize = list.size();

    //
    Delta<E> tail = new Delta<E>();
    tail.listIndex = list.size();
    tail.listSize = 0;

    //
    head.next = tail;
    tail.previous = head;

    //
    this.list = list;
    this.head = head;
    this.tail = tail;
  }

  public E get(int index) {
    Delta<E> current = head;
    while (true) {
      if (index < current.insertions.size()) {
        return current.insertions.get(index);
      } else {
        index -= current.insertions.size();
        Delta<E> next = current.next;
        if (next != null) {
          if (index < current.listSize) {
            index += current.listIndex;
            return list.get(index);
          } else {
            index -= current.listSize;
            current = next;
          }
        } else {
          throw new IndexOutOfBoundsException();
        }
      }
    }
  }

  public void add(int index, E e) {
    Delta<E> current = head;
    while (true) {
      if (index <= current.insertions.size()) {
        current.insertions.add(index, e);
        return;
      } else {
        index -= current.insertions.size();
        Delta<E> next = current.next;
        if (next != null) {
          if (index < current.listSize) {
            Delta<E> delta = new Delta<E>();
            delta.listIndex = current.listIndex + index;
            delta.listSize = current.listSize - index;
            current.listSize = index;
            current.next = delta;
            delta.previous = current;
            delta.next = next;
            next.previous = delta;
            delta.insertions.add(e);
            break;
          } else {
            index -= current.listSize;
            current = next;
          }
        } else {
          throw new IndexOutOfBoundsException();
        }
      }
    }
  }

  public E remove(int index) {
    Delta<E> current = head;
    while (true) {
      if (index < current.insertions.size()) {
        return current.insertions.remove(index);
      } else {
        index -= current.insertions.size();
        Delta<E> next = current.next;
        if (next != null) {
          if (index < current.listSize) {
            if (index == 0) {
              E removed = list.get(current.listIndex);
              current.listIndex++;
              current.listSize--;
              return removed;
            } else if (index == current.listSize - 1) {
              return list.get(current.listIndex + --current.listSize);
            } else {
              Delta<E> delta = new Delta<E>();
              delta.listIndex = current.listIndex + index + 1;
              delta.listSize = current.listSize - index - 1;
              current.listSize = index;
              current.next = delta;
              delta.previous = current;
              delta.next = next;
              next.previous = delta;
              return list.get(index);
            }
          } else {
            index -= current.listSize;
            current = next;
          }
        } else {
          throw new IndexOutOfBoundsException();
        }
      }
    }
  }

  public int size() {
    int size = 0;
    Delta<E> current = head;
    while (current != null) {
      size += current.insertions.size() + current.listSize;
      current = current.next;
    }
    return size;
  }
}
