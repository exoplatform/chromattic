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

/**
 * An object that indicates the insertion of an element in a list.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ElementInsertion<E> {

  /** . */
  protected final E element;

  private ElementInsertion(E element) {
    this.element = element;
  }

  /**
   * <p>In case of a list addition the index indicates the value that would be returned by
   * a call to the {@link java.util.ListIterator#nextIndex()} when the element is inserted
   * in the list.</p>
   *
   * @return the index
   */
  public abstract int getIndex();

  /**
   * <p>Returns the inserted element.</p>
   *
   * @return the inserted element
   */
  public E getElement() {
    return element;
  }

  public final static class Singleton<E> extends ElementInsertion<E> {

    public Singleton(E element) {
      super(element);
    }

    public int getIndex() {
      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Singleton) {
        Singleton that = (Singleton)obj;
        return element.equals(that.element);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementInsertion.Singleton[element=" + element + "]";
    }
  }

  public final static class First<E> extends ElementInsertion<E> {

    /** . */
    private final E next;

    public First(E element, E next) {
      super(element);

      //
      this.next = next;
    }

    @Override
    public int getIndex() {
      return 0;
    }

    public E getNext() {
      return next;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof First) {
        First that = (First)obj;
        return next.equals(that.next) && element.equals(that.element);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementInsertion.First[element=" + element + ",next=" + next + "]";
    }
  }

  public final static class Middle<E> extends ElementInsertion<E> {

    /** . */
    private final int index;

    /** . */
    private final E previous;

    /** . */
    private final E next;

    public Middle(int index, E previous, E element, E next) {
      super(element);

      //
      this.index = index;
      this.previous = previous;
      this.next = next;
    }

    @Override
    public int getIndex() {
      return index;
    }

    public E getPrevious() {
      return previous;
    }

    public E getNext() {
      return next;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Middle) {
        Middle that = (Middle)obj;
        return previous.equals(that.previous) && next.equals(that.next) && element.equals(that.element);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementInsertion.Previous[previous=" + previous + ",element=" + element +  ",next=" + next + "]";
    }
  }

  public final static class Last<E> extends ElementInsertion<E> {

    /** . */
    private final int index;

    /** . */
    private final E previous;

    public Last(int index, E previous, E element) {
      super(element);

      //
      this.index = index;
      this.previous = previous;
    }

    @Override
    public int getIndex() {
      return index;
    }

    public E getPrevious() {
      return previous;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Last) {
        Last that = (Last)obj;
        return previous.equals(that.previous) && element.equals(that.element);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementInsertion.Last[previous=" + previous + ",element=" + element + "]";
    }
  }
}
