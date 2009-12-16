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

import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class Segment<E> {

  abstract Segment<E> getNext();

  abstract void setNext(Segment<E> next);

  abstract Segment<E> getPrevious();

  abstract void setPrevious(Segment<E> previous);

  protected abstract E localGet(int index);

  protected abstract int localSize();

  protected abstract boolean localCanAdd(int index);

  protected abstract void localAdd(int index, E element);

  protected abstract E localRemove(int index);

  protected abstract Iterator<E> localIterator();

  public final E get(int index) {
    Segment<E> segment = this;
    while (true) {
      if (index < 0) {
        Segment<E> previous = segment.getPrevious();
        if (previous == null) {
          throw new IndexOutOfBoundsException();
        } else {
          index += previous.localSize();
          segment = previous;
        }
      } else {
        int localSize = segment.localSize();
        if (index >= localSize) {
          Segment<E> next = segment.getNext();
          if (next == null) {
            throw new IndexOutOfBoundsException();
          } else {
            index -= localSize;
            segment = next;
          }
        } else {
          break;
        }
      }
    }
    return segment.localGet(index);
  }

  public final void add(int index, E e) {
    Segment<E> segment = this;
    while (true) {
      if (index < 0) {
        throw new IndexOutOfBoundsException();
      } else {
        if (segment.localCanAdd(index)) {
          segment.localAdd(index, e);
          break;
        } else {
          Segment<E> next = segment.getNext();
          if (next == null) {
            throw new IndexOutOfBoundsException();
          } else {
            index -= segment.localSize();
            segment = next;
          }
        }
      }
    }
  }

  public final E remove(int index) {
    Segment<E> segment = this;
    while (true) {
      if (index < 0) {
        throw new IndexOutOfBoundsException();
      } else {
        int localSize = segment.localSize();
        if (index < localSize) {
          return segment.localRemove(index);
        } else {
          Segment<E> next = segment.getNext();
          if (next == null) {
            throw new IndexOutOfBoundsException();
          } else {
            index -= localSize;
            segment = next;
          }
        }
      }
    }
  }

  public final int size() {
    int size = 0;
    for (Segment<E> segment = this;segment != null;segment = segment.getNext()) {
      size += segment.localSize();
    }
    return size;
  }

  public final Iterator<E> iterator() {
    return new IteratorImpl<E>(this);
  }

  public final int complexity() {
    int complexity = 0;
    for (Segment<E> segment = this;segment != null;segment = segment.getNext()) {
      complexity ++;
    }
    return complexity;
  }

  // *********************

  final boolean hasNext() {
    return getNext() != null;
  }

  final Segment<E> addAfter(Segment<E> segment) {
    Segment<E> next = getNext();
    if (next != null) {
      segment.setNext(next);
      next.setPrevious(segment);
    }

    //
    segment.setPrevious(this);
    setNext(segment);

    //
    return segment;
  }

  // *********************

  protected abstract void format(StringBuilder builder);

  public String format() {
    StringBuilder builder = new StringBuilder("[");
    int count = 0;
    for (Segment<E> segment = this;segment != null;segment = segment.getNext()) {
      if (count > 0) {
        builder.append(",");
      }
      segment.format(builder);
      count ++;
    }
    builder.append("]");
    return builder.toString();
  }
}
