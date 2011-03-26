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
package org.chromattic.api;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class SimpleTypeKind<E, I> {

  private static <E> Class<?> externalType(Class<?> typeKind) {
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

  private final Class<E> externalType;

  private SimpleTypeKind() {
    externalType = (Class<E>)externalType(getClass());
  }

  public final Class<E> getExternalType() {
    return externalType;
  }

  public abstract E toExternal(I internal);

  public abstract I toInternal(E external);

  public abstract static class STRING<E> extends SimpleTypeKind<E, String> { }

  public abstract static class PATH<E> extends SimpleTypeKind<E, String> { }

  public abstract static class BOOLEAN<E> extends SimpleTypeKind<E, Boolean> { }

  public abstract static class LONG<E> extends SimpleTypeKind<E, Long> { }

  public abstract static class DATE<E> extends SimpleTypeKind<E, Date> { }

  public abstract static class DOUBLE<E> extends SimpleTypeKind<E, Double> { }

  public abstract static class STREAM<E> extends SimpleTypeKind<E, InputStream> { }

}
