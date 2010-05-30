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

package org.chromattic.metamodel.mapping.jcr;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.RepositoryException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @param <T> the java type modeling the JCR type
 */
public abstract class JCRPropertyType<T> {

  /** . */
  public static final JCRPropertyType<String> STRING = new JCRPropertyType<String>(String.class, PropertyType.STRING) {
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
  public static final JCRPropertyType<String> PATH = new JCRPropertyType<String>(String.class, PropertyType.PATH) {
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
  public static final JCRPropertyType<String> NAME = new JCRPropertyType<String>(String.class, PropertyType.NAME) {
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
  public static final JCRPropertyType<Long> LONG = new JCRPropertyType<Long>(Long.class, PropertyType.LONG) {
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
  public static final JCRPropertyType<Double> DOUBLE = new JCRPropertyType<Double>(Double.class, PropertyType.DOUBLE) {
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
  public static final JCRPropertyType<Boolean> BOOLEAN = new JCRPropertyType<Boolean>(Boolean.class, PropertyType.BOOLEAN) {
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
  public static final JCRPropertyType<InputStream> BINARY = new JCRPropertyType<InputStream>(InputStream.class, PropertyType.BINARY) {
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
  public static final JCRPropertyType<Calendar> DATE = new JCRPropertyType<Calendar>(Calendar.class, PropertyType.DATE) {
    @Override
    public Value getValue(ValueFactory factory, Calendar date) throws ValueFormatException {
      return factory.createValue(date);
    }
    @Override
    public Calendar getValue(Value value) throws RepositoryException {
      return value.getDate();
    }
  };

  private static final JCRPropertyType<?>[] ALL = {
    STRING,
    PATH,
    NAME,
    LONG,
    DOUBLE,
    BOOLEAN,
    BINARY,
    DATE
  };

  public static JCRPropertyType<?> get(int code) {
    for (JCRPropertyType<?> pt : ALL) {
      if (pt.code == code) {
        return pt;
      }
    }
    return null;
  }

  /** . */
  private final Class<T> javaType;

  /** . */
  private final int code;

  private JCRPropertyType(Class<T> javaType, int code) {
    this.javaType = javaType;
    this.code = code;
  }

  public abstract Value getValue(ValueFactory factory, T t) throws ValueFormatException;

  public abstract T getValue(Value value) throws RepositoryException;

  public Class<T> getJavaType() {
    return javaType;
  }

  public int getCode() {
    return code;
  }
}
