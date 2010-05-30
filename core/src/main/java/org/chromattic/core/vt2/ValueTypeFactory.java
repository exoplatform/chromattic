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

import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.spi.type.EnumeratedValueType;
import org.chromattic.spi.type.SimpleValueTypes;
import org.chromattic.spi.type.ValueType;
import org.reflext.api.ClassTypeInfo;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTypeFactory {

  public static <I> ValueType<I, ?> create(ClassTypeInfo type, JCRPropertyType<I> jcrType) {
    ValueType vt;

    Class typeClass = (Class)type.getType();

    if (String.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.PATH) {
        vt = new SimpleValueTypes.PATH();
      } else if (jcrType == JCRPropertyType.STRING) {
        vt = new SimpleValueTypes.STRING();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Integer.class.equals(typeClass) || int.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.LONG) {
        vt = new SimpleValueTypes.INTEGER();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Boolean.class.equals(typeClass) || boolean.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.BOOLEAN) {
        vt = new SimpleValueTypes.BOOLEAN();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Long.class.equals(typeClass) || long.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.LONG) {
        vt = new SimpleValueTypes.LONG();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Date.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.DATE) {
        vt = new SimpleValueTypes.DATE();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Double.class.equals(typeClass) || double.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.DOUBLE) {
        vt = new SimpleValueTypes.DOUBLE();
      } else {
        throw new AssertionError("todo");
      }
    } else if (Float.class.equals(typeClass) || float.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.DOUBLE) {
        vt = new SimpleValueTypes.FLOAT();
      } else {
        throw new AssertionError("todo");
      }
    } else if (InputStream.class.equals(typeClass)) {
      if (jcrType == JCRPropertyType.BINARY) {
        vt = new SimpleValueTypes.BINARY();
      } else {
        throw new AssertionError("todo");
      }
    } else if (typeClass.isEnum()) {
      vt = new EnumeratedValueType(typeClass);
    } else {
      throw new AssertionError();
    }

    //
    return (ValueType<I, ?>)vt;
  }
}
