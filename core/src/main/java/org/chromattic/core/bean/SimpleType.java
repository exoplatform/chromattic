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

/**
 * A simple type as exposed to the programming model. A simple type is defined by:
 *
 * <ul>
 *   <li>The simple type kind which is the underlying data semantic</li>
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
  public static SimpleType<String> PATH = new SimpleType<String>(BaseSimpleTypes.PATH, String.class);

  /** . */
  public static final SimpleType<Integer> INT = new SimpleType<Integer>(BaseSimpleTypes.INT, int.class);

  /** . */
  public static final SimpleType<Boolean> BOOLEAN = new SimpleType<Boolean>(BaseSimpleTypes.BOOLEAN, boolean.class);

  /** . */
  public static final SimpleType<Long> LONG = new SimpleType<Long>(BaseSimpleTypes.LONG, long.class);

  /** . */
  public static final SimpleType<Double> DOUBLE = new SimpleType<Double>(BaseSimpleTypes.DOUBLE, double.class);

  /** . */
  public static final SimpleType<Float> FLOAT = new SimpleType<Float>(BaseSimpleTypes.FLOAT, float.class);

  /** . */
  private final SimpleTypeKind<E, ?> kind;

  /** . */
  private final Class<E> objectType;

  /** . */
  private final Class<?> realType;

  public SimpleType(SimpleTypeKind<E, ?> kind, Class<?> realType) {

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

    // Do some check
    if (!objectType.equals(kind.getExternalType())) {
      throw new AssertionError();
    }

    this.kind = kind;
    this.objectType = (Class<E>)objectType;
    this.realType = realType;
  }

  public boolean isPrimitive() {
    return realType.isPrimitive();
  }

  public SimpleTypeKind<E, ?> getKind() {
    return kind;
  }

  public Class<E> getObjectType() {
    return objectType;
  }

  public Class<?> getRealType() {
    return realType;
  }
}
