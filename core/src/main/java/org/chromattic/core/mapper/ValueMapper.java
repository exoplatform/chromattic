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

import org.chromattic.core.bean.SimpleType;

import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import java.util.Date;
import java.util.Calendar;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueMapper {

  /** . */
  public static final ValueMapper instance = new ValueMapper();

  private ValueMapper() {
  }

  public final <T> T get(Value value, SimpleType<T> wantedType) throws RepositoryException {
    int propertyType = value.getType();
    if (wantedType != null) {
      if (wantedType == SimpleType.BINARY) {
        if (propertyType == PropertyType.BINARY) {
          return (T)value.getStream();
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.STRING) {
        if (propertyType == PropertyType.STRING || propertyType == PropertyType.NAME || propertyType == PropertyType.PATH) {
          return (T)value.getString();
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.PATH) {
        if (propertyType == PropertyType.PATH) {
          return (T)value.getString();
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.INT) {
        if (propertyType == PropertyType.LONG) {
          return (T)Integer.valueOf((int)value.getLong());
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.LONG) {
        if (propertyType == PropertyType.LONG) {
          return (T)Long.valueOf(value.getLong());
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.FLOAT) {
        if (propertyType == PropertyType.DOUBLE) {
          return (T)Float.valueOf((float)value.getDouble());
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.DOUBLE) {
        if (propertyType == PropertyType.DOUBLE) {
          return (T)Double.valueOf(value.getDouble());
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.BOOLEAN) {
        if (propertyType == PropertyType.BOOLEAN) {
          return (T)Boolean.valueOf(value.getBoolean());
        } else {
          throw new ClassCastException();
        }
      } else if (wantedType == SimpleType.DATE) {
        if (propertyType == PropertyType.DATE) {
          return (T)value.getDate().getTime();
        } else {
          throw new ClassCastException();
        }
      } else {
        throw new AssertionError("Property type " + propertyType + " not handled");
      }
    } else {
      switch (propertyType) {
        case PropertyType.BOOLEAN:
          return (T)Boolean.valueOf(value.getBoolean());
        case PropertyType.LONG:
          return (T)Integer.valueOf((int)value.getLong());
        case PropertyType.DOUBLE:
          return (T)Double.valueOf(value.getDouble());
        case PropertyType.NAME:
        case PropertyType.PATH:
        case PropertyType.STRING:
          return (T)value.getString();
        case PropertyType.BINARY:
          return (T)value.getStream();
        case PropertyType.DATE:
          return (T)value.getDate().getTime();
        default:
          throw new AssertionError("Property type " + propertyType + " not handled");
      }
    }
  }

  public final <T> Value get(ValueFactory valueFactory, T o, SimpleType<T> type) throws ValueFormatException {
    if (type == null) {
      if (o instanceof String) {
        type = (SimpleType<T>)SimpleType.STRING;
      } else if (o instanceof Integer) {
        type = (SimpleType<T>)SimpleType.INT;
      } else if (o instanceof Long) {
        type = (SimpleType<T>)SimpleType.LONG;
      } else if (o instanceof Date) {
        type = (SimpleType<T>)SimpleType.DATE;
      } else if (o instanceof Double) {
        type = (SimpleType<T>)SimpleType.DOUBLE;
      }  else if (o instanceof Float) {
        type = (SimpleType<T>)SimpleType.FLOAT;
      }  else if (o instanceof InputStream) {
        type = (SimpleType<T>)SimpleType.BINARY;
      } else if (o instanceof Boolean) {
        type = (SimpleType<T>)SimpleType.BOOLEAN;
      } else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }
    }

    //
    if (type == SimpleType.STRING) {
      return valueFactory.createValue((String)o);
    } else if (type == SimpleType.PATH) {
      return valueFactory.createValue((String)o, PropertyType.PATH);
    } else if (type == SimpleType.LONG) {
      return valueFactory.createValue((Long)o);
    } else if (type == SimpleType.INT) {
      return valueFactory.createValue((Integer)o);
    } else if (type == SimpleType.DATE) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime((Date)o);
      return valueFactory.createValue(calendar);
    } else if (type == SimpleType.BINARY) {
      return valueFactory.createValue((InputStream)o);
    } else if (type == SimpleType.DOUBLE) {
      return valueFactory.createValue((Double)o);
    } else if (type == SimpleType.FLOAT) {
      return valueFactory.createValue((Float)o);
    } else if (type == SimpleType.BOOLEAN) {
      return valueFactory.createValue((Boolean)o);
    } else {
      throw new UnsupportedOperationException("Simple type " + type + " not accepted");
    }
  }
}
