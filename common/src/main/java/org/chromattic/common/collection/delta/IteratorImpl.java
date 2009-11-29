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
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class IteratorImpl<E> implements Iterator<E> {

  /** The current segment. */
  private Segment<E> segment;

  /** The iterator of the current segment. */
  private Iterator<E> iterator;

  public IteratorImpl(Segment<E> segment) {
    this.segment = segment;
    this.iterator = segment.localIterator();
  }

  public boolean hasNext() {
    while (true) {
      if (iterator.hasNext()) {
        return true;
      }
      if (!segment.hasNext()) {
        return false;
      }
      segment = segment.getNext();
      iterator = segment.localIterator();
    }
  }

  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return iterator.next();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
