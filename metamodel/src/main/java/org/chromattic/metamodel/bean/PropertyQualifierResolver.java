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

import org.chromattic.api.BuilderException;
import org.chromattic.api.annotations.*;
import org.chromattic.metamodel.bean.value.*;
import org.chromattic.metamodel.mapping.InvalidMappingException;
import org.chromattic.metamodel.mapping.NodeAttributeType;
import org.chromattic.metamodel.type.PropertyTypeResolver;
import org.chromattic.metamodel.type.ValueTypeInfo;
import org.reflext.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyQualifierResolver {

  /** . */
  private final ClassTypeInfo beanType;

  /** . */
  private final PropertyTypeResolver typeResolver;

  public PropertyQualifierResolver(ClassTypeInfo beanType) {
    this.beanType = beanType;
    this.typeResolver = new PropertyTypeResolver();
  }

  private List<PropertyRole> findRoles(PropertyInfo propertyInfo) {
    List<PropertyRole> roles = new ArrayList<PropertyRole>();

    //
    AnnotatedProperty<Property> propertyAnnotation = propertyInfo.getAnnotated(Property.class);
    if (propertyAnnotation != null) {
      roles.add(new PropertyRole.Property(propertyAnnotation.getOwner(), propertyAnnotation.getAnnotation().name(), propertyAnnotation.getAnnotation().type()));
    }

    //
    AnnotatedProperty<Properties> propertiesAnnotation = propertyInfo.getAnnotated(Properties.class);
    if (propertiesAnnotation != null) {
      roles.add(new PropertyRole.Properties(propertiesAnnotation.getOwner()));
    }

    //
    Collection<AnnotatedProperty<?>> attributeAnnotations = propertyInfo.getAnnotateds(Name.class, Id.class, Path.class, WorkspaceName.class);
    for (AnnotatedProperty<?> attributeAnnotation : attributeAnnotations) {
      NodeAttributeType nat;
      if (attributeAnnotation.getAnnotation() instanceof Name) {
        nat = NodeAttributeType.NAME;
      } else if (attributeAnnotation.getAnnotation() instanceof Id) {
        nat = NodeAttributeType.ID;
      } else if (attributeAnnotation.getAnnotation() instanceof Path) {
        nat = NodeAttributeType.PATH;
      } else if (attributeAnnotation.getAnnotation() instanceof WorkspaceName) {
        nat = NodeAttributeType.WORKSPACE_NAME;
      } else {
        throw new AssertionError();
      }
      roles.add(new PropertyRole.Attribute(attributeAnnotation.getOwner(), nat));
    }

    //
    AnnotatedProperty<OneToOne> oneToOneAnnotation = propertyInfo.getAnnotated(OneToOne.class);
    if (oneToOneAnnotation != null) {
      roles.add(new PropertyRole.OneToOne(oneToOneAnnotation.getOwner(), oneToOneAnnotation.getAnnotation().type()));
    }

    //
    AnnotatedProperty<OneToMany> oneToManyAnnotation = propertyInfo.getAnnotated(OneToMany.class);
    if (oneToManyAnnotation != null) {
      roles.add(new PropertyRole.OneToMany(oneToManyAnnotation.getOwner(), oneToManyAnnotation.getAnnotation().type()));
    }

    //
    AnnotatedProperty<ManyToOne> manyToOneAnnotation = propertyInfo.getAnnotated(ManyToOne.class);
    if (manyToOneAnnotation != null) {
      roles.add(new PropertyRole.ManyToOne(manyToOneAnnotation.getOwner(), manyToOneAnnotation.getAnnotation().type()));
    }

    //
    return roles;
  }

  public PropertyQualifier createPropertyInfo(
    PropertyInfo propertyInfo
  ) {

    //
    List<PropertyRole> roles = findRoles(propertyInfo);

    //
    if (roles.size() > 1) {
      throw new InvalidMappingException(beanType, "Too many annotations of the same kind " + roles);
    }

    // Get role
    PropertyRole role = roles.isEmpty() ? null : roles.get(0);

    //
    TypeInfo resolvedTI = beanType.resolve(propertyInfo.getDeclaredType());

    //
    ValueInfo vi = bilto(resolvedTI);

    //
    if (vi != null) {
      return new PropertyQualifier(role, propertyInfo, vi);
    }

    //
    return null;
  }

  private ValueInfo bilto(TypeInfo typeInfo) {

    ValueInfo res = createValue(typeInfo);

    // It's a simple value type
    if (res != null) {
      return res;
    }

    //
    if (typeInfo instanceof ParameterizedTypeInfo) {

      //
      ParameterizedTypeInfo parameterizedTI = (ParameterizedTypeInfo)typeInfo;
      TypeInfo rawTI = parameterizedTI.getRawType();

      //
      if (rawTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawClassTI = (ClassTypeInfo)rawTI;
        String rawClassName = rawClassTI.getName();

        //
        if (rawClassName.equals("java.util.Collection") || rawClassName.equals("java.util.List")) {
          TypeInfo elementTV = parameterizedTI.getTypeArguments().get(0);
          ClassTypeInfo elementTI = resolveClass(beanType, elementTV);
          CollectionType collectionType = rawClassName.equals("java.util.Collection") ? CollectionType.COLLECTION : CollectionType.LIST;

          //
          if (elementTI != null) {
            ValueInfo resolvedElementTI = createValue(elementTI);
            return new CollectionValueInfo<ValueInfo>(typeInfo, collectionType, resolvedElementTI);
          }
        } else if (rawClassName.equals("java.util.Map")) {
          TypeInfo elementTV = parameterizedTI.getTypeArguments().get(1);
          ClassTypeInfo elementTI = resolveClass(beanType, elementTV);

          //
          if (elementTI != null) {
            ValueInfo resolvedElementTI = createValue(elementTI);
            TypeInfo keyTV = parameterizedTI.getTypeArguments().get(0);
            ClassTypeInfo keyTI = resolveClass(beanType, keyTV);

            //
            if (keyTI != null) {
              ValueInfo resolvedKeyTI = createValue(keyTI);
              return new MapValueInfo<ValueInfo,ValueInfo>(typeInfo, resolvedKeyTI, resolvedElementTI);
            }
          }
        }
      }
    } else if (typeInfo instanceof ArrayTypeInfo) {
      TypeInfo componentTI = ((ArrayTypeInfo)typeInfo).getComponentType();
      if (componentTI instanceof ClassTypeInfo) {
        ClassTypeInfo rawComponentTI = (ClassTypeInfo)componentTI;
        if (rawComponentTI.getName().equals("byte")) {
          return new SimpleValueInfo(typeInfo);
        } else {
          ValueInfo resolved = createValue(rawComponentTI);
          if (resolved.getKind() == TypeKind.SIMPLE) {
            return new CollectionValueInfo<ValueInfo>(typeInfo, CollectionType.ARRAY, resolved);
          }
        }
      }
    }

    //
    return null;
  }

  private ValueInfo createValue(TypeInfo typeInfo) throws BuilderException {
    ValueTypeInfo vti = typeResolver.resolveType(typeInfo);
    if (vti != null) {
      return new SimpleValueInfo(typeInfo);
    } else if (typeInfo instanceof ClassTypeInfo) {
      return new BeanValueInfo((ClassTypeInfo)typeInfo);
    } else {
      return null;
    }
  }

  private ClassTypeInfo resolveClass(ClassTypeInfo baseType, TypeInfo type) {
    TypeInfo resolvedType = baseType.resolve(type);
    return resolvedType instanceof ClassTypeInfo ? (ClassTypeInfo)resolvedType : null;
  }
}
