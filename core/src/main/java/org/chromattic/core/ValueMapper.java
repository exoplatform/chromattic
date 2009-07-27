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
package org.chromattic.core;

import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import javax.jcr.ValueFormatException;
import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.Calendar;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class ValueMapper<T> {

  /** . */
  private final Class<T> type;

  public ValueMapper(Class<T> type) {
    this.type = type;
  }

  public Class<T> getType() {
    return type;
  }

  public final T get(Value value) throws RepositoryException {
    if (value == null) {
      throw new NullPointerException("Was not expecting null value");
    }
    return convert(value);
  }

  public final Value get(ValueFactory valueFactory, T t) throws UndeclaredRepositoryException {
    if (valueFactory == null) {
      throw new NullPointerException();
    }
    if (t == null) {
      throw new NullPointerException();
    }
    try {
      return convert(valueFactory, t);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  protected abstract T convert(Value value) throws RepositoryException;

  protected abstract Value convert(ValueFactory valueFactory, T t) throws RepositoryException;

  public static ValueMapper<String> STRING = new ValueMapper<String>(String.class) {
    @Override
    protected String convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.STRING) {
        try {
          return value.getString();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to string");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, String s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Integer> INTEGER = new ValueMapper<Integer>(Integer.class) {
    @Override
    protected Integer convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.LONG) {
        try {
          return (int)value.getLong();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to int");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Integer s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Long> LONG = new ValueMapper<Long>(Long.class) {
    @Override
    protected Long convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.LONG) {
        try {
          return (long)value.getLong();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to long");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Long s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Double> DOUBLE = new ValueMapper<Double>(Double.class) {
    @Override
    protected Double convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.DOUBLE) {
        try {
          return (double)value.getLong();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to double");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Double s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Float> FLOAT = new ValueMapper<Float>(Float.class) {
    @Override
    protected Float convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.DOUBLE) {
        try {
          return (float)value.getLong();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to float");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Float s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<InputStream> BINARY = new ValueMapper<InputStream>(InputStream.class) {
    @Override
    protected InputStream convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.BINARY) {
        try {
          return value.getStream();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to input stream");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, InputStream s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Boolean> BOOLEAN = new ValueMapper<Boolean>(Boolean.class) {
    @Override
    protected Boolean convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.BOOLEAN) {
        try {
          return value.getBoolean();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to boolean");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Boolean s) {
      return valueFactory.createValue(s);
    }
  };

  public static ValueMapper<Date> DATE = new ValueMapper<Date>(Date.class) {
    @Override
    protected Date convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.DATE) {
        try {
          return value.getDate().getTime();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to date");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Date s) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(s);
      return valueFactory.createValue(calendar);
    }
  };
}
