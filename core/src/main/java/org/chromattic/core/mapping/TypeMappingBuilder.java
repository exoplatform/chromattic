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

package org.chromattic.core.mapping;

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.Property;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.OneToOne;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.ManyToOne;
import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.RelatedMappedBy;
import org.chromattic.api.annotations.Id;
import org.chromattic.api.annotations.Path;
import org.chromattic.api.annotations.Destroy;
import org.chromattic.api.annotations.Properties;
import org.chromattic.api.annotations.FindById;
import org.chromattic.api.annotations.Mixin;
import org.chromattic.api.annotations.WorkspaceName;
import org.chromattic.api.RelationshipType;
import org.chromattic.core.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.core.mapping.jcr.JCRPropertyMapping;
import org.chromattic.core.mapping.value.SimpleMapping;
import org.chromattic.core.mapping.value.NamedOneToOneMapping;
import org.chromattic.core.mapping.value.NamedManyToOneMapping;
import org.chromattic.core.mapping.value.RelationshipMapping;
import org.chromattic.core.mapping.value.ManyToOneMapping;
import org.chromattic.core.mapping.value.OneToManyMapping;
import org.chromattic.core.mapping.value.NamedOneToManyMapping;
import org.chromattic.core.mapping.value.PropertyMapMapping;
import org.chromattic.core.NodeAttributeType;
import org.chromattic.core.bean.BeanInfo;
import org.chromattic.core.bean.PropertyInfo;
import org.chromattic.core.bean.MapPropertyInfo;
import org.chromattic.core.bean.MultiValuedPropertyInfo;
import org.chromattic.core.bean.SingleValuedPropertyInfo;
import org.chromattic.core.bean.ValueInfo;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.bean.SimpleType;
import org.chromattic.core.bean.BeanValueInfo;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.ClassIntrospector;
import org.reflext.api.AnnotationIntrospector;
import org.reflext.api.MethodInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.TypeInfo;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMappingBuilder {

  /** . */
  private final ClassTypeInfo javaClass;

  public TypeMappingBuilder(ClassTypeInfo javaClass) {
    this.javaClass = javaClass;
  }

  public TypeMapping build() {
    return _build();
  }

  private TypeMapping _build() {
    NodeMapping nodeMapping = javaClass.getDeclaredAnnotation(NodeMapping.class);
    if (nodeMapping == null) {
      throw new IllegalStateException("Class " + javaClass + " is not annotated ");
    }

    //
    String primaryNodeTypeName = nodeMapping.name();
    Set<PropertyMapping> propertyMappings = new HashSet<PropertyMapping>();
    Set<MethodMapping> methodMappings = new HashSet<MethodMapping>();
    BeanInfo info = new BeanInfo(javaClass);

    //
    Mixin mixin = new AnnotationIntrospector<Mixin>(Mixin.class).resolve(javaClass);
    Set<String> mixinNames = new HashSet<String>();
    if (mixin != null) {
      for (String mixinName : mixin.name()) {
        mixinNames.add(mixinName);
      }
    }

    // Property
    for (PropertyInfo propertyInfo : info.getProperties(Property.class)) {
      Property propertyAnnotation = propertyInfo.getAnnotation(Property.class);

      //
      ValueInfo value;
      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svp = (SingleValuedPropertyInfo)propertyInfo;
        value = svp.getValue();
      } else if (propertyInfo instanceof MultiValuedPropertyInfo) {
        MultiValuedPropertyInfo mvp = (MultiValuedPropertyInfo)propertyInfo;
        value = mvp.getElementValue();
      } else {
        throw new IllegalStateException();
      }

      //
      if (value instanceof SimpleValueInfo) {
        JCRPropertyMapping memberMapping = new JCRPropertyMapping(propertyAnnotation.name());
        SimpleMapping<JCRPropertyMapping> simpleMapping = new SimpleMapping<JCRPropertyMapping>(memberMapping);
        PropertyMapping<SimpleMapping<JCRPropertyMapping>> propertyMapping = new PropertyMapping<SimpleMapping<JCRPropertyMapping>>(propertyInfo, simpleMapping);
        propertyMappings.add(propertyMapping);
      } else {
        throw new IllegalStateException("Cannot map property type " + value);
      }
    }

    // Property map
    for (PropertyInfo propertyInfo : info.getProperties(Properties.class)) {
      Properties propertyAnnotation = propertyInfo.getAnnotation(Properties.class);
      if (propertyInfo instanceof MapPropertyInfo) {
        MapPropertyInfo mapPropertyInfo = (MapPropertyInfo)propertyInfo;
        PropertyMapMapping simpleMapping = new PropertyMapMapping();
        PropertyMapping<PropertyMapMapping> propertyMapping = new PropertyMapping<PropertyMapMapping>(mapPropertyInfo, simpleMapping);
        propertyMappings.add(propertyMapping);

      } else {
        throw new IllegalStateException();
      }
    }

    // Node attributes
    for (PropertyInfo propertyInfo : info.getProperties()) {
      NodeAttributeType nat = null;
      if (propertyInfo.getAnnotation(Name.class) != null) {
        nat = NodeAttributeType.NAME;
      } else if (propertyInfo.getAnnotation(Id.class) != null) {
        nat = NodeAttributeType.ID;
      } else if (propertyInfo.getAnnotation(Path.class) != null) {
        nat = NodeAttributeType.PATH;
      } else if (propertyInfo.getAnnotation(WorkspaceName.class) != null) {
        nat = NodeAttributeType.WORKSPACE_NAME;
      }
      if (nat != null) {
        if (propertyInfo instanceof SingleValuedPropertyInfo) {
          SingleValuedPropertyInfo svpi = (SingleValuedPropertyInfo)propertyInfo;
          ValueInfo vi = svpi.getValue();
          if (vi instanceof SimpleValueInfo) {
            SimpleValueInfo svi = (SimpleValueInfo)vi;
            JCRNodeAttributeMapping memberMapping = new JCRNodeAttributeMapping(nat);
            if (svi.getSimpleType() == SimpleType.STRING) {
              SimpleMapping<JCRNodeAttributeMapping> simpleMapping = new SimpleMapping<JCRNodeAttributeMapping>(memberMapping);
              PropertyMapping<SimpleMapping<JCRNodeAttributeMapping>> propertyMapping = new PropertyMapping<SimpleMapping<JCRNodeAttributeMapping>>(propertyInfo, simpleMapping);
              propertyMappings.add(propertyMapping);
            } else {
              throw new IllegalStateException();
            }
          } else {
            throw new IllegalStateException();
          }
        } else {
          throw new IllegalStateException();
        }
      }
    }

    // One to one
    for (PropertyInfo propertyInfo : info.getProperties(OneToOne.class)) {

      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svpi = (SingleValuedPropertyInfo)propertyInfo;
        ValueInfo vi = svpi.getValue();
        if (vi instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)vi;
          ClassTypeInfo typeInfo = bvi.getTypeInfo();
          OneToOne oneToOneAnn = propertyInfo.getAnnotation(OneToOne.class);

          // The mapped by of a one to one mapping discrimines between the parent and the child
          RelationshipMapping hierarchyMapping;
          MappedBy mappedBy = propertyInfo.getAnnotation(MappedBy.class);
          if (mappedBy != null) {
            hierarchyMapping = new NamedOneToOneMapping(typeInfo, mappedBy.value(), RelationshipType.HIERARCHIC, true);
          } else {
            RelatedMappedBy relatedMappedBy = propertyInfo.getAnnotation(RelatedMappedBy.class);
            if (relatedMappedBy != null) {
              hierarchyMapping = new NamedOneToOneMapping(typeInfo, relatedMappedBy.value(), RelationshipType.HIERARCHIC, false);
            } else {
              throw new IllegalStateException("No related by mapping found for property " + propertyInfo + " when introspecting " + info);
            }
          }
          PropertyMapping<RelationshipMapping> oneToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
          propertyMappings.add(oneToOneMapping);
        } else {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalStateException();
      }
    }

    // One to many
    for (PropertyInfo propertyInfo : info.getProperties(OneToMany.class)) {
      OneToMany oneToManyAnn = propertyInfo.getAnnotation(OneToMany.class);
      if (propertyInfo instanceof MultiValuedPropertyInfo) {
        MultiValuedPropertyInfo multiValuedProperty = (MultiValuedPropertyInfo)propertyInfo;

        //
        if (multiValuedProperty instanceof MapPropertyInfo) {
          MapPropertyInfo mapProperty = (MapPropertyInfo)multiValuedProperty;
          if (!(mapProperty.getKeyValue() instanceof SimpleValueInfo)) {
            throw new IllegalStateException("Wrong key value type " + mapProperty.getKeyValue());
          }
          SimpleValueInfo svi = (SimpleValueInfo)mapProperty.getKeyValue();
          if (svi.getSimpleType() != SimpleType.STRING) {
            throw new IllegalStateException();
          }
        }

        //
        ValueInfo beanElementType = multiValuedProperty.getElementValue();
        if (beanElementType instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)beanElementType;

          //
          OneToManyMapping mapping;
          RelationshipType type = oneToManyAnn.type();
          if (type == RelationshipType.HIERARCHIC) {
            MappedBy mappedBy = propertyInfo.getAnnotation(MappedBy.class);
            if (mappedBy != null) {
              throw new IllegalStateException();
            }
            mapping = new OneToManyMapping(bvi.getTypeInfo(), RelationshipType.HIERARCHIC);
          } else {
            RelatedMappedBy mappedBy = propertyInfo.getAnnotation(RelatedMappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            mapping = new NamedOneToManyMapping(bvi.getTypeInfo(), mappedBy.value(), type);
          }

          //
          PropertyMapping<OneToManyMapping> oneToManyMapping = new PropertyMapping<OneToManyMapping>(propertyInfo, mapping);
          propertyMappings.add(oneToManyMapping);
        }
      }
    }

    // Many to one
    for (PropertyInfo propertyInfo : info.getProperties(ManyToOne.class)) {
      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svpi = (SingleValuedPropertyInfo)propertyInfo;
        ValueInfo vi = svpi.getValue();
        if (vi instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)vi;

          //
          ManyToOne manyToOneAnn = propertyInfo.getAnnotation(ManyToOne.class);
          RelationshipType type = manyToOneAnn.type();

          //
          if (type == RelationshipType.HIERARCHIC) {
            RelationshipMapping hierarchyMapping = new ManyToOneMapping(bvi.getTypeInfo(), RelationshipType.HIERARCHIC);
            PropertyMapping<RelationshipMapping> manyToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(manyToOneMapping);
          } else {
            MappedBy mappedBy = propertyInfo.getAnnotation(MappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            NamedManyToOneMapping referenceMapping = new NamedManyToOneMapping(bvi.getTypeInfo(), mappedBy.value(), type);
            PropertyMapping<NamedManyToOneMapping> manyToOneMapping = new PropertyMapping<NamedManyToOneMapping>(propertyInfo, referenceMapping);
            propertyMappings.add(manyToOneMapping);
          }
        }
      } else {
        throw new IllegalStateException();
      }
    }

    //
    ClassIntrospector introspector = new ClassIntrospector(javaClass);

    // Create
    for (MethodInfo method : introspector.resolveMethods(Create.class)) {
      if (!method.isStatic()) {
        List<TypeInfo> parameterTypes = method.getParameterTypes();
        if (parameterTypes.size() < 2) {
          if (parameterTypes.size() == 1) {
            TypeInfo argTI = parameterTypes.get(0);
            if (argTI instanceof ClassTypeInfo) {
              ClassTypeInfo argCTI = (ClassTypeInfo)argTI;
              if (!argCTI.getName().equals(String.class.getName())) {
                throw new IllegalStateException();
              }
            } else {
              throw new IllegalStateException();
            }
          }
          ClassTypeInfo cti = (ClassTypeInfo)javaClass.resolve(method.getReturnType());
          methodMappings.add(new CreateMapping(method, cti));
        } else {
          throw new IllegalStateException();
        }
      }
    }

    // Destroy
    for (MethodInfo method : introspector.resolveMethods(Destroy.class)) {
      if (!method.isStatic()) {
        List<TypeInfo> parameterTypes = method.getParameterTypes();
        if (parameterTypes.size() != 0) {
          throw new IllegalStateException();
        }
        if (!(method.getReturnType() instanceof VoidTypeInfo)) {
          throw new IllegalStateException();
        }
        methodMappings.add(new DestroyMapping(method));
      }
    }

    // Find by id
    for (MethodInfo method : introspector.resolveMethods(FindById.class)) {
      if (!method.isStatic()) {
        List<TypeInfo> parameterTypes = method.getParameterTypes();
        if (parameterTypes.size() == 1) {
          TypeInfo argTI = parameterTypes.get(0);
          if (argTI instanceof ClassTypeInfo) {
            ClassTypeInfo argCTI = (ClassTypeInfo)argTI;
            if (argCTI.getName().equals(String.class.getName())) {
              ClassTypeInfo cti = (ClassTypeInfo)javaClass.resolve(method.getReturnType());
              methodMappings.add(new FindByIdMapping(method, cti));
            } else {
              throw new IllegalStateException();
            }
          } else {
            throw new IllegalStateException();
          }
        }
      }
    }

    //
    return new TypeMapping(
      javaClass,
      propertyMappings,
      methodMappings,
      primaryNodeTypeName,
      mixinNames);
  }
}