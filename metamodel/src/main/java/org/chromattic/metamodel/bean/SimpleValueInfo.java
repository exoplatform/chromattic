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
import org.chromattic.spi.type.SimpleValueTypes;
import org.chromattic.spi.type.ValueType;
import org.reflext.api.ClassKind;
import org.reflext.api.ClassTypeInfo;
import org.reflext.core.TypeDomain;
import org.reflext.jlr.JavaLangReflectMethodModel;
import org.reflext.jlr.JavaLangReflectTypeModel;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleValueInfo extends ValueInfo {

  private static final TypeDomain<Type, Method> typeDomain = new TypeDomain<Type, Method>(
    new JavaLangReflectTypeModel(),
    new JavaLangReflectMethodModel()
  );

  /** . */
  private static final Map<String, ClassTypeInfo> typeMapping = new HashMap<String, ClassTypeInfo>();

  /** . */
  private static final Map<JCRPropertyType<?>, ClassTypeInfo> jcrTypes = new HashMap<JCRPropertyType<?>, ClassTypeInfo>();

  static {
    typeMapping.put(int.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.PRIMITIVE_INTEGER.class));
    typeMapping.put(Integer.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.INTEGER.class));
    typeMapping.put(long.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.PRIMITIVE_LONG.class));
    typeMapping.put(Long.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.LONG.class));
    typeMapping.put(boolean.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.PRIMITIVE_BOOLEAN.class));
    typeMapping.put(Boolean.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.BOOLEAN.class));
    typeMapping.put(float.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.PRIMITIVE_FLOAT.class));
    typeMapping.put(Float.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.FLOAT.class));
    typeMapping.put(double.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.PRIMITIVE_DOUBLE.class));
    typeMapping.put(Double.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.DOUBLE.class));
    typeMapping.put(String.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.STRING.class));
    typeMapping.put(InputStream.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.BINARY.class));
    typeMapping.put(Date.class.getName(), (ClassTypeInfo)typeDomain.getType(SimpleValueTypes.DATE.class));

    //
    jcrTypes.put(JCRPropertyType.STRING, (ClassTypeInfo)typeDomain.getType(ValueType.STRING.class));
    jcrTypes.put(JCRPropertyType.PATH, (ClassTypeInfo)typeDomain.getType(ValueType.PATH.class));
    jcrTypes.put(JCRPropertyType.NAME, (ClassTypeInfo)typeDomain.getType(ValueType.NAME.class));
    jcrTypes.put(JCRPropertyType.LONG, (ClassTypeInfo)typeDomain.getType(ValueType.LONG.class));
    jcrTypes.put(JCRPropertyType.DOUBLE, (ClassTypeInfo)typeDomain.getType(ValueType.DOUBLE.class));
    jcrTypes.put(JCRPropertyType.BOOLEAN, (ClassTypeInfo)typeDomain.getType(ValueType.BOOLEAN.class));
    jcrTypes.put(JCRPropertyType.BINARY, (ClassTypeInfo)typeDomain.getType(ValueType.BINARY.class));
    jcrTypes.put(JCRPropertyType.DATE, (ClassTypeInfo)typeDomain.getType(ValueType.DATE.class));
  }

  /** The value type info as defined by the type. */
//  private final ClassTypeInfo valueTypeInfo;

  /** . */
  private final JCRPropertyType<?> jcrType;

  SimpleValueInfo(ClassTypeInfo typeInfo) {
    super(typeInfo);

    //
    JCRPropertyType<?> jcrType = null;
    if (typeInfo.getKind() == ClassKind.ENUM) {
      jcrType = JCRPropertyType.STRING;
    } else {
      ClassTypeInfo valueTypeInfo = typeMapping.get(typeInfo.getName());
      for (Map.Entry<JCRPropertyType<?>, ClassTypeInfo> entry : jcrTypes.entrySet()) {
        if (valueTypeInfo.isSubType(entry.getValue())) {
          jcrType = entry.getKey();
          break;
        }
      }
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
