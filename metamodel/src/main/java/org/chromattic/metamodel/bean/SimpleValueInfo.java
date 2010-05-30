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

package org.chromattic.metamodel.bean;

import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.reflext.api.ClassKind;
import org.reflext.api.ClassTypeInfo;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueInfo extends ValueInfo {

  /** . */
  private static final Map<String, JCRPropertyType<?>> typeMapping = new HashMap<String, JCRPropertyType<?>>();

  static {
    typeMapping.put(int.class.getName(), JCRPropertyType.LONG);
    typeMapping.put(Integer.class.getName(), JCRPropertyType.LONG);
    typeMapping.put(long.class.getName(), JCRPropertyType.LONG);
    typeMapping.put(Long.class.getName(), JCRPropertyType.LONG);
    typeMapping.put(boolean.class.getName(), JCRPropertyType.BOOLEAN);
    typeMapping.put(Boolean.class.getName(), JCRPropertyType.BOOLEAN);
    typeMapping.put(float.class.getName(), JCRPropertyType.DOUBLE);
    typeMapping.put(Float.class.getName(), JCRPropertyType.DOUBLE);
    typeMapping.put(double.class.getName(), JCRPropertyType.DOUBLE);
    typeMapping.put(Double.class.getName(), JCRPropertyType.DOUBLE);
    typeMapping.put(String.class.getName(), JCRPropertyType.STRING);
    typeMapping.put(InputStream.class.getName(), JCRPropertyType.BINARY);
    typeMapping.put(Date.class.getName(), JCRPropertyType.DATE);
  }

  /** . */
  private final JCRPropertyType<?> jcrType;

  SimpleValueInfo(ClassTypeInfo typeInfo) {
    super(typeInfo);

    //
    JCRPropertyType<?> jcrType;
    if (typeInfo.getKind() == ClassKind.ENUM) {
      jcrType = JCRPropertyType.STRING;
    } else {
      jcrType = typeMapping.get(typeInfo.getName());
    }

    //
    if (jcrType == null) {
      throw new UnsupportedOperationException("todo");
    }

    //
    this.jcrType = jcrType;
  }

  public JCRPropertyType<?> getJCRType() {
    return jcrType;
  }

  @Override
  public String toString() {
    return "SimpleValueInfo[typeInfo=" + typeInfo + "]";
  }
}
