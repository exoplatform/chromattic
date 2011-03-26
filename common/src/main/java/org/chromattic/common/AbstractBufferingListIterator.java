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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AbstractBufferingListIterator<E> implements ListIterator<E> {

  /** . */
  private static final Object MARKER = new Object();

  /** . */
  private final ListModel<E> model;

  /** . */
  private final ArrayList<E> elements;

  /** . */
  private Iterator<E> iterator;

  /** . */
  private int offset;

  /** The last returned element or the value <tt>MARKER</tt>. */
  private E e;

  public AbstractBufferingListIterator(ListModel<E> model) {
    @SuppressWarnings("unchecked") E e = (E)MARKER;

    //
    this.model = model;
    this.iterator = model.iterator();
    this.elements = new ArrayList<E>();
    this.offset = 0;
    this.e = e;
  }

  public boolean hasNext() {
    return offset > 0 || iterator.hasNext();
  }

  public boolean hasPrevious() {
    return offset < elements.size();
  }

  public E next() {
    if (offset == 0) {
      E next = iterator.next();
      elements.add(next);
      e = next;
      return next;
    } else {
      offset--;
      int index = elements.size() - (offset + 1);
      E next = elements.get(index);
      e = next;
      return next;
    }
  }

  public E previous() {
    int index = elements.size() - (offset + 1);
    if (index < 0) {
      throw new NoSuchElementException();
    }
    offset++;
    E previous = elements.get(index);
    e = previous;
    return previous;
  }

  public int nextIndex() {
    return elements.size() - (offset - 1);
  }

  public int previousIndex() {
    return elements.size() - (offset + 1);
  }

  public void remove() {
    if (e == MARKER) {
      throw new IllegalStateException();
    }

    // Compute index
    int index = elements.size() - (offset + 1);

    // Update model state
    model.remove(index, e);

    // Update local state
    elements.remove(index);

    // Mark for IllegalStateException
    @SuppressWarnings("unchecked") E tmp = (E)MARKER;
    e = tmp;

    // Renew the iterator
    iterator = model.iterator();
    int length = elements.size();
    while (length-- > 0) {
      iterator.next();
    }
  }

  public void add(E e) {
    // Compute index
    int index = elements.size() - offset;

    // Update model state
    model.add(index, e);

    // Update local state
    elements.add(index, e);
    offset++;

    // Renew the iterator
    iterator = model.iterator();
    int length = elements.size();
    while (length-- > 0) {
      iterator.next();
    }
  }

  public void set(E e) {
    if (this.e == MARKER) {
      throw new IllegalStateException();
    }

    // Compute index
    int index = elements.size() - (offset + 1);

    // Update model state
    model.set(index, this.e, e);

    // Update local state
    elements.set(index, e);

    // Renew the iterator
    iterator = model.iterator();
    int length = elements.size();
    while (length-- > 0) {
      iterator.next();
    }
  }
}
