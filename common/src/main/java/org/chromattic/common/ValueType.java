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

import java.util.Date;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

/**
 * The enumeration of value type that are permitted for attributes state. The generic type is used to represent
 * the relevant associated java type for the runtime values.
 *
 * @param <T> the value java type
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueType<T> {

  /** . */
  public static final ValueType<String> STRING = new ValueType<String>() {};

  /** . */
  public static final ValueType<Integer> INTEGER = new ValueType<Integer>() {};

  /** . */
  public static final ValueType<Boolean> BOOLEAN = new ValueType<Boolean>() {};

  /** . */
  public static final ValueType<Date> DATE = new ValueType<Date>() {};

  /** . */
  public static final ValueType<Double> DOUBLE = new ValueType<Double>() {};

  /** . */
  private final Class<T> javaType;

  @SuppressWarnings("unchecked")
  private ValueType() {
    Type type = getClass().getGenericSuperclass();
    ParameterizedType parameterizedType = (ParameterizedType)type;
    javaType = (Class<T>)parameterizedType.getActualTypeArguments()[0];
  }

  /**
   * Returns the java type.
   *
   * @return the java type
   */
  public Class<T> getJavaType() {
    return javaType;
  }

  /**
   * Returns true if the object matches the type.
   *
   * @param o the object
   * @return true when the value matches the type
   */
  public boolean isInstance(Object o) {
    return javaType.isInstance(o);
  }

  /**
   * Casts the object to the underlying java type.
   *
   * @param o the object to cast
   * @return the casted object
   * @throws ClassCastException if the object cannot be casted
   */
  public T cast(Object o) throws ClassCastException {
    if (o == null) {
      return null;
    }
    if (javaType.isInstance(o)) {
      return javaType.cast(o);
    }
    throw new ClassCastException("Object " + o + " cannot be casted to " + javaType.getName());
  }

  /**
   * Returns the corresponding value type for the specified object or null if no valid one can be found.
   *
   * @param t the object to decode type for
   * @param <T> the java type
   * @return the decoded type
   */
  @SuppressWarnings("unchecked")
  public static <T> ValueType<T> decode(T t) {
    if (t == null) {
      return null;
    }
    if (t instanceof String) {
      return (ValueType<T>)ValueType.STRING;
    }
    if (t instanceof Integer) {
      return (ValueType<T>)ValueType.INTEGER;
    }
    if (t instanceof Boolean) {
      return (ValueType<T>)ValueType.BOOLEAN;
    }
    if (t instanceof Date) {
      return (ValueType<T>)ValueType.DATE;
    }
    if (t instanceof Double) {
      return (ValueType<T>)ValueType.DOUBLE;
    }
    return null;
  }

  /**
   * Returns the corresponding value type for the specified object.
   *
   * @param t the object to get the type for
   * @param <T> the java type
   * @return the decoded type
   * @throws NullPointerException if the argument is null
   * @throws IllegalArgumentException if the argument does not match a valid type
   */
  @SuppressWarnings("unchecked")
  public static <T> ValueType<T> get(T t) throws NullPointerException, IllegalArgumentException {
    if (t == null) {
      throw new NullPointerException();
    }
    if (t instanceof String) {
      return (ValueType<T>)ValueType.STRING;
    }
    if (t instanceof Integer) {
      return (ValueType<T>)ValueType.INTEGER;
    }
    if (t instanceof Boolean) {
      return (ValueType<T>)ValueType.BOOLEAN;
    }
    if (t instanceof Date) {
      return (ValueType<T>)ValueType.DATE;
    }
    if (t instanceof Double) {
      return (ValueType<T>)ValueType.DOUBLE;
    }
    throw new IllegalArgumentException("Java class " + t.getClass().getName() + " cannot be used as a value type");
  }

  /**
   * Returns the corresponding value type for the specified java class.
   *
   * @param t the java type
   * @param <T> the java type
   * @return the decoded type
   * @throws NullPointerException if the argument is null
   * @throws IllegalArgumentException if the argument does not match a valid type
   */
  @SuppressWarnings("unchecked")
  public static <T> ValueType<T> forClass(Class<T> t) throws NullPointerException, IllegalArgumentException {
    if (t == null) {
      throw new NullPointerException();
    }
    if (t == String.class) {
      return (ValueType<T>)ValueType.STRING;
    }
    if (t == Integer.class) {
      return (ValueType<T>)ValueType.INTEGER;
    }
    if (t == Boolean.class) {
      return (ValueType<T>)ValueType.BOOLEAN;
    }
    if (t == Date.class) {
      return (ValueType<T>)ValueType.DATE;
    }
    if (t == Double.class) {
      return (ValueType<T>)ValueType.DOUBLE;
    }
    throw new IllegalArgumentException("Java class " + t.getClass().getName() + " cannot be used as a value type");
  }

  @Override
  public String toString() {
    return "ValueType[" + javaType.getSimpleName() + "]";
  }
}
