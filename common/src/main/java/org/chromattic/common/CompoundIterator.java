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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CompoundIterator<E> implements Iterator<E> {

  /** . */
  private Iterator<Iterator<E>> iteratorIterator;

  /** . */
  private Iterator<E> current;

  public CompoundIterator(Iterator<Iterator<E>> iteratorIterator) {
    this.iteratorIterator = iteratorIterator;
  }

  public CompoundIterator(List<Iterator<E>> iterators) {
    this(iterators.iterator());
  }

  public CompoundIterator(Iterator<E>... iterators) {
    this(Arrays.asList(iterators));
  }

  public boolean hasNext() {
    if (iteratorIterator == null) {
      return false;
    }
    while (current == null || !current.hasNext()) {
      if (iteratorIterator.hasNext()) {
        current = iteratorIterator.next();
      } else {
        iteratorIterator = null;
        return false;
      }
    }
    return true;
  }

  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return current.next();
  }

  public void remove() {
    current.remove();
  }
}
