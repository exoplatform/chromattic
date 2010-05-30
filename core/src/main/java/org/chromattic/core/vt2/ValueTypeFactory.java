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
import org.reflext.api.ClassTypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTypeFactory {

  public static <I> ValueType<I, ?> create(SimpleType type, JCRPropertyType<I> jcrType) {
    ValueType vt;
    if (type instanceof SimpleType.Base) {
      SimpleType.Base base = (SimpleType.Base)type;
      if (type == SimpleType.STRING) {
        if (jcrType == JCRPropertyType.PATH) {
          vt = SimpleValueTypes.PATH;
        } else if (jcrType == JCRPropertyType.STRING) {
          vt = SimpleValueTypes.STRING;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.INTEGER || type == SimpleType.PRIMITIVE_INTEGER) {
        if (jcrType == JCRPropertyType.LONG) {
          vt = SimpleValueTypes.INTEGER;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.BOOLEAN || type == SimpleType.PRIMITIVE_BOOLEAN) {
        if (jcrType == JCRPropertyType.BOOLEAN) {
          vt = SimpleValueTypes.BOOLEAN;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.LONG || type == SimpleType.PRIMITIVE_LONG) {
        if (jcrType == JCRPropertyType.LONG) {
          vt = SimpleValueTypes.LONG;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.DATE) {
        if (jcrType == JCRPropertyType.DATE) {
          vt = SimpleValueTypes.DATE;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.DOUBLE || type == SimpleType.PRIMITIVE_DOUBLE) {
        if (jcrType == JCRPropertyType.DOUBLE) {
          vt = SimpleValueTypes.DOUBLE;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.FLOAT || type == SimpleType.PRIMITIVE_FLOAT) {
        if (jcrType == JCRPropertyType.DOUBLE) {
          vt = SimpleValueTypes.FLOAT;
        } else {
          throw new AssertionError("todo");
        }
      } else if (type == SimpleType.STREAM) {
        if (jcrType == JCRPropertyType.BINARY) {
          vt = SimpleValueTypes.BINARY;
        } else {
          throw new AssertionError("todo");
        }
      } else {
        throw new AssertionError();
      }
    } else {
      if (jcrType != JCRPropertyType.STRING) {
        throw new AssertionError("todo");
      }
      SimpleType.Enumerated enumerated = (SimpleType.Enumerated)type;
      ClassTypeInfo cti = enumerated.getTypeInfo();
      Class<?> realType = (Class<?>)cti.getType();
      if (realType.isEnum()) {
        vt = new EnumeratedValueType(realType);
      } else {
        throw new UnsupportedOperationException("investigate later " + type);
      }
    }

    //
    return (ValueType<I, ?>)vt;
  }
}
