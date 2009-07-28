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
package org.chromattic.core.mapper;

import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.bean.SimpleValueInfo;

import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import javax.jcr.ValueFormatException;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.Date;
import java.util.Calendar;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueMapper<T> {

  public static ValueMapper<?> getValueMapper(Object o) {
    if (o instanceof String) {
      return ValueMapper.STRING;
    } else if (o instanceof Integer) {
      return ValueMapper.INTEGER;
    } else if (o instanceof Long) {
      return ValueMapper.LONG;
    } else if (o instanceof Date) {
      return ValueMapper.DATE;
    } else if (o instanceof Double) {
      return ValueMapper.DOUBLE;
    }  else if (o instanceof Float) {
      return ValueMapper.FLOAT;
    }  else if (o instanceof InputStream) {
      return ValueMapper.BINARY;
    } else if (o instanceof Boolean) {
      return ValueMapper.BOOLEAN;
    } else {
      throw new AssertionError();
    }
  }

  public static ValueMapper<?> getValueMapper(SimpleValueInfo type) {
    switch (type.getSimpleType()) {
      case STRING:
        return ValueMapper.STRING;
      case INT:
        return ValueMapper.INTEGER;
      case LONG:
        return ValueMapper.LONG;
      case BOOLEAN:
        return ValueMapper.BOOLEAN;
      case FLOAT:
        return ValueMapper.FLOAT;
      case DOUBLE:
        return ValueMapper.DOUBLE;
      case DATE:
        return ValueMapper.DATE;
      case BINARY:
        return ValueMapper.BINARY;
      default:
        throw new UnsupportedOperationException();
    }
  }
  
  public static ValueMapper<?> getValueMapper(Value value) {
    return getValueMapper(value.getType());
  }

  public static ValueMapper<?> getValueMapper(PropertyDefinition def) {
    int propertyType = def.getRequiredType();
    return getValueMapper(propertyType);
  }

  public static ValueMapper<?> getValueMapper(int propertyType) {
    switch (propertyType) {
      case PropertyType.BINARY:
        return ValueMapper.BINARY;
      case PropertyType.BOOLEAN:
        return ValueMapper.BOOLEAN;
      case PropertyType.DATE:
        return ValueMapper.DATE;
      case PropertyType.LONG:
        return ValueMapper.INTEGER;
      case PropertyType.DOUBLE:
        return ValueMapper.DOUBLE;
      case PropertyType.STRING:
      case PropertyType.NAME:
      case PropertyType.PATH:
        return ValueMapper.STRING;
      case PropertyType.REFERENCE:
        throw new UnsupportedOperationException("Reference type is not supported via a map");
      case PropertyType.UNDEFINED:
        throw new UnsupportedOperationException("Undefined type is not supported via a map");
      default:
        throw new UnsupportedOperationException();
    }
  }

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

  public final Value get(ValueFactory valueFactory, Object o) throws UndeclaredRepositoryException {
    if (valueFactory == null) {
      throw new NullPointerException();
    }
    if (o == null) {
      throw new NullPointerException();
    }
    try {
      return convert(valueFactory, o);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  protected abstract T convert(Value value) throws RepositoryException;

  protected abstract Value convert(ValueFactory valueFactory, Object o) throws RepositoryException;

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
    protected Value convert(ValueFactory valueFactory, Object o) {
      return valueFactory.createValue((String)o);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      Number n = (Number)o;
      long l = n.longValue();
      return valueFactory.createValue(l);
    }
  };

  public static ValueMapper<Long> LONG = new ValueMapper<Long>(Long.class) {
    @Override
    protected Long convert(Value value) throws RepositoryException {
      int type = value.getType();
      if (type == PropertyType.LONG) {
        try {
          return value.getLong();
        }
        catch (ValueFormatException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        throw new IllegalArgumentException("Could not convert " + type + " to long");
      }
    }

    @Override
    protected Value convert(ValueFactory valueFactory, Object o) {
      Number n = (Number)o;
      long l = n.longValue();
      return valueFactory.createValue(l);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      Number n = (Number)o;
      double d = n.doubleValue();
      return valueFactory.createValue(d);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      Number n = (Number)o;
      double d = n.doubleValue();
      return valueFactory.createValue(d);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      return valueFactory.createValue((InputStream)o);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      return valueFactory.createValue((Boolean)o);
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
    protected Value convert(ValueFactory valueFactory, Object o) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime((Date)o);
      return valueFactory.createValue(calendar);
    }
  };
}
