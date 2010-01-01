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

import org.chromattic.api.annotations.*;
import org.chromattic.api.RelationshipType;
import org.chromattic.api.NameConflictResolution;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.core.bean.*;
import org.chromattic.core.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.core.mapping.jcr.JCRPropertyMapping;
import org.chromattic.core.mapping.value.OneToOneMapping;
import org.chromattic.core.mapping.value.SimpleMapping;
import org.chromattic.core.mapping.value.NamedOneToOneMapping;
import org.chromattic.core.mapping.value.NamedManyToOneMapping;
import org.chromattic.core.mapping.value.RelationshipMapping;
import org.chromattic.core.mapping.value.ManyToOneMapping;
import org.chromattic.core.mapping.value.OneToManyMapping;
import org.chromattic.core.mapping.value.NamedOneToManyMapping;
import org.chromattic.core.mapping.value.PropertyMapMapping;
import org.chromattic.core.NodeAttributeType;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.AnnotationIntrospector;
import org.reflext.api.MethodInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.introspection.HierarchyScope;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMappingBuilder {

  /** . */
  private final BeanInfoFactory beanInfoBuilder;

  public TypeMappingBuilder(BeanInfoFactory beanInfoBuilder) {
    this.beanInfoBuilder = beanInfoBuilder;
  }

  public TypeMapping build(ClassTypeInfo javaClass) {
    return _build(javaClass);
  }

  private TypeMapping _build(ClassTypeInfo javaClass) {
    Set<PropertyMapping> propertyMappings = new HashSet<PropertyMapping>();
    Set<MethodMapping> methodMappings = new HashSet<MethodMapping>();
    BeanInfo info = beanInfoBuilder.build(javaClass);

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
        if (propertyInfo.getAnnotation(Property.class) == null) {
          // Check it's not a property
          nat = NodeAttributeType.PATH;
        }
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
            SimpleType simpleType = svi.getSimpleType();
            if (nat == NodeAttributeType.PATH) {
              if (simpleType.getKind() != BaseSimpleTypes.PATH) {
                throw new IllegalStateException("Type " + simpleType + " is not accepted for path attribute mapping");
              }
            } else {
              if (simpleType.getKind() != BaseSimpleTypes.STRING) {
                throw new IllegalStateException("Type " + simpleType + " is not accepted for attribute mapping");
              }
            }
            SimpleMapping<JCRNodeAttributeMapping> simpleMapping = new SimpleMapping<JCRNodeAttributeMapping>(memberMapping);
            PropertyMapping<SimpleMapping<JCRNodeAttributeMapping>> propertyMapping = new PropertyMapping<SimpleMapping<JCRNodeAttributeMapping>>(propertyInfo, simpleMapping);
            propertyMappings.add(propertyMapping);
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
          RelationshipType type = oneToOneAnn.type();

          //
          PropertyMapping<RelationshipMapping> oneToOneMapping;
          if (type == RelationshipType.HIERARCHIC) {
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
            oneToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(oneToOneMapping);
          } else if (type == RelationshipType.MIXIN) {
            OneToOneMapping embeddedMapping = new OneToOneMapping(typeInfo, RelationshipType.MIXIN);
            PropertyMapping<OneToOneMapping> a = new PropertyMapping<OneToOneMapping>(propertyInfo, embeddedMapping);
            propertyMappings.add(a);
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
          SimpleValueInfo<?> svi = (SimpleValueInfo)mapProperty.getKeyValue();
          if (svi.getSimpleType().getKind() != BaseSimpleTypes.STRING) {
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
        } else {
          throw new IllegalStateException();
        }
      } else {
        throw new IllegalStateException();
      }
    }

    //
    MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL);

    // Create
    for (MethodInfo method : introspector.resolveMethods(javaClass, Create.class)) {
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
    for (MethodInfo method : introspector.resolveMethods(javaClass, Destroy.class)) {
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
    for (MethodInfo method : introspector.resolveMethods(javaClass, FindById.class)) {
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
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NamingPolicy namingPolicy = new AnnotationIntrospector<NamingPolicy>(NamingPolicy.class).resolve(javaClass);
    if (namingPolicy != null) {
      onDuplicate = namingPolicy.onDuplicate();
    }

    //
    NodeMapping nodeMapping = javaClass.getDeclaredAnnotation(NodeMapping.class);
    MixinMapping mixin = javaClass.getDeclaredAnnotation(MixinMapping.class);
    String[] mixinNames = mixin != null ? mixin.name() : new String[0];

    //
    if (nodeMapping == null) {
      if  (mixin == null) {
        throw new IllegalStateException("Class " + javaClass + " is not annotated ");
      }
      if (mixinNames.length == 0) {
        throw new IllegalStateException("Class " + javaClass + " is annotated with @Mixin but does not contain any name");
      }
      if (mixinNames.length > 1) {
        throw new IllegalStateException("Class " + javaClass + " is annotated with @Mixin but contains more than one name");
      }

      //
      return new MixinNodeTypeMapping(
        javaClass,
        propertyMappings,
        methodMappings,
        onDuplicate,
        mixinNames[0]);
    } else {
      String nodeTypeName = nodeMapping.name();

      //
      FormattedBy formattedBy = info.getAnnotation(FormattedBy.class);
      Class<? extends ObjectFormatter> formatter = formattedBy != null ? formattedBy.value() : null;

      //
      return new PrimaryNodeTypeMapping(
        javaClass,
        propertyMappings,
        methodMappings,
        onDuplicate,
        nodeTypeName,
        formatter);
    }
  }
}