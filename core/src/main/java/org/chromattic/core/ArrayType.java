package org.chromattic.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public abstract class ArrayType<A, E> {

  public abstract A create(int size);

  public abstract E get(A array, int index);

  public abstract void set(A array, int index, E element);

  public abstract int size(A array);

  public static <E> ArrayType<List<E>, E> list(Class<E> componentType) {
    return new ArrayType<List<E>, E>() {
      @Override
      public List<E> create(int size) {
        return new ArrayList<E>();
      }
      @Override
      public E get(List<E> array, int index) {
        return array.get(index);
      }
      @Override
      public void set(List<E> array, int index, E element) {
        while (index >= array.size()) {
          array.add(null);
        }
        array.set(index, element);
      }
      @Override
      public int size(List<E> array) {
        return array.size();
      }
    };
  }

  public static <E> ArrayType<E[], E> array(final Class<E> componentType) {
    if (componentType.isPrimitive()) {
      throw new IllegalArgumentException("Component type must not be primitive");
    }
    return new ArrayType<E[], E>() {
      @Override
      public E[] create(int size) {
        if (componentType == boolean.class) {
          return (E[])(Object)new boolean[size];
        } else {
          return (E[])Array.newInstance(componentType, size);
        }
      }
      @Override
      public E get(E[] array, int index) {
        if (componentType == boolean.class) {
          Boolean b = Array.getBoolean(array, index);
          return (E)b;
        } else {
          return array[index];
        }
      }
      @Override
      public void set(E[] array, int index, E element) {
        if (componentType == boolean.class) {
          Array.setBoolean(array, index, (Boolean)element);
        } else {
          array[index] = element;
        }
      }
      @Override
      public int size(E[] array) {
        return array.length;
      }
    };
  }

  public static <E> ArrayType<Object, E> primitiveArray(final Class<E> componentType) {
    if (!componentType.isPrimitive()) {
      throw new IllegalArgumentException("Component type must be primitive");
    }
    return new ArrayType<Object, E>() {
      @Override
      public Object create(int size) {
        if (componentType == boolean.class) {
          return new boolean[size];
        } else if (componentType == int.class) {
          return new int[size];
        } else if (componentType == long.class) {
          return new long[size];
        } else if (componentType == float.class) {
          return new float[size];
        } else if (componentType == double.class) {
          return new double[size];
        } else if (componentType == byte.class) {
          return new byte[size];
        } else if (componentType == char.class) {
          return new char[size];
        } else if (componentType == short.class) {
          return new short[size];
        } else {
          throw new AssertionError();
        }
      }
      @Override
      public E get(Object array, int index) {
        if (componentType == boolean.class) {
          Boolean b = Array.getBoolean(array, index);
          return (E)b;
        } else if (componentType == int.class) {
          Integer i = Array.getInt(array, index);
          return (E)i;
        } else if (componentType == long.class) {
          Long l = Array.getLong(array, index);
          return (E)l;
        } else if (componentType == float.class) {
          Float f = Array.getFloat(array, index);
          return (E)f;
        } else if (componentType == double.class) {
          Double d = Array.getDouble(array, index);
          return (E)d;
        } else if (componentType == byte.class) {
          Byte b = Array.getByte(array, index);
          return (E)b;
        } else if (componentType == char.class) {
          Character b = Array.getChar(array, index);
          return (E)b;
        } else if (componentType == short.class) {
          Short b = Array.getShort(array, index);
          return (E)b;
        } else {
          throw new AssertionError();
        }
      }
      @Override
      public void set(Object array, int index, E element) {
        if (componentType == boolean.class) {
          Array.setBoolean(array, index, (Boolean)element);
        } else if (componentType == int.class) {
          Array.setInt(array, index, (Integer)element);
        } else if (componentType == long.class) {
          Array.setLong(array, index, (Long)element);
        } else if (componentType == float.class) {
          Array.setFloat(array, index, (Float)element);
        } else if (componentType == double.class) {
          Array.setDouble(array, index, (Double) element);
        } else if (componentType == byte.class) {
          Array.setByte(array, index, (Byte) element);
        } else if (componentType == char.class) {
          Array.setChar(array, index, (Character)element);
        } else if (componentType == short.class) {
          Array.setShort(array, index, (Short)element);
        } else {
          throw new AssertionError();
        }
      }
      @Override
      public int size(Object array) {
        return Array.getLength(array);
      }
    };
  }
}
