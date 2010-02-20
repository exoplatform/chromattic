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

package org.chromattic.metamodel.mapping;

import org.chromattic.api.NameConflictResolution;
import org.chromattic.api.RelationshipType;
import org.chromattic.api.annotations.*;
import org.chromattic.api.annotations.Properties;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.metamodel.MetaModelException;
import org.chromattic.metamodel.bean.*;
import org.chromattic.metamodel.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.mapping.value.*;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMappingDomain {

  /** . */
  private final boolean processFormatter;

  /** . */
  private final Map<String, NodeTypeMapping> mappings;

  /** . */
  private final Set<ClassTypeInfo> types;

  /** . */
  private final BeanInfoFactory beanInfoBuilder;

  /** . */
  private boolean resolved;

  public TypeMappingDomain(boolean processFormatter) {
    this.mappings = new HashMap<String, NodeTypeMapping>();
    this.beanInfoBuilder = new BeanInfoFactory();
    this.processFormatter = processFormatter;
    this.types = new HashSet<ClassTypeInfo>();
    this.resolved = false;
  }

  public NodeTypeMapping get(ClassTypeInfo type) {
    return mappings.get(type.getName());
  }

  public void add(ClassTypeInfo javaClass) {

    types.add(javaClass);
    resolved = false;
  }

  public Collection<NodeTypeMapping> build() {
    resolve();
    return mappings.values();
  }

  public void resolve() {
    if (!resolved) {
      ClassTypeInfo rootType = null;
      for (ClassTypeInfo cti : types) {
        if (cti.getName().equals(Object.class.getName())) {
          rootType = cti;
          break;
        }
      }

      //
      if (rootType == null) {
        throw new MetaModelException("The type domain must contain the java.lang.Object type");
      }

      //
      Map<String, NodeTypeMapping> addedMappings = new HashMap<String, NodeTypeMapping>();
      for (ClassTypeInfo cti : types) {
        try {
          resolve(cti, addedMappings);
        }
        catch (Exception e) {
          if (e instanceof InvalidMappingException) {
            InvalidMappingException ime = (InvalidMappingException)e;
          if (ime.getType().equals(cti)) {
            throw ime;
          }          }
          throw new InvalidMappingException(cti, e);
         }
      }

      // Remove root type as we don't want it to appear as first class  
      addedMappings.remove(rootType.getName());

      //
      this.mappings.clear();
      this.mappings.putAll(addedMappings);
    }
  }

  private NodeTypeMapping resolve(ClassTypeInfo javaClass, Map<String, NodeTypeMapping> addedMappings) {
    if (javaClass.getName().equals(Object.class.getName())) {
      NodeTypeMapping objectMapping = new NodeTypeMapping(
        this,
        javaClass,
        Collections.<PropertyMapping<? extends ValueMapping>>emptySet(),
        Collections.<MethodMapping>emptySet(),
        NameConflictResolution.FAIL,
        "nt:base",
        null,
        NodeTypeKind.PRIMARY);
      addedMappings.put(javaClass.getName(), objectMapping);
      return objectMapping;
    }

    NodeTypeMapping nodeTypeMapping = addedMappings.get(javaClass.getName());
    if (nodeTypeMapping != null) {
      return nodeTypeMapping;
    }

    //
    Set<PropertyMapping<? extends ValueMapping>> propertyMappings = new HashSet<PropertyMapping<? extends ValueMapping>>();
    Set<MethodMapping> methodMappings = new HashSet<MethodMapping>();
    BeanInfo info = beanInfoBuilder.build(javaClass);

    //
    NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
    NamingPolicy namingPolicy = new AnnotationIntrospector<NamingPolicy>(NamingPolicy.class).resolve(javaClass);
    if (namingPolicy != null) {
      onDuplicate = namingPolicy.onDuplicate();
    }

    //
    PrimaryType primaryType = javaClass.getDeclaredAnnotation(PrimaryType.class);

    //
    if (primaryType == null) {
      MixinType mixinType = javaClass.getDeclaredAnnotation(MixinType.class);

      //
      if (mixinType == null) {
        throw new InvalidMappingException(javaClass, "Class is not annotated");
      }

      //
      String mixinName = mixinType != null ? mixinType.name() : null;

      //
      nodeTypeMapping = NodeTypeMapping.createMixinType(
        this,
        javaClass,
        propertyMappings,
        methodMappings,
        onDuplicate,
        mixinName);
    } else {
      String nodeTypeName = primaryType.name();

      //
      Class<? extends ObjectFormatter> formatter = null;
      if (processFormatter) {
        FormattedBy formattedBy = info.getAnnotation(FormattedBy.class);
        formatter = formattedBy != null ? formattedBy.value() : null;
      }

      //
      nodeTypeMapping =  NodeTypeMapping.createPrimaryType(
        this,
        javaClass,
        propertyMappings,
        methodMappings,
        onDuplicate,
        nodeTypeName,
        formatter);
    }

    // Add it to added map
    addedMappings.put(javaClass.getName(), nodeTypeMapping);

    // Property
    for (PropertyInfo<?> propertyInfo : info.getProperties(Property.class)) {
      Property propertyAnnotation = propertyInfo.getAnnotation(Property.class);

      //
      ValueInfo value;
      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svp = (SingleValuedPropertyInfo)propertyInfo;
        value = svp.getValue();
      } else if (propertyInfo instanceof MultiValuedPropertyInfo) {
        MultiValuedPropertyInfo mvp = (MultiValuedPropertyInfo)propertyInfo;
        value = mvp.getValue();
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
    for (PropertyInfo<?> propertyInfo : info.getProperties(Properties.class)) {
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
    for (PropertyInfo<?> propertyInfo : info.getProperties()) {
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
              if (simpleType != SimpleType.PATH) {
                throw new IllegalStateException("Type " + simpleType + " is not accepted for path attribute mapping");
              }
            } else {
              if (simpleType != SimpleType.STRING) {
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
    for (PropertyInfo<?> propertyInfo : info.getProperties(OneToOne.class)) {

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
              NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
              hierarchyMapping = new NamedOneToOneMapping(nodeTypeMapping, relatedMapping, mappedBy.value(), RelationshipType.HIERARCHIC, true);
            } else {
              RelatedMappedBy relatedMappedBy = propertyInfo.getAnnotation(RelatedMappedBy.class);
              if (relatedMappedBy != null) {
                NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
                hierarchyMapping = new NamedOneToOneMapping(nodeTypeMapping, relatedMapping, relatedMappedBy.value(), RelationshipType.HIERARCHIC, false);
              } else {
                throw new IllegalStateException("No related by mapping found for property " + propertyInfo + " when introspecting " + info);
              }
            }
            oneToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(oneToOneMapping);
          } else if (type == RelationshipType.EMBEDDED) {
            NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
            OneToOneMapping embeddedMapping = new OneToOneMapping(nodeTypeMapping, relatedMapping, RelationshipType.EMBEDDED);
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
    for (PropertyInfo<?> propertyInfo : info.getProperties(OneToMany.class)) {
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
          if (svi.getSimpleType() != SimpleType.STRING) {
            throw new IllegalStateException();
          }
        }

        //
        ValueInfo beanElementType = multiValuedProperty.getValue();
        if (beanElementType instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)beanElementType;

          //
          RelationshipType type = oneToManyAnn.type();
          if (type == RelationshipType.HIERARCHIC) {
            MappedBy mappedBy = propertyInfo.getAnnotation(MappedBy.class);
            if (mappedBy != null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            OneToManyMapping mapping = new OneToManyMapping(nodeTypeMapping, relatedMapping, RelationshipType.HIERARCHIC);
            PropertyMapping<OneToManyMapping> oneToManyMapping = new PropertyMapping<OneToManyMapping>(propertyInfo, mapping);
            propertyMappings.add(oneToManyMapping);
          } else {
            RelatedMappedBy mappedBy = propertyInfo.getAnnotation(RelatedMappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            NamedOneToManyMapping mapping = new NamedOneToManyMapping(nodeTypeMapping, relatedMapping, mappedBy.value(), type);
            PropertyMapping<NamedOneToManyMapping> oneToManyMapping = new PropertyMapping<NamedOneToManyMapping>(propertyInfo, mapping);
            propertyMappings.add(oneToManyMapping);
          }
        }
      }
    }

    // Many to one
    for (PropertyInfo<?> propertyInfo : info.getProperties(ManyToOne.class)) {
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
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            RelationshipMapping hierarchyMapping = new ManyToOneMapping(nodeTypeMapping, relatedMapping, RelationshipType.HIERARCHIC);
            PropertyMapping<RelationshipMapping> manyToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(manyToOneMapping);
          } else {
            MappedBy mappedBy = propertyInfo.getAnnotation(MappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            NamedManyToOneMapping referenceMapping = new NamedManyToOneMapping(nodeTypeMapping, relatedMapping, mappedBy.value(), type);
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
    mappings.put(javaClass.getName(), nodeTypeMapping);

    //
    return nodeTypeMapping;
  }
}
