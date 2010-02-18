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

import org.chromattic.core.bean.SimpleTypeKind;
import org.chromattic.core.bean.BaseSimpleTypes;
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

  public final <E> E get(Value value, SimpleType<E> wantedType) throws RepositoryException {
    int propertyType = value.getType();
    if (wantedType != null) {
      SimpleTypeKind<E, ?> typeKind = wantedType.getKind();
      if (typeKind instanceof SimpleTypeKind.STREAM) {
        SimpleTypeKind.STREAM<?> streamKind = (SimpleTypeKind.STREAM<?>)typeKind;
        if (propertyType == PropertyType.BINARY) {
          return (E)streamKind.toExternal(value.getStream());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.STRING) {
        SimpleTypeKind.STRING stringKind = (SimpleTypeKind.STRING)typeKind;
        if (propertyType == PropertyType.STRING || propertyType == PropertyType.NAME || propertyType == PropertyType.PATH) {
          return (E)stringKind.toExternal(value.getString());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.PATH) {
        SimpleTypeKind.PATH pathKind = (SimpleTypeKind.PATH)typeKind;
        if (propertyType == PropertyType.PATH) {
          return (E)pathKind.toExternal(value.getString());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.LONG) {
        SimpleTypeKind.LONG longKind = (SimpleTypeKind.LONG)typeKind;
        if (propertyType == PropertyType.LONG) {
          return (E)longKind.toExternal(value.getLong());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.DOUBLE) {
        SimpleTypeKind.DOUBLE doubleKind = (SimpleTypeKind.DOUBLE)typeKind;
        if (propertyType == PropertyType.DOUBLE) {
          return (E)doubleKind.toExternal(value.getDouble());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.BOOLEAN) {
        SimpleTypeKind.BOOLEAN booleanKind = (SimpleTypeKind.BOOLEAN)typeKind;
        if (propertyType == PropertyType.BOOLEAN) {
          return (E)booleanKind.toExternal(value.getBoolean());
        } else {
          throw new ClassCastException();
        }
      } else if (typeKind instanceof SimpleTypeKind.DATE) {
        SimpleTypeKind.DATE dateKind = (SimpleTypeKind.DATE)typeKind;
        if (propertyType == PropertyType.DATE) {
          return (E)dateKind.toExternal(value.getDate().getTime());
        } else {
          throw new ClassCastException();
        }
      } else {
        throw new AssertionError("Property type " + propertyType + " not handled");
      }
    } else {
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
      } else if (o instanceof Integer) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.INT;
      } else if (o instanceof Long) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.LONG;
      } else if (o instanceof Date) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.DATE;
      } else if (o instanceof Double) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.DOUBLE;
      } else if (o instanceof Float) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.FLOAT;
      } else if (o instanceof InputStream) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.STREAM;
      } else if (o instanceof Boolean) {
        typeKind = (SimpleTypeKind<E, ?>)BaseSimpleTypes.BOOLEAN;
      } else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }
    } else {
      typeKind = type.getKind();
    }

    //
    if (typeKind instanceof SimpleTypeKind.STRING) {
      SimpleTypeKind.STRING<E> stringKind = (SimpleTypeKind.STRING<E>)typeKind;
      String s = stringKind.toInternal(o);
      return valueFactory.createValue(s);
    } else if (typeKind instanceof SimpleTypeKind.PATH) {
      SimpleTypeKind.PATH<E> pathKind = (SimpleTypeKind.PATH<E>)typeKind;
      String s = pathKind.toInternal(o);
      return valueFactory.createValue(s, PropertyType.PATH);
    } else if (typeKind instanceof SimpleTypeKind.LONG) {
      SimpleTypeKind.LONG<E> longKind = (SimpleTypeKind.LONG<E>)typeKind;
      Long l = longKind.toInternal(o);
      return valueFactory.createValue(l);
    } else if (typeKind instanceof SimpleTypeKind.DATE) {
      SimpleTypeKind.DATE<E> dateKind = (SimpleTypeKind.DATE<E>)typeKind;
      Date time = dateKind.toInternal(o);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(time);
      return valueFactory.createValue(calendar);
    } else if (typeKind instanceof SimpleTypeKind.STREAM) {
      SimpleTypeKind.STREAM<E> streamKind = (SimpleTypeKind.STREAM<E>)typeKind;
      InputStream in = streamKind.toInternal(o);
      return valueFactory.createValue(in);
    } else if (typeKind instanceof SimpleTypeKind.DOUBLE) {
      SimpleTypeKind.DOUBLE<E> doubleKind = (SimpleTypeKind.DOUBLE<E>)typeKind;
      Double d = doubleKind.toInternal(o);
      return valueFactory.createValue(d);
    } else if (typeKind instanceof SimpleTypeKind.BOOLEAN) {
      SimpleTypeKind.BOOLEAN<E> booleanKind = (SimpleTypeKind.BOOLEAN<E>)typeKind;
      Boolean b = booleanKind.toInternal(o);
      return valueFactory.createValue(b);
    } else {
      throw new UnsupportedOperationException("Simple type " + type + " not accepted");
    }
  }
}
