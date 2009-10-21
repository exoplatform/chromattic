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

import org.chromattic.core.bean.ObjectSimpleType;
import org.chromattic.core.bean.PrimitiveSimpleType;
import org.chromattic.core.bean.SimpleType;
import org.chromattic.core.bean.SimpleTypeKind;

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
      switch (wantedType.getKind()) {
        case BINARY:
          if (propertyType == PropertyType.BINARY) {
            return (T)value.getStream();
          } else {
            throw new ClassCastException();
          }
        case STRING:
          if (propertyType == PropertyType.STRING || propertyType == PropertyType.NAME || propertyType == PropertyType.PATH) {
            return (T)value.getString();
          } else {
            throw new ClassCastException();
          }
        case PATH:
          if (propertyType == PropertyType.PATH) {
            return (T)value.getString();
          } else {
            throw new ClassCastException();
          }
        case INT:
          if (propertyType == PropertyType.LONG) {
            return (T)Integer.valueOf((int)value.getLong());
          } else {
            throw new ClassCastException();
          }
        case LONG:
          if (propertyType == PropertyType.LONG) {
            return (T)Long.valueOf(value.getLong());
          } else {
            throw new ClassCastException();
          }
        case FLOAT:
          if (propertyType == PropertyType.DOUBLE) {
            return (T)Float.valueOf((float)value.getDouble());
          } else {
            throw new ClassCastException();
          }
        case DOUBLE:
          if (propertyType == PropertyType.DOUBLE) {
            return (T)Double.valueOf(value.getDouble());
          } else {
            throw new ClassCastException();
          }
        case BOOLEAN:
          if (propertyType == PropertyType.BOOLEAN) {
            return (T)Boolean.valueOf(value.getBoolean());
          } else {
            throw new ClassCastException();
          }
        case DATE:
          if (propertyType == PropertyType.DATE) {
            return (T)value.getDate().getTime();
          } else {
            throw new ClassCastException();
          }
        default:
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
    SimpleTypeKind typeKind;
    if (type == null) {
      if (o instanceof String) {
        typeKind = SimpleTypeKind.STRING;
      } else if (o instanceof Integer) {
        typeKind = SimpleTypeKind.INT;
      } else if (o instanceof Long) {
        typeKind = SimpleTypeKind.LONG;
      } else if (o instanceof Date) {
        typeKind = SimpleTypeKind.DATE;
      } else if (o instanceof Double) {
        typeKind = SimpleTypeKind.DOUBLE;
      }  else if (o instanceof Float) {
        typeKind = SimpleTypeKind.FLOAT;
      }  else if (o instanceof InputStream) {
        typeKind = SimpleTypeKind.BINARY;
      } else if (o instanceof Boolean) {
        typeKind = SimpleTypeKind.BOOLEAN;
      } else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }
    } else {
      typeKind = type.getKind();
    }

    //
    switch (typeKind) {
      case STRING:
        return valueFactory.createValue((String)o);
      case PATH:
        return valueFactory.createValue((String)o, PropertyType.PATH);
      case LONG:
        return valueFactory.createValue((Long)o);
      case INT:
        return valueFactory.createValue((Integer)o);
      case DATE:
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date)o);
        return valueFactory.createValue(calendar);
      case BINARY:
        return valueFactory.createValue((InputStream)o);
      case DOUBLE:
        return valueFactory.createValue((Double)o);
      case FLOAT:
        return valueFactory.createValue((Float)o);
      case BOOLEAN:
        return valueFactory.createValue((Boolean)o);
      default:
        throw new UnsupportedOperationException("Simple type " + type + " not accepted");
    }
  }
}
