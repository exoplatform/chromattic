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

package org.chromattic.spi.type;

import org.chromattic.api.TypeConversionException;

import java.io.InputStream;
import java.util.Calendar;

/**
 * The Service Provider Interface for converting simple types.  
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class SimpleTypeConverter<I, E> {

  private SimpleTypeConverter() {
  }

  public abstract static class STRING<E> extends SimpleTypeConverter<String, E> {
    protected STRING() {
    }
    @Override
    public final Class<String> getInternalType() {
      return String.class;
    }
  }

  public abstract static class BINARY<E> extends SimpleTypeConverter<InputStream, E> {
    protected BINARY() {
    }
    @Override
    public final Class<InputStream> getInternalType() {
      return InputStream.class;
    }
  }

  public abstract static class LONG<E> extends SimpleTypeConverter<Long, E> {
    protected LONG() {
    }
    @Override
    public final Class<Long> getInternalType() {
      return Long.class;
    }
  }

  public abstract static class DOUBLE<E> extends SimpleTypeConverter<Double, E> {
    protected DOUBLE() {
    }
    @Override
    public final Class<Double> getInternalType() {
      return Double.class;
    }
  }

  public abstract static class DATE<E> extends SimpleTypeConverter<Calendar, E> {
    protected DATE() {
    }
    @Override
    public final Class<Calendar> getInternalType() {
      return Calendar.class;
    }
  }

  public abstract static class BOOLEAN<E> extends SimpleTypeConverter<Boolean, E> {
    protected BOOLEAN() {
    }
    @Override
    public final Class<Boolean> getInternalType() {
      return Boolean.class;
    }
  }

  public abstract static class NAME<E> extends SimpleTypeConverter<String, E> {
    protected NAME() {
    }
    @Override
    public final Class<String> getInternalType() {
      return String.class;
    }
  }

  public abstract static class PATH<E> extends SimpleTypeConverter<String, E> {
    protected PATH() {
    }
    @Override
    public final Class<String> getInternalType() {
      return String.class;
    }
  }

  public abstract Class<I> getInternalType();

  public abstract Class<E> getExternalType();

  public abstract I getInternal(E e) throws TypeConversionException;

  public abstract E getExternal(I i) throws TypeConversionException;

  public abstract E fromString(String s) throws TypeConversionException;

  public abstract String toString(E e) throws TypeConversionException;

}
