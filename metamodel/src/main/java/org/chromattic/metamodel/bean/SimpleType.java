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

package org.chromattic.metamodel.bean;

import org.reflext.api.ClassKind;
import org.reflext.api.ClassTypeInfo;

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
  public final static SimpleType.Base<String> PATH = new SimpleType.Base<String>(String.class);

  /** . */
  public static final SimpleType.Base<Integer> PRIMITIVE_INTEGER = new SimpleType.Base<Integer>(int.class);

  /** . */
  public static final SimpleType.Base<Boolean> PRIMITIVE_BOOLEAN = new SimpleType.Base<Boolean>(boolean.class);

  /** . */
  public static final SimpleType.Base<Long> PRIMITIVE_LONG = new SimpleType.Base<Long>(long.class);

  /** . */
  public static final SimpleType.Base<Double> PRIMITIVE_DOUBLE = new SimpleType.Base<Double>(double.class);

  /** . */
  public static final SimpleType.Base<Float> PRIMITIVE_FLOAT = new SimpleType.Base<Float>(float.class);

  /** . */
  public static final SimpleType.Base<Integer> INTEGER = new SimpleType.Base<Integer>(Integer.class);

  /** . */
  public static final SimpleType.Base<Boolean> BOOLEAN = new SimpleType.Base<Boolean>(Boolean.class);

  /** . */
  public static final SimpleType.Base<Long> LONG = new SimpleType.Base<Long>(Long.class);

  /** . */
  public static final SimpleType.Base<Double> DOUBLE = new SimpleType.Base<Double>(Double.class);

  /** . */
  public static final SimpleType.Base<Float> FLOAT = new SimpleType.Base<Float>(Float.class);

  /** . */
  public static final SimpleType.Base<String> STRING = new SimpleType.Base<String>(String.class);

  /** . */
  public static final SimpleType.Base<InputStream> STREAM = new SimpleType.Base<InputStream>(InputStream.class);

  /** . */
  public static final SimpleType.Base<Date> DATE = new SimpleType.Base<Date>(Date.class);

  /** . */
  private static final Map<String, SimpleType.Base<?>> builtin = new HashMap<String, SimpleType.Base<?>>();

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

  private static void add(SimpleType.Base<?> type) {
    builtin.put(type.getRealType().getName(), type);
  }

  public static SimpleType<?> create(ClassTypeInfo type) {
    if (type.getKind() == ClassKind.ENUM) {
      return new Enumerated(type);
    } else {
      return builtin.get(type.getName());
    }
  }

  public static class Base<E> extends SimpleType<E> {

    /** . */
    private final Class<E> objectType;

    /** . */
    private final Class<?> realType;

    private Base(Class<?> realType) {

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
      return "SimpleType.Base[objectType=" + objectType.getName() + ",realType=" + realType.getName() + "]";
    }
  }

  public static class Enumerated extends SimpleType<String> {

    /** . */
    private final ClassTypeInfo typeInfo;

    public Enumerated(ClassTypeInfo typeInfo) {
      this.typeInfo = typeInfo;
    }

    public ClassTypeInfo getTypeInfo() {
      return typeInfo;
    }
  }
}
