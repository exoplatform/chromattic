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

import org.chromattic.core.bean.BaseSimpleTypes;
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

  public final <E> E get(Value value, SimpleType<E> wantedType) throws RepositoryException {
    int propertyType = value.getType();
    if (wantedType != null) {
      SimpleTypeKind<E, ?> typeKind = wantedType.getKind();
      if (SimpleTypeKind.STREAM.class.isInstance(typeKind)) {
        if (propertyType == PropertyType.BINARY) {
          return (E)value.getStream();
        }
        else {
          throw new ClassCastException();
        }
      }
      else if (SimpleTypeKind.STRING.class.isInstance(typeKind)) {
        if (propertyType == PropertyType.STRING || propertyType == PropertyType.NAME || propertyType == PropertyType.PATH) {
          return (E)value.getString();
        }
        else {
          throw new ClassCastException();
        }
      }
      else if (SimpleTypeKind.PATH.class.isInstance(typeKind)) {
        if (propertyType == PropertyType.PATH) {
          return (E)value.getString();
        }
        else {
          throw new ClassCastException();
        }
      }
      else if (SimpleTypeKind.LONG.class.isInstance(typeKind)) {
        if (BaseSimpleTypes.INT == typeKind) {
          if (propertyType == PropertyType.LONG) {
            return (E)Integer.valueOf((int)value.getLong());
          }
          else {
            throw new ClassCastException();
          }
        }
        else if (BaseSimpleTypes.LONG == typeKind) {
          if (propertyType == PropertyType.LONG) {
            return (E)Long.valueOf(value.getLong());
          }
          else {
            throw new ClassCastException();
          }
        }
        else {
          throw new AssertionError("Property type " + propertyType + " not handled");
        }
      }
      else if (SimpleTypeKind.DOUBLE.class.isInstance(typeKind)) {
        if (BaseSimpleTypes.FLOAT == typeKind) {
          if (propertyType == PropertyType.DOUBLE) {
            return (E)Float.valueOf((float)value.getDouble());
          }
          else {
            throw new ClassCastException();
          }
        }
        else if (BaseSimpleTypes.DOUBLE == typeKind) {
          if (propertyType == PropertyType.DOUBLE) {
            return (E)Double.valueOf(value.getDouble());
          }
          else {
            throw new ClassCastException();
          }
        } else {
          throw new AssertionError("Property type " + propertyType + " not handled");
        }
      }
      else if (SimpleTypeKind.BOOLEAN.class.isInstance(typeKind)) {
        if (propertyType == PropertyType.BOOLEAN) {
          return (E)Boolean.valueOf(value.getBoolean());
        }
        else {
          throw new ClassCastException();
        }
      }
      else if (SimpleTypeKind.DATE.class.isInstance(typeKind)) {
        if (propertyType == PropertyType.DATE) {
          return (E)value.getDate().getTime();
        }
        else {
          throw new ClassCastException();
        }
      }
      else {
        throw new AssertionError("Property type " + propertyType + " not handled");
      }
    }
    else {
      switch (propertyType) {
        case PropertyType.BOOLEAN:
          return (E)Boolean.valueOf(value.getBoolean());
        case PropertyType.LONG:
          return (E)Integer.valueOf((int)value.getLong());
        case PropertyType.DOUBLE:
          return (E)Double.valueOf(value.getDouble());
        case PropertyType.NAME:
        case PropertyType.PATH:
        case PropertyType.STRING:
          return (E)value.getString();
        case PropertyType.BINARY:
          return (E)value.getStream();
        case PropertyType.DATE:
          return (E)value.getDate().getTime();
        default:
          throw new AssertionError("Property type " + propertyType + " not handled");
      }
    }
  }

  public final <E> Value get(ValueFactory valueFactory, E o, SimpleType<E> type) throws ValueFormatException {
    SimpleTypeKind<E, ?> typeKind;
    if (type == null) {
      if (o instanceof String) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.STRING;
      }
      else if (o instanceof Integer) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.INT;
      }
      else if (o instanceof Long) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.LONG;
      }
      else if (o instanceof Date) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.DATE;
      }
      else if (o instanceof Double) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.DOUBLE;
      }
      else if (o instanceof Float) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.FLOAT;
      }
      else if (o instanceof InputStream) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.STREAM;
      }
      else if (o instanceof Boolean) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.BOOLEAN;
      }
      else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }
    }
    else {
      typeKind = type.getKind();
    }

    //
    if (SimpleTypeKind.STRING.class.isInstance(typeKind)) {
      return valueFactory.createValue((String)o);
    }
    else if (SimpleTypeKind.PATH.class.isInstance(typeKind)) {
      return valueFactory.createValue((String)o, PropertyType.PATH);
    } else if (SimpleTypeKind.LONG.class.isInstance(typeKind)) {
      if (typeKind == BaseSimpleTypes.LONG) {
        return valueFactory.createValue((Long)o);
      }
      else if (typeKind == BaseSimpleTypes.INT) {
        return valueFactory.createValue((Integer)o);
      } else {
        throw new UnsupportedOperationException("Simple type " + type + " not accepted");
      }
    } else if (SimpleTypeKind.DATE.class.isInstance(typeKind)) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime((Date)o);
      return valueFactory.createValue(calendar);
    }
    else if (SimpleTypeKind.STREAM.class.isInstance(typeKind)) {
      return valueFactory.createValue((InputStream)o);
    }
    else if (SimpleTypeKind.DOUBLE.class.isInstance(typeKind)) {
      if (typeKind == BaseSimpleTypes.DOUBLE) {
        return valueFactory.createValue((Double)o);
      }
      else if (typeKind == BaseSimpleTypes.FLOAT) {
        return valueFactory.createValue((Float)o);
      } else {
        throw new UnsupportedOperationException("Simple type " + type + " not accepted");
      }
    } else if (SimpleTypeKind.BOOLEAN.class.isInstance(typeKind)) {
      return valueFactory.createValue((Boolean)o);
    }
    else {
      throw new UnsupportedOperationException("Simple type " + type + " not accepted");
    }
  }
}
