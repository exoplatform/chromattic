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

import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.type.SimpleTypeProviders;
import org.chromattic.spi.type.SimpleTypeProvider;

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
public class ValueDefinition<I, E> {

  /** . */
  private static final ValueDefinition<String, String> STRING = new ValueDefinition<String, String>(
    String.class,
    PropertyMetaType.STRING,
    new SimpleTypeProviders.STRING(),
    null);

  /** . */
  private static final ValueDefinition<String, String> PATH = new ValueDefinition<String, String>(
    String.class,
    PropertyMetaType.PATH,
    new SimpleTypeProviders.PATH(),
    null);

  /** . */
  private static final ValueDefinition<String, String> NAME = new ValueDefinition<String, String>(
      String.class,
      PropertyMetaType.NAME,
      new SimpleTypeProviders.NAME(),
      null);

  /** . */
  private static final ValueDefinition<Boolean, Boolean> BOOLEAN = new ValueDefinition<Boolean, Boolean>(
      Boolean.class,
      PropertyMetaType.BOOLEAN,
      new SimpleTypeProviders.BOOLEAN(),
      null);

  /** . */
  private static final ValueDefinition<Double, Double> DOUBLE = new ValueDefinition<Double, Double>(
      Double.class,
      PropertyMetaType.DOUBLE,
      new SimpleTypeProviders.DOUBLE(),
      null);

  /** . */
  private static final ValueDefinition<Long, Long> LONG = new ValueDefinition<Long, Long>(
      Long.class,
      PropertyMetaType.LONG,
      new SimpleTypeProviders.LONG(),
      null);

  /** . */
  private static final ValueDefinition<InputStream, InputStream> BINARY = new ValueDefinition<InputStream, InputStream>(
      InputStream.class,
      PropertyMetaType.BINARY,
      new SimpleTypeProviders.BINARY(),
      null);

  public static ValueDefinition<?, ?> get(Object o) {
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

  public static ValueDefinition<?, ?> get(int code) {
    switch (code) {
      case PropertyType.STRING:
        return STRING;
      case PropertyType.PATH:
        return PATH;
      case PropertyType.NAME:
        return NAME;
      case PropertyType.LONG:
        return LONG;
      case PropertyType.BOOLEAN:
        return BOOLEAN;
      case PropertyType.DOUBLE:
        return DOUBLE;
      case PropertyType.BINARY:
        return BINARY;
      case PropertyType.UNDEFINED:
        return null;
      default:
        throw new AssertionError("Unsupported JCR type " + code);
    }
  }

  /** . */
  private final Class<?> realType;

  /** . */
  private final SimpleTypeProvider<I, E> valueType;

  /** . */
  private final List<String> defaultValue;

  /** . */
  private final PropertyMetaType<I> propertyMetaType;

  public ValueDefinition(
    Class<?> realType,
    PropertyMetaType<I> propertyMetaType,
    SimpleTypeProvider<I, E> valueType,
    List<String> defaultValue) {
    this.realType = realType;
    this.valueType = valueType;
    this.defaultValue = defaultValue;
    this.propertyMetaType = propertyMetaType;
  }

  public boolean isPrimitive() {
    return realType.isPrimitive();
  }

  public List<E> getDefaultValue() {
    if (defaultValue != null) {
      ArrayList<E> a = new ArrayList<E>();

      for (String d : defaultValue) {
        a.add(valueType.fromString(d));
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
    if (expectedType != PropertyType.UNDEFINED && expectedType != propertyMetaType.getCode()) {
      throw new ClassCastException("Cannot cast type " + valueType.getExternalType() + " to type " + expectedType);
    } else {
      I internal = valueType.getInternal(value);
      return propertyMetaType.getValue(factory, internal);
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
    if (value.getType() == propertyMetaType.getCode()) {
      I internal = propertyMetaType.getValue(value);
      return valueType.getExternal(internal);
    } else {
      throw new ClassCastException();
    }
  }


  public Class<?> getRealType() {
    return realType;
  }

  public Class<E> getObjectType() {
    return valueType.getExternalType();
  }
}
