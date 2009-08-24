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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ElementPosition<E> {

  private ElementPosition() {
  }

  public abstract int getIndex();

  public static class First<E> extends ElementPosition<E> {

    /** . */
    private final E next;

    public First(E next) {
      this.next = next;
    }

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
        return next.equals(that.next);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementPosition.First[next=" + next + "]";
    }
  }

  public static class Middle<E> extends ElementPosition<E> {

    /** . */
    private final int index;

    /** . */
    private final E previous;

    /** . */
    private final E next;

    public Middle(int index, E previous, E next) {
      this.index = index;
      this.previous = previous;
      this.next = next;
    }

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
        return previous.equals(that.previous) && next.equals(that.next);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementPosition.Previous[previous=" + previous + ",next=" + next + "]";
    }
  }

  public static class Last<E> extends ElementPosition<E> {

    /** . */
    private final int index;

    /** . */
    private final E previous;

    public Last(int index, E previous) {
      this.index = index;
      this.previous = previous;
    }

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
        return previous.equals(that.previous);
      }
      return false;
    }

    @Override
    public String toString() {
      return "ElementPosition.Last[previous=" + previous + "]";
    }
  }
}
