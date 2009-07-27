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
package org.chromattic.bean;

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.SimpleTypeInfo;

import java.util.Date;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueInfo extends ValueInfo {

  /** . */
  private final SimpleType simpleType;

  /** . */
  private final boolean primitive;

  public SimpleValueInfo(ClassTypeInfo typeInfo) {
    super(typeInfo);

    //
    if (typeInfo instanceof SimpleTypeInfo) {
      switch (((SimpleTypeInfo)typeInfo).getLiteralType()) {
        case BOOLEAN:
          simpleType = SimpleType.BOOLEAN;
          break;
        case INT:
          simpleType = SimpleType.INT;
          break;
        case LONG:
          simpleType = SimpleType.LONG;
          break;
        case FLOAT:
          simpleType = SimpleType.FLOAT;
          break;
        case DOUBLE:
          simpleType = SimpleType.DOUBLE;
          break;
        default:
          throw new AssertionError();
      }
      primitive = ((SimpleTypeInfo)typeInfo).isPrimitive();
    } else {
      if (String.class.getName().equals(typeInfo.getName())) {
        simpleType = SimpleType.STRING;
        primitive = false;
      } else if (Date.class.getName().equals(typeInfo.getName())) {
        simpleType = SimpleType.DATE;
        primitive = false;
      } else if (InputStream.class.getName().equals(typeInfo.getName())) {
        simpleType = SimpleType.BINARY;
        primitive = false;
      } else {
        throw new AssertionError();
      }
    }
  }

  public SimpleType getSimpleType() {
    return simpleType;
  }

  public boolean isPrimitive() {
    return primitive;
  }

  @Override
  public String toString() {
    return "SimpleValueInfo[simpleType=" + simpleType + ",primitive=" + primitive + "]";
  }
}
