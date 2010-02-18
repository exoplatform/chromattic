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

import org.chromattic.core.bean.BaseSimpleTypes;
import org.chromattic.core.bean.SimpleTypeKind;

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

    @Override
    public Value get(ValueFactory valueFactory, Object o) throws ValueFormatException {

      SimpleTypeKind<?, ?> typeKind;
      if (o instanceof String) {
        typeKind = BaseSimpleTypes.STRING;
      } else if (o instanceof Integer) {
        typeKind = BaseSimpleTypes.INT;
      } else if (o instanceof Long) {
        typeKind = BaseSimpleTypes.LONG;
      } else if (o instanceof Date) {
        typeKind = BaseSimpleTypes.DATE;
      } else if (o instanceof Double) {
        typeKind = BaseSimpleTypes.DOUBLE;
      } else if (o instanceof Float) {
        typeKind = BaseSimpleTypes.FLOAT;
      } else if (o instanceof InputStream) {
        typeKind = BaseSimpleTypes.STREAM;
      } else if (o instanceof Boolean) {
        typeKind = BaseSimpleTypes.BOOLEAN;
      } else {
        throw new UnsupportedOperationException("Type " + o.getClass().getName() + " is not accepted");
      }

      return ValueMapper.instance.get(valueFactory, o, typeKind);
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
