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

package org.chromattic.metatype;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.RepositoryException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * A data type.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @param <T> the java type expressing the underlying type
 */
public abstract class DataType<T> {

  /** . */
  public static final DataType<String> STRING = new DataType<String>(String.class, PropertyType.STRING) {
    @Override
    public Value getValue(ValueFactory factory, String s) throws ValueFormatException {
      return factory.createValue(s, PropertyType.STRING);
    }
    @Override
    public String getValue(Value value) throws RepositoryException {
      return value.getString();
    }
  };

  /** . */
  public static final DataType<String> PATH = new DataType<String>(String.class, PropertyType.PATH) {
    @Override
    public Value getValue(ValueFactory factory, String s) throws ValueFormatException {
      return factory.createValue(s, PropertyType.PATH);
    }
    @Override
    public String getValue(Value value) throws RepositoryException {
      return value.getString();
    }
  };

  /** . */
  public static final DataType<String> NAME = new DataType<String>(String.class, PropertyType.NAME) {
    @Override
    public Value getValue(ValueFactory factory, String s) throws ValueFormatException {
      return factory.createValue(s, PropertyType.NAME);
    }
    @Override
    public String getValue(Value value) throws RepositoryException {
      return value.getString();
    }
  };

  /** . */
  public static final DataType<Long> LONG = new DataType<Long>(Long.class, PropertyType.LONG) {
    @Override
    public Value getValue(ValueFactory factory, Long aLong) throws ValueFormatException {
      return factory.createValue(aLong);
    }
    @Override
    public Long getValue(Value value) throws RepositoryException {
      return value.getLong();
    }
  };

  /** . */
  public static final DataType<Double> DOUBLE = new DataType<Double>(Double.class, PropertyType.DOUBLE) {
    @Override
    public Value getValue(ValueFactory factory, Double aDouble) throws ValueFormatException {
      return factory.createValue(aDouble);
    }
    @Override
    public Double getValue(Value value) throws RepositoryException {
      return value.getDouble();
    }
  };

  /** . */
  public static final DataType<Boolean> BOOLEAN = new DataType<Boolean>(Boolean.class, PropertyType.BOOLEAN) {
    @Override
    public Value getValue(ValueFactory factory, Boolean aBoolean) throws ValueFormatException {
      return factory.createValue(aBoolean);
    }
    @Override
    public Boolean getValue(Value value) throws RepositoryException {
      return value.getBoolean();
    }
  };

  /** . */
  public static final DataType<InputStream> BINARY = new DataType<InputStream>(InputStream.class, PropertyType.BINARY) {
    @Override
    public Value getValue(ValueFactory factory, InputStream inputStream) throws ValueFormatException {
      return factory.createValue(inputStream);
    }
    @Override
    public InputStream getValue(Value value) throws RepositoryException {
      return value.getStream();
    }
  };

  /** . */
  public static final DataType<Calendar> DATE = new DataType<Calendar>(Calendar.class, PropertyType.DATE) {
    @Override
    public Value getValue(ValueFactory factory, Calendar date) throws ValueFormatException {
      return factory.createValue(date);
    }
    @Override
    public Calendar getValue(Value value) throws RepositoryException {
      return value.getDate();
    }
  };

  /** . */
  public static final DataType<Object> ANY = new DataType<Object>(Object.class, PropertyType.UNDEFINED) {
    @Override
    public Value getValue(ValueFactory factory, Object date) throws ValueFormatException {
      throw new UnsupportedOperationException();
    }
    @Override
    public Object getValue(Value value) throws RepositoryException {
      throw new UnsupportedOperationException();
    }
  };

  /** . */
  private static final DataType<?>[] ALL = {
    STRING,
    PATH,
    NAME,
    LONG,
    DOUBLE,
    BOOLEAN,
    BINARY,
    DATE
  };

  public static DataType<?> get(int code) {
    for (DataType<?> pt : ALL) {
      if (pt.code == code) {
        return pt;
      }
    }
    return null;
  }

  /** The java type associated with the type. */
  private final Class<T> javaType;

  /**
   * The JCR type code among the values:
   * <ul>
   *   <li>{@link PropertyType#STRING}</li>
   *   <li>{@link PropertyType#BINARY}</li>
   *   <li>{@link PropertyType#LONG}</li>
   *   <li>{@link PropertyType#DOUBLE}</li>
   *   <li>{@link PropertyType#DATE}</li>
   *   <li>{@link PropertyType#BOOLEAN}</li>
   *   <li>{@link PropertyType#NAME}</li>
   *   <li>{@link PropertyType#PATH}</li>
   *   <li>{@link PropertyType#REFERENCE}</li>
   * </ul>
   */
  private final int code;

  private DataType(Class<T> javaType, int code) {
    this.javaType = javaType;
    this.code = code;
  }

  /**
   * Converts the Java value to the {@link Value}.
   *
   * @param factory the JCR value factory required to create the value
   * @param v the Java value
   * @return the JCR value
   * @throws ValueFormatException thrown by the factory
   */
  public abstract Value getValue(ValueFactory factory, T v) throws ValueFormatException;

  /**
   * Converts the {@link Value} to the java value.
   *
   * @param value the JCR value
   * @return the Java value
   * @throws RepositoryException thrown by the conversion
   */
  public abstract T getValue(Value value) throws RepositoryException;

  /**
   * Returns the Java value type modelling the property type.
   *
   * @return the Java value type modelling the property type
   */
  public Class<T> getJavaType() {
    return javaType;
  }

  /**
   * Returns the JCR property type as defined by {@link PropertyType}
   *
   * @return the JCR property type
   */
  public int getCode() {
    return code;
  }
}
