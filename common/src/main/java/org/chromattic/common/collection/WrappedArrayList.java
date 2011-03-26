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

import java.lang.reflect.Array;
import java.util.AbstractList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class WrappedArrayList<E, A> extends AbstractList<E> {

  public static <E, A> WrappedArrayList<E, A> create(Class<E> elementType, Class<?> componentType, int size) {
    if (elementType == null) {
      throw new NullPointerException("No null element type can be provided");
    }
    if (size < 0) {
      throw new IllegalArgumentException("No negative sized array can be created (" + size + ")");
    }
    return wrap(elementType, Array.newInstance(componentType, size));
  }

  public static <E, A> WrappedArrayList<E, A> wrap(Class<E> elementType, Object array) {
    if (elementType == null) {
      throw new NullPointerException("No null element type can be provided");
    }
    Class<?> arrayClass = array.getClass();
    if (!arrayClass.isArray()) {
      throw new IllegalArgumentException("Provided array is not an array");
    }
    Class<?> componentType = arrayClass.getComponentType();
    if (componentType.isPrimitive()) {
      if (elementType == Integer.class) {
        if (!arrayClass.getComponentType().equals(int.class)) {
          throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to int");
        }
        @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new IntWrappedArrayList((int[])array);
        return list;
      }  else if (elementType == Boolean.class) {
        if (!componentType.equals(boolean.class)) {
          throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to boolean");
        }
        @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new BooleanWrappedArrayList((boolean[])array);
        return list;
      } else if (elementType == Long.class) {
        if (!componentType.equals(long.class)) {
          throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to long");
        }
        @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new LongWrappedArrayList((long[])array);
        return list;
      } else if (elementType == Double.class) {
        if (!componentType.equals(double.class)) {
          throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to double");
        }
        @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new DoubleWrappedArrayList((double[])array);
        return list;
      } else if (elementType == Float.class) {
        if (!componentType.equals(float.class)) {
          throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to float");
        }
        @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new FloatWrappedArrayList((float[])array);
        return list;
      } else {
        throw new UnsupportedOperationException("Cannot create wrapper of class " + elementType);
      }
    } else {
      if (!componentType.equals(elementType)) {
        throw new IllegalArgumentException("Cannot wrap array with component type " + componentType.getName() + " to " + elementType.getName());
      }

      //
      @SuppressWarnings("unchecked") WrappedArrayList<E, A> list = (WrappedArrayList<E, A>)new GenericWrappedArrayList<E>((E[])array);
      return list;
    }
  }

  /** . */
  private final A array;

  /** . */
  private final int size;

  protected WrappedArrayList(A array) throws NullPointerException {
    this.array = array;
    this.size = size(array);
  }

  protected abstract E get(A array, int index);

  protected abstract void set(A array, int index, E element);

  protected abstract int size(A array);

  public final A getArray() {
    return array;
  }

  @Override
  public final E get(int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("No negative index " + index);
    }
    if (index >= size) {
      throw new IndexOutOfBoundsException("No index greater than the list size " + index);
    }

    //
    return get(array, index);
  }

  @Override
  public final E set(int index, E element) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("No negative index " + index);
    }
    if (index >= size) {
      throw new IndexOutOfBoundsException("No index greater than the list size " + index);
    }

    //
    E previous = get(array, index);
    set(array, index, element);
    return previous;
  }

  @Override
  public final int size() {
    return size;  
  }
}
