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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class SimpleTypeKind<E, I> {

  private static Class<?> externalType(Class<?> typeKind) {
    Type gst = typeKind.getGenericSuperclass();
    if (gst instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType)gst;
      Type rpt = pt.getRawType();
      if (rpt instanceof Class) {
        Class rptc = (Class)rpt;
        if (rptc.getSuperclass().equals(SimpleTypeKind.class)) {
          Type[] typeArgs = pt.getActualTypeArguments();
          if (typeArgs.length == 1) {
            if (typeArgs[0] instanceof Class) {
              return (Class<?>)typeArgs[0];
            } else {
              throw new IllegalArgumentException("The custom type should extends directly the " + SimpleTypeKind.class.getName() + " class");
            }
          } else {
            throw new IllegalArgumentException("The custom type should extends directly the " + SimpleTypeKind.class.getName() + " class");
          }
        } else {
          throw new IllegalArgumentException("The custom type should extends directly the " + SimpleTypeKind.class.getName() + " class");
        }
      } else {
        throw new IllegalArgumentException("The custom type should extends directly the " + SimpleTypeKind.class.getName() + " class");
      }
    } else {
      throw new IllegalArgumentException("The custom type should extends directly the " + SimpleTypeKind.class.getName() + " class");
    }
  }

  /** . */
  private final Class<I> internalType;

  /** . */
  private final Class<E> externalType;

  private SimpleTypeKind(Class<I> internalType) {
    if (internalType == null) {
      throw new NullPointerException();
    }

    //
    this.internalType = internalType;
    this.externalType = (Class<E>)externalType(getClass());
  }

  private SimpleTypeKind(Class<E> externalType, Class<I> internalType) {
    if (externalType == null) {
      throw new NullPointerException();
    }
    if (internalType == null) {
      throw new NullPointerException();
    }

    //
    this.internalType = internalType;
    this.externalType = externalType;
  }

  public final Class<E> getExternalType() {
    return externalType;
  }

  public abstract static class STRING<E> extends SimpleTypeKind<E, String> {
    protected STRING(Class<E> externalType) {
      super(externalType, String.class);
    }
    protected STRING() {
      super(String.class);
    }
  }

  public abstract static class PATH<E> extends SimpleTypeKind<E, String> {
    protected PATH(Class<E> externalType) {
      super(externalType, String.class);
    }
    protected PATH() {
      super(String.class);
    }
  }

  public abstract static class BOOLEAN<E> extends SimpleTypeKind<E, Boolean> {
    protected BOOLEAN(Class<E> externalType) {
      super(externalType, Boolean.class);
    }
    protected BOOLEAN() {
      super(Boolean.class);
    }
  }

  public abstract static class LONG<E> extends SimpleTypeKind<E, Long> {
    protected LONG(Class<E> externalType) {
      super(externalType, Long.class);
    }
    protected LONG() {
      super(Long.class);
    }
  }

  public abstract static class DATE<E> extends SimpleTypeKind<E, Date> {
    protected DATE(Class<E> externalType) {
      super(externalType, Date.class);
    }
    protected DATE() {
      super(Date.class);
    }
  }

  public abstract static class DOUBLE<E> extends SimpleTypeKind<E, Double> {
    protected DOUBLE(Class<E> externalType) {
      super(externalType, Double.class);
    }
    protected DOUBLE() {
      super(Double.class);
    }
  }

  public abstract static class STREAM<E> extends SimpleTypeKind<E, InputStream> {
    protected STREAM(Class<E> externalType) {
      super(externalType, InputStream.class);
    }
    protected STREAM() {
      super(InputStream.class);
    }
  }

  @Override
  public String toString() {
    // Find the class name before the SimpleTypeKind
    Class<?> current = getClass();
    while (current.getSuperclass() != SimpleTypeKind.class) {
      current = current.getSuperclass();
    }
    return getClass().getSimpleName() + "SimpleTypeKind[kind=" + current.getSimpleName() + ",externalType=" + externalType.getName() + ",internalType=" + internalType.getName() + "]";
  }
}
