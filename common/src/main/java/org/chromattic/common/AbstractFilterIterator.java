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

package org.chromattic.common;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that filter the elements of a delegate iterator allowing to skip some elements and
 * to perform type conversion.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractFilterIterator<E, I> implements Iterator<E> {

  /** . */
  private Iterator<I> iterator;

  /** . */
  private E next;

  /**
   * Create a new filter iterator.
   *
   * @param iterator the iterator
   * @throws NullPointerException if the iterator is null
   */
  public AbstractFilterIterator(Iterator<I> iterator) throws NullPointerException {
    if (iterator == null) {
      throw new NullPointerException();
    }

    //
    this.iterator = iterator;
  }

  public final boolean hasNext() {
    if (next == null) {
      bilto:
      if (iterator != null) {
        while (iterator.hasNext()) {
          I internal = iterator.next();
          E external = adapt(internal);
          if (external != null) {
            next = external;
            break bilto;
          }
        }
        iterator = null;
      }
    }

    //
    return next != null;
  }

  public final E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    //
    E tmp = next;
    next = null;

    //
    return tmp;
  }

  public void remove() {
    iterator.remove();
  }

  /**
   * Adapts the internal element as an external element. Returning a null external element means that
   * the element must be skipped and not considered by the iterator.
   *
   * @param internal the internal element
   * @return the external element
   */
  protected abstract E adapt(I internal);
}