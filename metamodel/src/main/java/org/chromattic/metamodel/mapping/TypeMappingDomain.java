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
import org.chromattic.metamodel.bean.*;
import org.chromattic.metamodel.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.mapping.value.*;
import org.reflext.api.*;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.lang.annotation.Annotation;
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
      Map<String, NodeTypeMapping> addedMappings = new HashMap<String, NodeTypeMapping>();

      //
      resolve(JLOTypeInfo.get(), addedMappings);

      //
      for (ClassTypeInfo cti : types) {
        if (!cti.getName().equals(Object.class.getName())) {
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
      }

      // Remove root type as we don't want it to appear as first class  
      addedMappings.remove(Object.class.getName());

      //
      this.mappings.clear();
      this.mappings.putAll(addedMappings);
    }
  }

  private static <V> JCRPropertyMapping<V> createProperty(String name, SimpleType<V> type, String[] defaultValue) {
    List<V> defaultValueList = null;
    if (defaultValue.length > 0) {
      defaultValueList = new ArrayList<V>(defaultValue.length);
      for (String value : defaultValue) {
        V v = type.toExternal(value);
        defaultValueList.add(v);
      }
      defaultValueList = Collections.unmodifiableList(defaultValueList);
    }
    return new JCRPropertyMapping<V>(name, defaultValueList);
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
        NodeTypeKind.PRIMARY,
        false);
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
        mixinName,
        false);
    } else {
      String nodeTypeName = primaryType.name();
      boolean orderable = primaryType.orderable();

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
        formatter,
        orderable);
    }

    // Add it to added map
    addedMappings.put(javaClass.getName(), nodeTypeMapping);

    // Property
    for (AnnotatedProperty<Property> annotatedProperty : info.findAnnotatedProperties(Property.class)) {
      Property propertyAnnotation = annotatedProperty.getAnnotation();
      PropertyInfo propertyInfo = annotatedProperty.getProperty();

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
      SimpleValueInfo simpleValue;
      if (value instanceof SimpleValueInfo) {
        simpleValue = (SimpleValueInfo)value;
      } else {
        throw new InvalidMappingException(javaClass, "Cannot map property type " + value);
      }

      //
      String[] defaultValues = propertyAnnotation.defaultValue();
      SimpleType<?> simpleType = simpleValue.getSimpleType();
      JCRPropertyMapping<?> memberMapping = createProperty(propertyAnnotation.name(), simpleType, defaultValues);
      SimpleMapping<JCRPropertyMapping> simpleMapping = new SimpleMapping<JCRPropertyMapping>(annotatedProperty.getOwner(), memberMapping);
      PropertyMapping<SimpleMapping<JCRPropertyMapping>> propertyMapping = new PropertyMapping<SimpleMapping<JCRPropertyMapping>>(propertyInfo, simpleMapping);
      propertyMappings.add(propertyMapping);
    }

    // Property map
    for (AnnotatedProperty<Properties> annotatedProperty : info.findAnnotatedProperties(Properties.class)) {
      PropertyInfo<?> propertyInfo = annotatedProperty.getProperty();
      if (propertyInfo instanceof MapPropertyInfo) {
        MapPropertyInfo mapPropertyInfo = (MapPropertyInfo)propertyInfo;
        PropertyMapMapping simpleMapping = new PropertyMapMapping(annotatedProperty.getOwner());
        PropertyMapping<PropertyMapMapping> propertyMapping = new PropertyMapping<PropertyMapMapping>(mapPropertyInfo, simpleMapping);
        propertyMappings.add(propertyMapping);
      } else {
        throw new IllegalStateException();
      }
    }

    // Node attributes
    for (PropertyInfo<?> propertyInfo : info.getProperties()) {
      Collection<AnnotatedProperty<?>> annotations = propertyInfo.getAnnotateds(
        Name.class,
        Id.class,
        Path.class,
        WorkspaceName.class);
      if (annotations.size() > 0) {
        AnnotatedProperty<?> annotated;
        if (annotations.size() > 1) {
          throw new InvalidMappingException(javaClass, "Too many annotations of the same kind " + annotations);
        } else {
          annotated = annotations.iterator().next();
        }
        NodeAttributeType nat;
        Annotation annotation = annotated.getAnnotation();
        if (annotation instanceof Name) {
          nat = NodeAttributeType.NAME;
        } else if (annotation instanceof Id) {
          nat = NodeAttributeType.ID;
        } else if (annotation instanceof Path) {
          nat = NodeAttributeType.PATH;
        } else if (annotation instanceof WorkspaceName) {
          nat = NodeAttributeType.WORKSPACE_NAME;
        } else {
          throw new AssertionError();
        }
/*
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
*/
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
            SimpleMapping<JCRNodeAttributeMapping> simpleMapping = new SimpleMapping<JCRNodeAttributeMapping>(annotated.getOwner(), memberMapping);
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
    for (AnnotatedProperty<OneToOne> annotatedProperty : info.findAnnotatedProperties(OneToOne.class)) {
      PropertyInfo<?> propertyInfo = annotatedProperty.getProperty();
      OneToOne oneToOneAnn = annotatedProperty.getAnnotation();
      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svpi = (SingleValuedPropertyInfo)propertyInfo;
        ValueInfo vi = svpi.getValue();
        if (vi instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)vi;
          ClassTypeInfo typeInfo = bvi.getTypeInfo();
          RelationshipType type = oneToOneAnn.type();

          //
          PropertyMapping<RelationshipMapping> oneToOneMapping;
          if (type == RelationshipType.HIERARCHIC) {
            // The mapped by of a one to one mapping discrimines between the parent and the child
            RelationshipMapping hierarchyMapping;
            AnnotatedProperty<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
            if (mappedBy != null) {
              NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
              hierarchyMapping = new NamedOneToOneMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), RelationshipType.HIERARCHIC, true);
            } else {
              AnnotatedProperty<RelatedMappedBy> relatedMappedBy = propertyInfo.getAnnotated(RelatedMappedBy.class);
              if (relatedMappedBy != null) {
                NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
                hierarchyMapping = new NamedOneToOneMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, relatedMappedBy.getAnnotation().value(), RelationshipType.HIERARCHIC, false);
              } else {
                throw new IllegalStateException("No related by mapping found for property " + propertyInfo + " when introspecting " + info);
              }
            }
            oneToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(oneToOneMapping);
          } else if (type == RelationshipType.EMBEDDED) {
            NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
            OneToOneMapping embeddedMapping = new OneToOneMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, RelationshipType.EMBEDDED);
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
    for (AnnotatedProperty<OneToMany> annotatedProperty : info.findAnnotatedProperties(OneToMany.class)) {
      PropertyInfo<?> propertyInfo = annotatedProperty.getProperty();
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
          RelationshipType type = annotatedProperty.getAnnotation().type();
          if (type == RelationshipType.HIERARCHIC) {
            AnnotatedProperty<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
            if (mappedBy != null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            OneToManyMapping mapping = new OneToManyMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, RelationshipType.HIERARCHIC);
            PropertyMapping<OneToManyMapping> oneToManyMapping = new PropertyMapping<OneToManyMapping>(propertyInfo, mapping);
            propertyMappings.add(oneToManyMapping);
          } else {
            AnnotatedProperty<RelatedMappedBy> mappedBy = propertyInfo.getAnnotated(RelatedMappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            NamedOneToManyMapping mapping = new NamedOneToManyMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), type);
            PropertyMapping<NamedOneToManyMapping> oneToManyMapping = new PropertyMapping<NamedOneToManyMapping>(propertyInfo, mapping);
            propertyMappings.add(oneToManyMapping);
          }
        }
      }
    }

    // Many to one
    for (AnnotatedProperty<ManyToOne> annotatedProperty : info.findAnnotatedProperties(ManyToOne.class)) {
      PropertyInfo<?> propertyInfo = annotatedProperty.getProperty();
      if (propertyInfo instanceof SingleValuedPropertyInfo) {
        SingleValuedPropertyInfo svpi = (SingleValuedPropertyInfo)propertyInfo;
        ValueInfo vi = svpi.getValue();
        if (vi instanceof BeanValueInfo) {
          BeanValueInfo bvi = (BeanValueInfo)vi;

          //
          RelationshipType type = annotatedProperty.getAnnotation().type();

          //
          if (type == RelationshipType.HIERARCHIC) {
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            RelationshipMapping hierarchyMapping = new ManyToOneMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, RelationshipType.HIERARCHIC);
            PropertyMapping<RelationshipMapping> manyToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
            propertyMappings.add(manyToOneMapping);
          } else {
            AnnotatedProperty<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
            if (mappedBy == null) {
              throw new IllegalStateException();
            }
            NodeTypeMapping relatedMapping = resolve(bvi.getTypeInfo(), addedMappings);
            NamedManyToOneMapping referenceMapping = new NamedManyToOneMapping(annotatedProperty.getOwner(), nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), type);
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
