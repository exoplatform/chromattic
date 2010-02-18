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

import org.chromattic.metamodel.bean.SimpleType;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.reflext.api.ClassTypeInfo;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTypeFactory {

  public static <V> ValueType<V> create(final SimpleValueInfo<V> sv) {
    return create(sv, sv.getSimpleType());
  }

  private static <E> ValueType<E> create(SimpleValueInfo<E> sv, SimpleType<E> type) {
    ValueType vt;
    if (type instanceof SimpleType.Base) {
      SimpleType.Base base = (SimpleType.Base)type;
      if (type == SimpleType.STRING) {
        vt = new BaseValueType.STRING.TO_STRING((List<String>)sv.getDefaultValue(), String.class);
      } else if (type == SimpleType.PATH) {
        vt = new BaseValueType.PATH.TO_STRING((List<String>)sv.getDefaultValue(), String.class);
      } else if (type == SimpleType.INTEGER || type == SimpleType.PRIMITIVE_INTEGER) {
        vt = new BaseValueType.LONG.TO_INT((List<Integer>)sv.getDefaultValue(), base.getRealType());
      } else if (type == SimpleType.BOOLEAN || type == SimpleType.PRIMITIVE_BOOLEAN) {
        vt = new BaseValueType.BOOLEAN.TO_BOOLEAN((List<Boolean>)sv.getDefaultValue(), base.getRealType());
      } else if (type == SimpleType.LONG || type == SimpleType.PRIMITIVE_LONG) {
        vt = new BaseValueType.LONG.TO_LONG((List<Long>)sv.getDefaultValue(), base.getRealType());
      } else if (type == SimpleType.DATE) {
        vt = new BaseValueType.DATE.TO_DATE((List<Date>)sv.getDefaultValue(), Date.class);
      } else if (type == SimpleType.DOUBLE || type == SimpleType.PRIMITIVE_DOUBLE) {
        vt = new BaseValueType.DOUBLE.TO_DOUBLE((List<Double>)sv.getDefaultValue(), base.getRealType());
      } else if (type == SimpleType.FLOAT || type == SimpleType.PRIMITIVE_FLOAT) {
        vt = new BaseValueType.DOUBLE.TO_FLOAT((List<Float>)sv.getDefaultValue(), base.getRealType());
      } else if (type == SimpleType.STREAM) {
        vt = new BaseValueType.STREAM.TO_STREAM((List<InputStream>)sv.getDefaultValue(), InputStream.class);
      } else {
        throw new AssertionError();
      }
    } else {
      SimpleType.Enumerated enumerated = (SimpleType.Enumerated)type;
      ClassTypeInfo cti = enumerated.getTypeInfo();
      Class<?> realType = (Class<?>)cti.getType();
      if (realType.isEnum()) {
        vt = new StringEnumValueType(sv.getDefaultValue(), realType);
      } else {
        throw new UnsupportedOperationException("investigate later " + type);
      }
    }

    //
    return (ValueType<E>)vt;
  }
}
