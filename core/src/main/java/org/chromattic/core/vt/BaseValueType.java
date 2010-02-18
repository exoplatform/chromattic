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

package org.chromattic.core.vt;

import org.chromattic.core.bean.SimpleType;

import javax.jcr.*;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class BaseValueType<E, I> extends ValueType<E> {

  /** . */
  private final List<E> defaultValues;

  /** . */
  private final SimpleType<E> type;

  protected BaseValueType(List<E> defaultValues, SimpleType<E> type) {
    this.defaultValues = defaultValues;
    this.type = type;
  }

  @Override
  public List<E> getDefaultValue() {
    return defaultValues;
  }

  @Override
  public boolean isPrimitive() {
    return type.isPrimitive();
  }

  public abstract E toExternal(I internal);

  public abstract I toInternal(E external);

  @Override
  public Class<E> getObjectType() {
    return type.getObjectType();
  }

  @Override
  public Class<?> getRealType() {
    return type.getRealType();
  }

  public abstract static class STRING<V> extends BaseValueType<V, String> {

    public static class TO_STRING extends STRING<String> {
      public TO_STRING(List<String> defaultValues, SimpleType<String> type) {
        super(defaultValues, type);
      }
      @Override
      public String toExternal(String internal) {
        return internal;
      }
      @Override
      public String toInternal(String external) {
        return external;
      }
    }

    public STRING(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.STRING || propertyType == PropertyType.NAME || propertyType == PropertyType.PATH) {
        return toExternal(value.getString());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      String s = toInternal(o);
      return valueFactory.createValue(s);
    }
  }
  
  public abstract static class STREAM<V> extends BaseValueType<V, InputStream> {

    public static class TO_STREAM extends STREAM<InputStream> {
      public TO_STREAM(List<InputStream> defaultValues, SimpleType<InputStream> type) {
        super(defaultValues, type);
      }
      @Override
      public InputStream toExternal(InputStream internal) {
        return internal;
      }
      @Override
      public InputStream toInternal(InputStream external) {
        return external;
      }
    }

    public STREAM(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.BINARY) {
        return toExternal(value.getStream());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      InputStream in = toInternal(o);
      return valueFactory.createValue(in);
    }
  }

  public abstract static class PATH<V> extends BaseValueType<V, String> {

    public static class TO_STRING extends PATH<String> {
      public TO_STRING(List<String> defaultValues, SimpleType<String> type) {
        super(defaultValues, type);
      }
      @Override
      public String toExternal(String internal) {
        return internal;
      }
      @Override
      public String toInternal(String external) {
        return external;
      }
    }

    public PATH(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.PATH) {
        return toExternal(value.getString());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      String s = toInternal(o);
      return valueFactory.createValue(s, PropertyType.PATH);
    }
  }

  public abstract static class LONG<V> extends BaseValueType<V, Long> {

    public static class TO_INT extends LONG<Integer> {
      public TO_INT(List<Integer> defaultValues, SimpleType<Integer> type) {
        super(defaultValues, type);
      }
      @Override
      public Integer toExternal(Long internal) {
        return internal.intValue();
      }
      @Override
      public Long toInternal(Integer external) {
        return external.longValue();
      }
    }

    public static class TO_LONG extends LONG<Long> {
      public TO_LONG(List<Long> defaultValues, SimpleType<Long> type) {
        super(defaultValues, type);
      }
      @Override
      public Long toExternal(Long internal) {
        return internal;
      }
      @Override
      public Long toInternal(Long external) {
        return external;
      }
    }

    public LONG(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.LONG) {
        return toExternal(value.getLong());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      Long l = toInternal(o);
      return valueFactory.createValue(l);
    }
  }

  public abstract static class DOUBLE<V> extends BaseValueType<V, Double> {

    public static class TO_DOUBLE extends DOUBLE<Double> {
      public TO_DOUBLE(List<Double> defaultValues, SimpleType<Double> type) {
        super(defaultValues, type);
      }
      @Override
      public Double toExternal(Double internal) {
        return internal;
      }
      @Override
      public Double toInternal(Double external) {
        return external;
      }
    }

    public static class TO_FLOAT extends DOUBLE<Float> {
      public TO_FLOAT(List<Float> defaultValues, SimpleType<Float> type) {
        super(defaultValues, type);
      }
      @Override
      public Float toExternal(Double internal) {
        return internal.floatValue();
      }
      @Override
      public Double toInternal(Float external) {
        return external.doubleValue();
      }
    }

    public DOUBLE(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.DOUBLE) {
        return toExternal(value.getDouble());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      Double d = toInternal(o);
      return valueFactory.createValue(d);
    }
  }

  public abstract static class BOOLEAN<V> extends BaseValueType<V, Boolean> {

    public static class TO_BOOLEAN extends BOOLEAN<Boolean> {
      public TO_BOOLEAN(List<Boolean> defaultValues, SimpleType<Boolean> type) {
        super(defaultValues, type);
      }
      @Override
      public Boolean toExternal(Boolean internal) {
        return internal;
      }
      @Override
      public Boolean toInternal(Boolean external) {
        return external;
      }
    }

    public BOOLEAN(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.BOOLEAN) {
        return toExternal(value.getBoolean());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      Boolean b = toInternal(o);
      return valueFactory.createValue(b);
    }
  }

  public abstract static class DATE<V> extends BaseValueType<V, Date> {

    public static class TO_DATE extends DATE<Date> {
      public TO_DATE(List<Date> defaultValues, SimpleType<Date> type) {
        super(defaultValues, type);
      }
      @Override
      public Date toExternal(Date internal) {
        return internal;
      }
      @Override
      public Date toInternal(Date external) {
        return external;
      }
    }

    public DATE(List<V> defaultValues, SimpleType<V> type) {
      super(defaultValues, type);
    }

    @Override
    public V get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      if (propertyType == PropertyType.DATE) {
        return toExternal(value.getDate().getTime());
      } else {
        throw new ClassCastException();
      }
    }

    @Override
    public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
      Date time = toInternal(o);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(time);
      return valueFactory.createValue(calendar);
    }
  }
}
