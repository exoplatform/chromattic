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

package org.chromattic.core.vt2;

import org.chromattic.metamodel.bean.SimpleType;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.spi.type.ValueType;

import javax.jcr.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueDefinition<E> {

  public static ValueDefinition<?> get(Object o) {
    int code;
    if (o instanceof String) {
      code = PropertyType.STRING;
    } else if (o instanceof Long) {
      code = PropertyType.LONG;
    } else if (o instanceof Boolean) {
      code = PropertyType.BOOLEAN;
    } else if (o instanceof Double) {
      code = PropertyType.DOUBLE;
    } else if (o instanceof InputStream) {
      code = PropertyType.BINARY;
    } else if (o instanceof Long) {
      code = PropertyType.LONG;
    } else if (o instanceof Date) {
      code = PropertyType.DATE;
    } else {
      return null;
    }

    //
    return get(code);
  }

  public static ValueDefinition<?> get(int code) {
    switch (code) {
      case PropertyType.STRING:
        return new ValueDefinition<String>(
          SimpleType.STRING,
          JCRPropertyType.STRING,
          SimpleValueTypes.STRING,
          null
        );
      case PropertyType.PATH:
        return new ValueDefinition<String>(
          SimpleType.STRING,
          JCRPropertyType.PATH,
          SimpleValueTypes.PATH,
          null
        );
      case PropertyType.NAME:
        return new ValueDefinition<String>(
          SimpleType.STRING,
          JCRPropertyType.NAME,
          SimpleValueTypes.NAME,
          null
        );
      case PropertyType.LONG:
        return new ValueDefinition<Long>(
          SimpleType.LONG,
          JCRPropertyType.LONG,
          SimpleValueTypes.LONG,
          null
        );
      case PropertyType.BOOLEAN:
        return new ValueDefinition<Boolean>(
          SimpleType.BOOLEAN,
          JCRPropertyType.BOOLEAN,
          SimpleValueTypes.BOOLEAN,
          null
        );
      case PropertyType.DOUBLE:
        return new ValueDefinition<Double>(
          SimpleType.DOUBLE,
          JCRPropertyType.DOUBLE,
          SimpleValueTypes.DOUBLE,
          null
        );
      case PropertyType.BINARY:
        return new ValueDefinition<InputStream>(
          SimpleType.STREAM,
          JCRPropertyType.BINARY,
          SimpleValueTypes.BINARY,
          null
        );
      case PropertyType.UNDEFINED:
        return null;
      default:
        throw new AssertionError("Unsupported JCR type " + code);
    }
  }

  /** . */
  private final SimpleType simpleType;

  /** . */
  private final ValueType valueType;

  /** . */
  private final List<String> defaultValue;

  /** . */
  private final JCRPropertyType jcrType;

  public ValueDefinition(
    SimpleType simpleType,
    JCRPropertyType jcrType,
    ValueType<?, E> valueType,
    List<String> defaultValue) {
    this.simpleType = simpleType;
    this.valueType = valueType;
    this.defaultValue = defaultValue;
    this.jcrType = jcrType;
  }

  public boolean isPrimitive() {
    if (simpleType instanceof SimpleType.Base) {
      return ((SimpleType.Base)simpleType).isPrimitive();
    } else {
      return false;
    }
  }

  public List<E> getDefaultValue() {
    if (defaultValue != null) {
      ArrayList<E> a = new ArrayList<E>();

      for (String d : defaultValue) {
        a.add((E)valueType.fromString(d));
      }

      return a;
    } else {
      return null;
    }
  }

  public Value get(ValueFactory factory, E value) throws RepositoryException {

    Object internal = valueType.getInternal(value);

    //
    switch (jcrType.getCode()) {
      case PropertyType.STRING:
        return factory.createValue((String)internal, PropertyType.STRING);
      case PropertyType.NAME:
        return factory.createValue((String)internal, PropertyType.NAME);
      case PropertyType.PATH:
        return factory.createValue((String)internal, PropertyType.PATH);
      case PropertyType.BINARY:
        return factory.createValue((InputStream)internal);
      case PropertyType.LONG:
        return factory.createValue((Long)internal);
      case PropertyType.DOUBLE:
        return factory.createValue((Double)internal);
      case PropertyType.DATE:
        return factory.createValue((Calendar)internal);
      case PropertyType.BOOLEAN:
        return factory.createValue((Boolean)internal);
      default:
        throw new AssertionError();
    }
  }

  public E get(Value value) throws RepositoryException {

    int propertyType = value.getType();

    //
    Object o;
    switch (propertyType) {
      case PropertyType.NAME:
      case PropertyType.PATH:
      case PropertyType.STRING:
        o = value.getString();
        break;
      case PropertyType.BOOLEAN:
        o = value.getBoolean();
        break;
      case PropertyType.DOUBLE:
        o = value.getDouble();
        break;
      case PropertyType.LONG:
        o = value.getLong();
        break;
      case PropertyType.DATE:
        o = value.getDate();
        break;
      case PropertyType.BINARY:
        o = value.getStream();
        break;
      default:
        throw new AssertionError("Property type " + propertyType + " not handled");
    }

    return (E)valueType.getExternal(o);
  }


  public Class<?> getRealType() {
    if (simpleType instanceof SimpleType.Base) {
      return ((SimpleType.Base)simpleType).getRealType();
    } else {
      return valueType.getExternalType();
    }
  }

  public Class<E> getObjectType() {
    return valueType.getExternalType();
  }
}
