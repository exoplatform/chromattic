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

import org.chromattic.core.bean.SimpleType;

import javax.jcr.*;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueType<V> {
  
  public static final ValueType<Object> DEFAULT = new ValueType<Object>() {
    @Override
    public List<Object> getDefaultValue() {
      return null;
    }

    @Override
    public boolean isPrimitive() {
      return false;
    }

    @Override
    public Object get(Value value) throws RepositoryException {
      int propertyType = value.getType();
      switch (propertyType) {
        case PropertyType.BOOLEAN:
          return Boolean.valueOf(value.getBoolean());
        case PropertyType.LONG:
          return Integer.valueOf((int)value.getLong());
        case PropertyType.DOUBLE:
          return Double.valueOf(value.getDouble());
        case PropertyType.NAME:
        case PropertyType.PATH:
        case PropertyType.STRING:
          return value.getString();
        case PropertyType.BINARY:
          return value.getStream();
        case PropertyType.DATE:
          return value.getDate().getTime();
        default:
          throw new AssertionError("Property type " + propertyType + " not handled");
      }
    }

    private final BaseValueType STRING = new BaseValueType.STRING.TO_STRING(null, SimpleType.STRING);
    private final BaseValueType INT = new BaseValueType.LONG.TO_INT(null, SimpleType.INTEGER);
    private final BaseValueType LONG = new BaseValueType.LONG.TO_LONG(null, SimpleType.LONG);
    private final BaseValueType DATE = new BaseValueType.DATE.TO_DATE(null, SimpleType.DATE);
    private final BaseValueType DOUBLE = new BaseValueType.DOUBLE.TO_DOUBLE(null, SimpleType.DOUBLE);
    private final BaseValueType FLOAT = new BaseValueType.DOUBLE.TO_FLOAT(null, SimpleType.FLOAT);
    private final BaseValueType STREAM = new BaseValueType.STREAM.TO_STREAM(null, SimpleType.STREAM);
    private final BaseValueType BOOLEAN = new BaseValueType.BOOLEAN.TO_BOOLEAN(null, SimpleType.BOOLEAN);

    @Override
    public Value get(ValueFactory valueFactory, Object o) throws ValueFormatException {

      ValueType typeKind;
      if (o instanceof String) {
        typeKind = STRING;
      } else if (o instanceof Integer) {
        typeKind = INT;
      } else if (o instanceof Long) {
        typeKind = LONG;
      } else if (o instanceof Date) {
        typeKind = DATE;
      } else if (o instanceof Double) {
        typeKind = DOUBLE;
      } else if (o instanceof Float) {
        typeKind = FLOAT;
      } else if (o instanceof InputStream) {
        typeKind = STREAM;
      } else if (o instanceof Boolean) {
        typeKind = BOOLEAN;
      } else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }

      return typeKind.get(valueFactory, o);
    }

    @Override
    public Class<Object> getObjectType() {
      return Object.class;
    }

    @Override
    public Class<?> getRealType() {
      return Object.class;
    }
  };

  public abstract List<V> getDefaultValue();

  public abstract boolean isPrimitive();

  public abstract V get(Value value) throws RepositoryException;

  public abstract Value get(ValueFactory valueFactory, V o) throws ValueFormatException;

  public abstract Class<V> getObjectType();

  public abstract Class<?> getRealType();
}
