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

import org.chromattic.api.BuilderException;
import org.chromattic.api.annotations.DefaultValue;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.SimpleTypeInfo;

import java.lang.annotation.Annotation;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueInfo<V> extends ValueInfo {

  /** . */
  private final SimpleType<V> simpleType;

  /** . */
  private List<V> defaultValue;

  private SimpleValueInfo(ClassTypeInfo typeInfo, SimpleType<V> simpleType, List<V> defaultValue) {
    super(typeInfo);

    // Make a safe clone to prevent modifications and make the object immutable
    if (defaultValue != null) {
      defaultValue = Collections.unmodifiableList(new ArrayList<V>(defaultValue));
    }

    //
    this.simpleType = simpleType;
    this.defaultValue = defaultValue;
  }

  public SimpleType<V> getSimpleType() {
    return simpleType;
  }

  public List<V> getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String toString() {
    return "SimpleValueInfo[simpleType=" + simpleType + "]";
  }

  public static SimpleValueInfo<String> createPath(ClassTypeInfo typeInfo) {
    if (typeInfo == null) {
      throw new NullPointerException();
    }

    //
    if (typeInfo.getName().equals(String.class.getName())) {
      return new SimpleValueInfo<String>(typeInfo, ObjectSimpleType.PATH, null);
    } else {
      throw new IllegalArgumentException("Simple value of type path must have a type of " + String.class.getName());
    }
  }

  /**
   * Build a simple value info meta data.
   *
   * @param typeInfo the type info
   * @param defaultValue the default value
   * @return the simple value info
   * @throws BuilderException any exception that may prevent the correct building such as having a default value that
   *         does not match the type
   */
  public static SimpleValueInfo<?> create(ClassTypeInfo typeInfo, Annotation defaultValue) throws BuilderException {
    if (typeInfo == null) {
      throw new NullPointerException();
    }

    //
    if (typeInfo instanceof SimpleTypeInfo) {
      boolean primitive = ((SimpleTypeInfo)typeInfo).isPrimitive();
      if (!primitive) {
        if (defaultValue != null) {
          throw new BuilderException("Non primitive property cannot have a default value");
        }
        switch (((SimpleTypeInfo)typeInfo).getLiteralType()) {
          case BOOLEAN: {
            return new SimpleValueInfo<Boolean>(typeInfo, ObjectSimpleType.BOOLEAN, null);
          }
          case INT: {
            return new SimpleValueInfo<Integer>(typeInfo, ObjectSimpleType.INT, null);
          }
          case LONG: {
            return new SimpleValueInfo<Long>(typeInfo, ObjectSimpleType.LONG, null);
          }
          case FLOAT: {
            return new SimpleValueInfo<Float>(typeInfo, ObjectSimpleType.FLOAT, null);
          }
          case DOUBLE: {
            return new SimpleValueInfo<Double>(typeInfo, ObjectSimpleType.DOUBLE, null);
          }
          default:
            throw new AssertionError();
        }
      } else {
        switch (((SimpleTypeInfo)typeInfo).getLiteralType()) {
          case BOOLEAN: {
            List<Boolean> defaultBoolean = null;
            if (defaultValue != null) {
              if (defaultValue instanceof DefaultValue.Boolean) {
                boolean[] tmp = ((DefaultValue.Boolean)defaultValue).value();
                defaultBoolean = new ArrayList<Boolean>(tmp.length);
                for (boolean b : tmp) {
                  defaultBoolean.add(b);
                }
              } else {
                throw new BuilderException();
              }
            }
            return new SimpleValueInfo<Boolean>(typeInfo, PrimitiveSimpleType.BOOLEAN, defaultBoolean);
          }
          case INT: {
            List<Integer> defaultInteger = null;
            if (defaultValue != null) {
              if (defaultValue instanceof DefaultValue.Int) {
                int[] tmp = ((DefaultValue.Int)defaultValue).value();
                defaultInteger = new ArrayList<Integer>(tmp.length);
                for (int i : tmp) {
                  defaultInteger.add(i);
                }
              } else {
                throw new BuilderException();
              }
            }
            return new SimpleValueInfo<Integer>(typeInfo, PrimitiveSimpleType.INT, defaultInteger);
          }
          case LONG: {
            List<Long> defaultLong = null;
            if (defaultValue != null) {
              if (defaultValue instanceof DefaultValue.Long) {
                long[] tmp = ((DefaultValue.Long)defaultValue).value();
                defaultLong = new ArrayList<Long>(tmp.length);
                for (long l : tmp) {
                  defaultLong.add(l);
                }
              } else {
                throw new BuilderException();
              }
            }
            return new SimpleValueInfo<Long>(typeInfo, PrimitiveSimpleType.LONG, defaultLong);
          }
          case FLOAT: {
            List<Float> defaultFloat = null;
            if (defaultValue != null) {
              if (defaultValue instanceof DefaultValue.Float) {
                float[] tmp = ((DefaultValue.Float)defaultValue).value();
                defaultFloat = new ArrayList<Float>(tmp.length);
                for (float f : tmp) {
                  defaultFloat.add(f);
                }
              } else {
                throw new BuilderException();
              }
            }
            return new SimpleValueInfo<Float>(typeInfo, PrimitiveSimpleType.FLOAT, defaultFloat);
          }
          case DOUBLE: {
            List<Double> defaultDouble = null;
            if (defaultValue != null) {
              if (defaultValue instanceof DefaultValue.Double) {
                double[] tmp = ((DefaultValue.Double)defaultValue).value();
                defaultDouble = new ArrayList<Double>(tmp.length);
                for (double d : tmp) {
                  defaultDouble.add(d);
                }
              } else {
                throw new BuilderException();
              }
            }
            return new SimpleValueInfo<Double>(typeInfo, PrimitiveSimpleType.DOUBLE, defaultDouble);
          }
          default:
            throw new AssertionError();
        }
      }
    } else {
      if (defaultValue != null) {
        throw new BuilderException("Non primitive property cannot have a default value");
      }

      //
      if (String.class.getName().equals(typeInfo.getName())) {
        return new SimpleValueInfo<String>(typeInfo, ObjectSimpleType.STRING, null);
      } else if (Date.class.getName().equals(typeInfo.getName())) {
        return new SimpleValueInfo<Date>(typeInfo, ObjectSimpleType.DATE, null);
      } else if (InputStream.class.getName().equals(typeInfo.getName())) {
        return new SimpleValueInfo<InputStream>(typeInfo, ObjectSimpleType.BINARY, null);
      } else {
        throw new AssertionError();
      }
    }
  }
}
