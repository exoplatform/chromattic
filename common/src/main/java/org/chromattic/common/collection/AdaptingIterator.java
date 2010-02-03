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

package org.chromattic.common.collection;

import org.chromattic.common.TypeAdapter;

import java.util.Iterator;

/**
 * An iterator that adapts a type to another type.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @param <O> the outter type
 * @param <I> the inner type
 */
public class AdaptingIterator<O, I> implements Iterator<O> {

  /** . */
  private final Iterator<I> iterator;

  /** . */
  private final TypeAdapter<O, I> adapter;

  /**
   * Build a new adapting iterator.
   *
   * @param iterator the iterator
   * @param adapter the adapter
   * @throws NullPointerException if any argument is null
   */
  public AdaptingIterator(Iterator<I> iterator, TypeAdapter<O, I> adapter) throws NullPointerException {
    if (iterator == null) {
      throw new NullPointerException();
    }
    if (adapter == null) {
      throw new NullPointerException();
    }
    this.iterator = iterator;
    this.adapter = adapter;
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  public O next() {
    I i = iterator.next();
    return adapter.adapt(i);
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
