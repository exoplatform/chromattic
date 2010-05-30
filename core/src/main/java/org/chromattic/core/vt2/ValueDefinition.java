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

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.io.InputStream;
import java.util.ArrayList;
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

  /**
   * Converts an external value to a JCR value.
   *
   * @param factory the value factory
   * @param expectedType the expected JCR type
   * @param value the value to convert
   * @return the converted value
   * @throws RepositoryException any repository exception
   * @throws ClassCastException if the value does not meet the expected type
   */
  public Value get(ValueFactory factory, int expectedType, E value) throws RepositoryException, ClassCastException {
    if (expectedType != PropertyType.UNDEFINED && expectedType != jcrType.getCode()) {
      throw new ClassCastException("Cannot cast type " + valueType.getExternalType() + " to type " + expectedType);
    } else {
      Object internal = valueType.getInternal(value);
      return jcrType.getValue(factory, internal);
    }
  }

  /**
   * Converts a JCR value to its external representation.
   *
   * @param value the value to convert
   * @return the converted value
   * @throws RepositoryException any repository exception
   * @throws ClassCastException if the value type is not the expected type
   */
  public E get(Value value) throws RepositoryException, ClassCastException {
    if (value.getType() == jcrType.getCode()) {
      Object o = jcrType.getValue(value);
      return (E)valueType.getExternal(o);
    } else {
      throw new ClassCastException();
    }
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
