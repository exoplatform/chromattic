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
public abstract class Segment<E> {

  /** . */
  Segment<E> previous;

  /** . */
  Segment<E> next;

  public final E get(int index) {
    if (index < 0) {
      if (previous == null) {
        throw new IndexOutOfBoundsException();
      } else {
        return previous.get(index + previous.localSize());
      }
    }

    //
    int localSize = localSize();
    if (index >= localSize) {
      if (next == null) {
        throw new IndexOutOfBoundsException();
      } else {
        return next.get(index - localSize);
      }
    }

    //
    return localGet(index);
  }

  public void add(int index, E e) {
    if (next == null) {
      throw new IndexOutOfBoundsException();
    }
    next.add(index, e);
  }

  public E remove(int index) {
    if (next == null) {
      throw new IndexOutOfBoundsException();
    }
    return next.remove(index);
  }

  public int size() {
    if (next == null) {
      return 0;
    }
    return next.size();
  }

  public Iterator<E> iterator() {
    return new IteratorImpl<E>(this);
  }

  protected abstract E localGet(int index);

  protected abstract int localSize();

  protected abstract Iterator<E> localIterator();
}
