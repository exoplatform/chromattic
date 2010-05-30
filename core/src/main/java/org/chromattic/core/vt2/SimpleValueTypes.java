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

package org.chromattic.core.vt2;

import org.chromattic.api.TypeConversionException;
import org.chromattic.spi.type.ValueType;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueTypes {

  public static ValueType.STRING<String> STRING = new ValueType.STRING<String>() {
    @Override
    public String getInternal(String s) {
      return s;
    }
    @Override
    public String getExternal(String s) {
      return s;
    }
    @Override
    public String fromString(String s) {
      return s;
    }
    @Override
    public String toString(String s) throws TypeConversionException {
      return s;
    }
    @Override
    public Class<String> getExternalType() {
      return String.class;
    }
  };

  public static ValueType.PATH<String> PATH = new ValueType.PATH<String>() {
    @Override
    public String getInternal(String s) {
      return s;
    }
    @Override
    public String getExternal(String s) {
      return s;
    }
    @Override
    public String fromString(String s) {
      return s;
    }
    @Override
    public String toString(String s) throws TypeConversionException {
      return s;
    }
    @Override
    public Class<String> getExternalType() {
      return String.class;
    }
  };

  public static ValueType.NAME<String> NAME = new ValueType.NAME<String>() {
    @Override
    public String getInternal(String s) {
      return s;
    }
    @Override
    public String getExternal(String s) {
      return s;
    }
    @Override
    public String fromString(String s) {
      return s;
    }
    @Override
    public String toString(String s) throws TypeConversionException {
      return s;
    }
    @Override
    public Class<String> getExternalType() {
      return String.class;
    }
  };

  public static ValueType.LONG<Integer> INTEGER = new ValueType.LONG<Integer>() {
    @Override
    public Long getInternal(Integer external) {
      return (long)external;
    }
    @Override
    public Integer getExternal(Long internal) {
      return (int)(long)internal;
    }
    @Override
    public Integer fromString(String s) {
      return Integer.parseInt(s);
    }
    @Override
    public String toString(Integer integer) throws TypeConversionException {
      return integer.toString();
    }
    @Override
    public Class<Integer> getExternalType() {
      return Integer.class;
    }
  };

  public static ValueType.LONG<Integer> PRIMITIVE_INTEGER = new ValueType.LONG<Integer>() {
    @Override
    public Long getInternal(Integer external) {
      return (long)external;
    }
    @Override
    public Integer getExternal(Long internal) {
      return (int)(long)internal;
    }
    @Override
    public Integer fromString(String s) {
      return Integer.parseInt(s);
    }
    @Override
    public String toString(Integer integer) throws TypeConversionException {
      return integer.toString();
    }
    @Override
    public Class<Integer> getExternalType() {
      return Integer.class;
    }
  };

  public static ValueType.LONG<Long> LONG = new ValueType.LONG<Long>() {
    @Override
    public Long getInternal(Long external) {
      return external;
    }
    @Override
    public Long getExternal(Long internal) {
      return internal;
    }
    @Override
    public Long fromString(String s) {
      return Long.parseLong(s);
    }
    @Override
    public String toString(Long aLong) throws TypeConversionException {
      return aLong.toString();
    }

    @Override
    public Class<Long> getExternalType() {
      return Long.class;
    }
  };

  public static ValueType.LONG<Long> PRIMITIVE_LONG = new ValueType.LONG<Long>() {
    @Override
    public Long getInternal(Long external) {
      return (long)external;
    }
    @Override
    public Long getExternal(Long internal) {
      return internal;
    }
    @Override
    public Long fromString(String s) {
      return Long.parseLong(s);
    }
    @Override
    public String toString(Long aLong) throws TypeConversionException {
      return aLong.toString();
    }
    @Override
    public Class<Long> getExternalType() {
      return Long.class;
    }
  };

  public static ValueType.DOUBLE<Double> DOUBLE = new ValueType.DOUBLE<Double>() {
    @Override
    public Double getInternal(Double external) {
      return external;
    }
    @Override
    public Double getExternal(Double internal) {
      return internal;
    }
    @Override
    public Double fromString(String s) {
      return Double.parseDouble(s);
    }
    @Override
    public String toString(Double aDouble) throws TypeConversionException {
      return aDouble.toString();
    }
    @Override
    public Class<Double> getExternalType() {
      return Double.class;
    }
  };

  public static ValueType.DOUBLE<Double> PRIMITIVE_DOUBLE = new ValueType.DOUBLE<Double>() {
    @Override
    public Double getInternal(Double external) {
      return external;
    }
    @Override
    public Double getExternal(Double internal) {
      return internal;
    }
    @Override
    public Double fromString(String s) {
      return Double.parseDouble(s);
    }
    @Override
    public String toString(Double aDouble) throws TypeConversionException {
      return aDouble.toString();
    }
    @Override
    public Class<Double> getExternalType() {
      return Double.class;
    }
  };

  public static ValueType.DOUBLE<Float> FLOAT = new ValueType.DOUBLE<Float>() {
    @Override
    public Double getInternal(Float external) {
      return (double)(float)external;
    }
    @Override
    public Float getExternal(Double internal) {
      return (float)(double)internal;
    }
    @Override
    public Float fromString(String s) {
      return Float.parseFloat(s);
    }
    @Override
    public String toString(Float aFloat) throws TypeConversionException {
      return aFloat.toString();
    }
    @Override
    public Class<Float> getExternalType() {
      return Float.class;
    }
  };

  public static ValueType.DOUBLE<Float> PRIMITIVE_FLOAT = new ValueType.DOUBLE<Float>() {
    @Override
    public Double getInternal(Float external) {
      return (double)(float)external;
    }
    @Override
    public Float getExternal(Double internal) {
      return (float)(double)internal;
    }
    @Override
    public Float fromString(String s) {
      return Float.parseFloat(s);
    }
    @Override
    public String toString(Float aFloat) throws TypeConversionException {
      return aFloat.toString();
    }
    @Override
    public Class<Float> getExternalType() {
      return Float.class;
    }
  };

  public static ValueType.BOOLEAN<Boolean> BOOLEAN = new ValueType.BOOLEAN<Boolean>() {
    @Override
    public Boolean getInternal(Boolean external) {
      return external;
    }
    @Override
    public Boolean getExternal(Boolean internal) {
      return internal;
    }
    @Override
    public Boolean fromString(String s) {
      return Boolean.parseBoolean(s);
    }
    @Override
    public String toString(Boolean aBoolean) throws TypeConversionException {
      return aBoolean.toString();
    }
    @Override
    public Class<Boolean> getExternalType() {
      return Boolean.class;
    }
  };

  public static ValueType.BOOLEAN<Boolean> PRIMITIVE_BOOLEAN = new ValueType.BOOLEAN<Boolean>() {
    @Override
    public Boolean getInternal(Boolean external) {
      return external;
    }
    @Override
    public Boolean getExternal(Boolean internal) {
      return internal;
    }
    @Override
    public Boolean fromString(String s) {
      return Boolean.parseBoolean(s);
    }
    @Override
    public String toString(Boolean aBoolean) throws TypeConversionException {
      return aBoolean.toString();
    }
    @Override
    public Class<Boolean> getExternalType() {
      return Boolean.class;
    }
  };

  public static ValueType.BINARY<InputStream> BINARY = new ValueType.BINARY<java.io.InputStream>() {
    @Override
    public InputStream getInternal(InputStream inputStream) {
      return inputStream;
    }
    @Override
    public InputStream getExternal(InputStream inputStream) {
      return inputStream;
    }
    @Override
    public InputStream fromString(String s) {
      throw new UnsupportedOperationException();
    }
    @Override
    public String toString(InputStream inputStream) throws TypeConversionException {
      throw new UnsupportedOperationException();
    }
    @Override
    public Class<InputStream> getExternalType() {
      return InputStream.class;
    }
  };

  public static ValueType.DATE<Date> DATE = new ValueType.DATE<Date>() {
    @Override
    public Calendar getInternal(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      return calendar;
    }
    @Override
    public Date getExternal(Calendar calendar) {
      return calendar.getTime();
    }
    @Override
    public Date fromString(String s) throws TypeConversionException {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.parse(s);
      }
      catch (ParseException e) {
        throw new TypeConversionException(e);
      }
    }
    @Override
    public String toString(Date date) throws TypeConversionException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      return sdf.format(date);
    }
    @Override
    public Class<Date> getExternalType() {
      return Date.class;
    }
  };
}
