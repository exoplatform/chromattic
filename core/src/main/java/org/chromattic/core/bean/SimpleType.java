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

package org.chromattic.core.bean;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple type as exposed to the programming model. A simple type is defined by:
 *
 * <ul>
 *   <li>The object type which is java type for object representing the type</li>
 *   <li>The real type which is the java type as wanted by the model meta data</li>
 *   <li>The primitiveness of the type</li>
 * </ul>
 *
 * The real type is most of the time used when there is a need for creating arrays with a component
 * type equals to the real type.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleType<E> {

  /** . */
  public final static SimpleType<String> PATH = new SimpleType<String>(String.class);

  /** . */
  public static final SimpleType<Integer> PRIMITIVE_INTEGER = new SimpleType<Integer>(int.class);

  /** . */
  public static final SimpleType<Boolean> PRIMITIVE_BOOLEAN = new SimpleType<Boolean>(boolean.class);

  /** . */
  public static final SimpleType<Long> PRIMITIVE_LONG = new SimpleType<Long>(long.class);

  /** . */
  public static final SimpleType<Double> PRIMITIVE_DOUBLE = new SimpleType<Double>(double.class);

  /** . */
  public static final SimpleType<Float> PRIMITIVE_FLOAT = new SimpleType<Float>(float.class);

  /** . */
  public static final SimpleType<Integer> INTEGER = new SimpleType<Integer>(Integer.class);

  /** . */
  public static final SimpleType<Boolean> BOOLEAN = new SimpleType<Boolean>(Boolean.class);

  /** . */
  public static final SimpleType<Long> LONG = new SimpleType<Long>(Long.class);

  /** . */
  public static final SimpleType<Double> DOUBLE = new SimpleType<Double>(Double.class);

  /** . */
  public static final SimpleType<Float> FLOAT = new SimpleType<Float>(Float.class);

  /** . */
  public static final SimpleType<String> STRING = new SimpleType<String>(String.class);

  /** . */
  public static final SimpleType<InputStream> STREAM = new SimpleType<InputStream>(InputStream.class);

  /** . */
  public static final SimpleType<Date> DATE = new SimpleType<Date>(Date.class);

  /** . */
  private static final Map<Class, SimpleType<?>> builtin = new HashMap<Class, SimpleType<?>>();

  static {
    add(PATH);
    add(PRIMITIVE_INTEGER);
    add(PRIMITIVE_BOOLEAN);
    add(PRIMITIVE_LONG);
    add(PRIMITIVE_DOUBLE);
    add(PRIMITIVE_FLOAT);
    add(INTEGER);
    add(BOOLEAN);
    add(LONG);
    add(DOUBLE);
    add(FLOAT);
    add(STRING);
    add(STREAM);
    add(DATE);
  }

  private static void add(SimpleType<?> type) {
    builtin.put(type.getRealType(), type);
  }

  public static SimpleType<?> create(Class<?> type) {
    if (type.isEnum()) {
      return new SimpleType<Object>(type);
    } else {
      return builtin.get(type);
    }
  }

  /** . */
  private final Class<E> objectType;

  /** . */
  private final Class<?> realType;

  private SimpleType(Class<?> realType) {

    //
    Class<?> objectType;
    if (realType.isPrimitive()) {
      if (realType == int.class) {
        objectType = Integer.class;
      } else if (realType == boolean.class) {
        objectType = Boolean.class;
      } else if (realType == long.class) {
        objectType = Long.class;
      } else if (realType == float.class) {
        objectType = Float.class;
      } else if (realType == double.class) {
        objectType = Double.class;
      } else {
        throw new UnsupportedOperationException();
      }
    } else {
      objectType = realType;
    }

    this.objectType = (Class<E>)objectType;
    this.realType = realType;
  }

  public boolean isPrimitive() {
    return realType.isPrimitive();
  }

  public Class<E> getObjectType() {
    return objectType;
  }

  public Class<?> getRealType() {
    return realType;
  }

  @Override
  public String toString() {
    return "SimpleType[objectType=" + objectType.getName() + ",realType=" + realType.getName() + "]";
  }
}
