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

package org.chromattic.metamodel.type;

import org.chromattic.metatype.DataType;
import org.chromattic.spi.type.SimpleTypeProvider;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.TypeVariableInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class SimpleTypeMappingImpl<I> implements SimpleTypeMapping {

  /** . */
  private static final Map<ClassTypeInfo, DataType<?>> propertyMetaTypes;
  
  static {
    //
    Map<ClassTypeInfo, DataType<?>> _jcrTypes = new HashMap<ClassTypeInfo, DataType<?>>();
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.STRING.class), DataType.STRING);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.PATH.class), DataType.PATH);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.NAME.class), DataType.NAME);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.LONG.class), DataType.LONG);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.DOUBLE.class), DataType.DOUBLE);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.BOOLEAN.class), DataType.BOOLEAN);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.BINARY.class), DataType.BINARY);
    _jcrTypes.put((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.DATE.class), DataType.DATE);
    propertyMetaTypes = _jcrTypes;
  }

  /** . */
  private SimpleTypeProvider<I, ?> instance;

  /** . */
  private final DataType<I> propertyMetaType;

  /** . */
  final ClassTypeInfo typeInfo;

  /** . */
  final TypeInfo external;

  SimpleTypeMappingImpl(ClassTypeInfo typeInfo) {
    
    // Find the right subclass
    ClassTypeInfo current = typeInfo;
    while (!current.getSuperClass().getName().equals(SimpleTypeProvider.class.getName())) {
      current = current.getSuperClass();
    }

    //
    ClassTypeInfo stp = (ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.class);
    TypeVariableInfo tvi = stp.getTypeParameters().get(1); // <E>
    TypeInfo aaa = typeInfo.resolve(tvi);
//    if (!aaa.equals(typeInfo)) {
//      throw new AssertionError(aaa + " should be equals to " + typeInfo);
//    }

    //
    DataType aaaaa = propertyMetaTypes.get(current);

    //
    this.propertyMetaType = aaaaa;
    this.typeInfo = typeInfo;
    this.external = aaa;
  }

  SimpleTypeMappingImpl(ClassTypeInfo typeInfo, DataType<I> propertyMetaType) {

    ClassTypeInfo stp = (ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(SimpleTypeProvider.class);
    TypeVariableInfo tvi = stp.getTypeParameters().get(1); // <E>
    TypeInfo aaa = typeInfo.resolve(tvi);

    //
    this.propertyMetaType = propertyMetaType;
    this.typeInfo = typeInfo;
    this.external = aaa;
  }

  SimpleTypeMappingImpl(Class<? extends SimpleTypeProvider<I, ?>> type, DataType<I> propertyMetaType) {
    this((ClassTypeInfo)SimpleTypeResolver.typeDomain.resolve(type), propertyMetaType);
  }

  public DataType<I> getPropertyMetaType() {
    return propertyMetaType;
  }

  public SimpleTypeProvider<I, ?> create() {
    if (instance == null) {
      Class type = (Class)typeInfo.unwrap();
      try {
        instance = (SimpleTypeProvider<I,?>)type.newInstance();
      }
      catch (InstantiationException e) {
        throw new AssertionError(e);
      }
      catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    return instance;
  }
}
