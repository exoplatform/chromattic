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

package org.chromattic.metamodel.type;

import org.chromattic.api.TypeConversionException;
import org.chromattic.spi.type.SimpleTypeProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleTypeProviders {

  private SimpleTypeProviders() {
  }

  public final static class STRING extends SimpleTypeProvider.STRING<String> {
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
  }

  public final static class PATH extends SimpleTypeProvider.PATH<String> {
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
  }

  public static final class NAME extends SimpleTypeProvider.NAME<String> {
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
  }

  public static final class INTEGER extends SimpleTypeProvider.LONG<Integer> {
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
  }

  public static final class PRIMITIVE_INTEGER extends SimpleTypeProvider.LONG<Integer> {
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
  }

  public static final class LONG extends SimpleTypeProvider.LONG<Long> {
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
  }

  public static final class PRIMITIVE_LONG extends SimpleTypeProvider.LONG<Long> {
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
  }

  public static final class DOUBLE extends SimpleTypeProvider.DOUBLE<Double> {
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
  }

  public static final class PRIMITIVE_DOUBLE extends SimpleTypeProvider.DOUBLE<Double> {
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
  }

  public static final class FLOAT extends SimpleTypeProvider.DOUBLE<Float> {
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
  }

  public static final class PRIMITIVE_FLOAT extends SimpleTypeProvider.DOUBLE<Float> {
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
  }

  public static final class BOOLEAN extends SimpleTypeProvider.BOOLEAN<Boolean> {
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
  }

  public static final class PRIMITIVE_BOOLEAN extends SimpleTypeProvider.BOOLEAN<Boolean> {
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
  }

  public static final class BINARY extends SimpleTypeProvider.BINARY<java.io.InputStream> {
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
  }

  public static final class BYTE_ARRAY extends SimpleTypeProvider.BINARY<byte[]> {
    @Override
    public Class<byte[]> getExternalType() {
      return byte[].class;
    }
    @Override
    public InputStream getInternal(byte[] bytes) throws TypeConversionException {
      return new ByteArrayInputStream(bytes);
    }
    @Override
    public byte[] getExternal(InputStream inputStream) throws TypeConversionException {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        for (int l = inputStream.read(buffer);l != -1;l = inputStream.read(buffer)) {
          baos.write(buffer, 0, l);
        }
        return baos.toByteArray();
      }
      catch (IOException e) {
        throw new TypeConversionException(e);
      }
    }
    @Override
    public byte[] fromString(String s) throws TypeConversionException {
      throw new UnsupportedOperationException();
    }
    @Override
    public String toString(byte[] bytes) throws TypeConversionException {
      throw new UnsupportedOperationException();
    }
  }

  public static final class DATE extends SimpleTypeProvider.DATE<Date> {
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
  }

  public static final class CALENDAR extends SimpleTypeProvider.DATE<Calendar> {
    @Override
    public Class<Calendar> getExternalType() {
      return Calendar.class;  
    }
    @Override
    public Calendar getInternal(Calendar calendar) throws TypeConversionException {
      return calendar;  
    }
    @Override
    public Calendar getExternal(Calendar calendar) throws TypeConversionException {
      return calendar;  
    }
    @Override
    public Calendar fromString(String s) throws TypeConversionException {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(s));
        return c;
      }
      catch (ParseException e) {
        throw new TypeConversionException(e);
      }
    }
    @Override
    public String toString(Calendar calendar) throws TypeConversionException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      return sdf.format(calendar.getTime());  
    }
  }

  public static final class TIMESTAMP extends SimpleTypeProvider.DATE<Long> {
    @Override
    public Class<Long> getExternalType() {
      return Long.class;
    }
    @Override
    public Calendar getInternal(Long l) throws TypeConversionException {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(l);
      return c;
    }
    @Override
    public Long getExternal(Calendar calendar) throws TypeConversionException {
      return calendar.getTimeInMillis();
    }
    @Override
    public Long fromString(String s) throws TypeConversionException {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(s));
        return c.getTimeInMillis();
      }
      catch (ParseException e) {
        throw new TypeConversionException(e);
      }
    }
    @Override
    public String toString(Long l) throws TypeConversionException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(l);
      return sdf.format(c);  
    }
  }
}
