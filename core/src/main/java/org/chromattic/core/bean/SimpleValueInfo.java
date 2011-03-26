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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueInfo<T> extends ValueInfo {

  /** . */
  private final SimpleType<T> simpleType;

  /** . */
  private final boolean primitive;

  /** . */
  private T defaultValue;

  private SimpleValueInfo(ClassTypeInfo typeInfo, SimpleType<T> simpleType, boolean primitive, T defaultValue) {
    super(typeInfo);

    //
    this.simpleType = simpleType;
    this.primitive = primitive;
    this.defaultValue = defaultValue;
  }

  public SimpleType getSimpleType() {
    return simpleType;
  }

  public T getDefaultValue() {
    return defaultValue;
  }

  public boolean isPrimitive() {
    return primitive;
  }

  @Override
  public String toString() {
    return "SimpleValueInfo[simpleType=" + simpleType + ",primitive=" + primitive + "]";
  }

  public static SimpleValueInfo<String> createPath(ClassTypeInfo typeInfo) {
    if (typeInfo == null) {
      throw new NullPointerException();
    }

    //
    if (typeInfo.getName().equals(String.class.getName())) {
      return new SimpleValueInfo<String>(typeInfo, SimpleType.PATH, false, null);
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
      if (!primitive && defaultValue != null) {
        throw new BuilderException("Non primitive property cannot have a default value");
      }

      //
      switch (((SimpleTypeInfo)typeInfo).getLiteralType()) {
        case BOOLEAN:
        {
          Boolean defaultBoolean = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Boolean) {
              defaultBoolean = ((DefaultValue.Boolean)defaultValue).value();
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Boolean>(typeInfo, SimpleType.BOOLEAN, primitive, defaultBoolean);
        }
        case INT:
        {
          Integer defaultInteger = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Int) {
              defaultInteger = ((DefaultValue.Int)defaultValue).value();
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Integer>(typeInfo, SimpleType.INT, primitive, defaultInteger);
        }
        case LONG:
        {
          Long defaultLong = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Long) {
              defaultLong = ((DefaultValue.Long)defaultValue).value();
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Long>(typeInfo, SimpleType.LONG, primitive, defaultLong);
        }
        case FLOAT:
        {
          Float defaultFloat = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Float) {
              defaultFloat = ((DefaultValue.Float)defaultValue).value();
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Float>(typeInfo, SimpleType.FLOAT, primitive, defaultFloat);
        }
        case DOUBLE:
        {
          Double defaultDouble = null;
          if (defaultValue != null) {
            if (defaultValue instanceof DefaultValue.Double) {
              defaultDouble = ((DefaultValue.Double)defaultValue).value();
            } else {
              throw new BuilderException();
            }
          }
          return new SimpleValueInfo<Double>(typeInfo, SimpleType.DOUBLE, primitive, defaultDouble);
        }
        default:
          throw new AssertionError();
      }
    } else {
      if (defaultValue != null) {
        throw new BuilderException("Non primitive property cannot have a default value");
      }

      //
      if (String.class.getName().equals(typeInfo.getName())) {
        String defaultString = null;
        return new SimpleValueInfo<String>(typeInfo, SimpleType.STRING, false, defaultString);
      } else if (Date.class.getName().equals(typeInfo.getName())) {
        return new SimpleValueInfo<Date>(typeInfo, SimpleType.DATE, false, null);
      } else if (InputStream.class.getName().equals(typeInfo.getName())) {
        return new SimpleValueInfo<byte[]>(typeInfo, SimpleType.BINARY, false, null);
      } else {
        throw new AssertionError();
      }
    }
  }
}
