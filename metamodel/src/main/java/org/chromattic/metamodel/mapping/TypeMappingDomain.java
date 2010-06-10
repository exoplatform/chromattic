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
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.metamodel.bean.*;
import org.chromattic.metamodel.bean.value.MapValueInfo;
import org.chromattic.metamodel.bean.value.MultiValueInfo;
import org.chromattic.metamodel.bean.value.TypeKind;
import org.chromattic.metamodel.bean.value.ValueInfo;
import org.chromattic.metamodel.mapping.jcr.NodeDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.NodeTypeDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.mapping.value.*;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.chromattic.metamodel.type.SimpleTypeMapping;
import org.reflext.api.*;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.AnnotationTarget;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMappingDomain {

  /** . */
  private static final AnnotationType<NamingPolicy, ?> NAMING_POLICY = AnnotationType.get(NamingPolicy.class);

  /** . */
  private static final AnnotationType<PrimaryType, ?> PRIMARY_TYPE = AnnotationType.get(PrimaryType.class);

  /** . */
  private static final AnnotationType<MixinType, ?> MIXIN_TYPE = AnnotationType.get(MixinType.class);

  /** . */
  private static final AnnotationType<Create, ?> CREATE = AnnotationType.get(Create.class);

  /** . */
  private static final AnnotationType<Destroy, ?> DESTROY = AnnotationType.get(Destroy.class);

  /** . */
  private static final AnnotationType<FindById, ?> FIND_BY_ID = AnnotationType.get(FindById.class);

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

  /** . */
  private final SimpleTypeResolver typeResolver;

  public TypeMappingDomain(boolean processFormatter) {
    this(new SimpleTypeResolver(), processFormatter);
  }

  public TypeMappingDomain(SimpleTypeResolver typeResolver, boolean processFormatter) {
    this.mappings = new HashMap<String, NodeTypeMapping>();
    this.beanInfoBuilder = new BeanInfoFactory(typeResolver);
    this.processFormatter = processFormatter;
    this.types = new HashSet<ClassTypeInfo>();
    this.resolved = false;
    this.typeResolver = typeResolver;
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

  private static <V> PropertyDefinitionMapping createProperty(
    String name,
    PropertyMetaType jcrType,
    String[] defaultValue) {

    //
    List<String> defaultValueList = null;
    if (defaultValue != null) {
      defaultValueList = new ArrayList<String>(defaultValue.length);
      defaultValueList.addAll(Arrays.asList(defaultValue));
      defaultValueList = Collections.unmodifiableList(defaultValueList);
    }

    //
    return new PropertyDefinitionMapping(name, jcrType, defaultValueList);
  }

  private NodeTypeMapping resolve(ClassTypeInfo javaClass, Map<String, NodeTypeMapping> addedMappings) {
    if (javaClass.getName().equals(Object.class.getName())) {
      NodeTypeMapping objectMapping = new NodeTypeMapping(
        this,
        javaClass,
        Collections.<PropertyMapping<? extends ValueMapping>>emptySet(),
        Collections.<MethodMapping>emptySet(),
        NameConflictResolution.FAIL,
        null,
        new NodeTypeDefinitionMapping("nt:base", NodeTypeKind.PRIMARY, false, true));
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
    AnnotationTarget<?, NamingPolicy> namingPolicy = new AnnotationIntrospector<NamingPolicy>(NAMING_POLICY).resolve(javaClass);
    if (namingPolicy != null) {
      onDuplicate = namingPolicy.getAnnotation().onDuplicate();
    }

    //
    PrimaryType primaryType = javaClass.getDeclaredAnnotation(PRIMARY_TYPE);

    //
    if (primaryType == null) {
      MixinType mixinType = javaClass.getDeclaredAnnotation(MIXIN_TYPE);

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
        new NodeTypeDefinitionMapping(mixinName, NodeTypeKind.MIXIN, false, true));
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
        formatter,
        new NodeTypeDefinitionMapping(nodeTypeName, NodeTypeKind.PRIMARY, orderable, primaryType.abstract_()));
    }

    // Add it to added map
    addedMappings.put(javaClass.getName(), nodeTypeMapping);

    //
    for (PropertyQualifier<?> propertyInfo : info.getProperties()) {

      PropertyRole role = propertyInfo.getRole();

      ValueInfo value = propertyInfo.getValue();

      if (role != null) {
        NodeTypeMapping definer = resolve(role.getDeclaringType(), addedMappings);

        //
        if (role instanceof PropertyRole.Property) {
          PropertyRole.Property roleProperty = (PropertyRole.Property)role;


          //
          if (value instanceof MultiValueInfo) {
            value = ((MultiValueInfo)value).getElement();
          }

          //
          if (value.getKind() != TypeKind.SIMPLE) {
            throw new InvalidMappingException(javaClass, "Cannot map property type " + value);
          }

          //
          String[] defaultValues = null;
          AnnotatedPropertyQualifier<DefaultValue> defaultValueAnnotated = propertyInfo.getAnnotated(DefaultValue.class);
          if (defaultValueAnnotated != null) {
            DefaultValue defaultValueAnnotation = defaultValueAnnotated.getAnnotation();
            defaultValues = defaultValueAnnotation.value();
          }

          // Determine mapping
          PropertyMetaType<?> propertyMetaType = PropertyMetaType.get(roleProperty.type);

          //
          SimpleTypeMapping abc = typeResolver.resolveType(value.getTypeInfo(), propertyMetaType);
          if (abc == null) {
            throw new InvalidMappingException(javaClass, "No simple type mapping for " + value.getTypeInfo());
          }

          //
          if (propertyMetaType == null) {
            propertyMetaType = abc.getPropertyMetaType();
          }

          //
          PropertyDefinitionMapping memberMapping = createProperty(
            roleProperty.name,
            propertyMetaType,
            defaultValues);
          SimpleMapping<PropertyDefinitionMapping> simpleMapping = new SimpleMapping<PropertyDefinitionMapping>(definer, memberMapping);
          PropertyMapping<SimpleMapping<PropertyDefinitionMapping>> propertyMapping = new PropertyMapping<SimpleMapping<PropertyDefinitionMapping>>(propertyInfo, simpleMapping);
          propertyMappings.add(propertyMapping);
        } else if (role instanceof PropertyRole.Properties) {
          if (value instanceof MapValueInfo) {
            MapValueInfo mvi = (MapValueInfo)value;
            ValueInfo keyVI = mvi.getKey();
            SimpleTypeMapping keyTM = typeResolver.resolveType(keyVI.getTypeInfo());
            if (keyTM == null) {
              throw new InvalidMappingException(javaClass, "No simple type mapping for map key " + keyVI.getTypeInfo());
            }
            if (keyTM.getPropertyMetaType() != PropertyMetaType.STRING) {
              throw new InvalidMappingException(javaClass, "Map key " + keyVI.getTypeInfo() + " is not mapped to string type");
            }
            ValueInfo valueVI = mvi.getElement();
            SimpleTypeMapping valueTM = typeResolver.resolveType(valueVI.getTypeInfo());
            PropertyMapMapping simpleMapping = new PropertyMapMapping(definer, keyTM, valueTM);
            PropertyMapping<PropertyMapMapping> propertyMapping = new PropertyMapping<PropertyMapMapping>(propertyInfo, simpleMapping);
            propertyMappings.add(propertyMapping);
          } else {
            throw new InvalidMappingException(javaClass, "The property annotated with @Properties must inherit from Map<String, ?>");
          }
        } else if (role instanceof PropertyRole.Attribute) {
          PropertyRole.Attribute attributeRole = (PropertyRole.Attribute)role;
          if (value.getKind() == TypeKind.SIMPLE) {
            TypeInfo simpleType = value.getTypeInfo();
            if (simpleType instanceof ClassTypeInfo && ((ClassTypeInfo)simpleType).getName().equals(String.class.getName())) {
              PropertyMapping<AttributeValueMapping> propertyMapping = new PropertyMapping<AttributeValueMapping>(
                propertyInfo,
                new AttributeValueMapping(definer, attributeRole.type));
              propertyMappings.add(propertyMapping);
            } else {
              throw new InvalidMappingException(javaClass, "Type " + simpleType + " is not accepted for path attribute mapping");
            }
          } else {
            throw new InvalidMappingException(javaClass);
          }
        } else if (role instanceof PropertyRole.Relationship) {
          PropertyRole.Relationship relationshipRole = (PropertyRole.Relationship)role;
          RelationshipType type = relationshipRole.type;
          if (relationshipRole instanceof PropertyRole.OneToOne) {
            PropertyRole.OneToOne oneToOneRole = (PropertyRole.OneToOne)relationshipRole;
            if (value.getKind() == TypeKind.BEAN) {
              ClassTypeInfo typeInfo = (ClassTypeInfo)value.getTypeInfo();

              //
              PropertyMapping<RelationshipMapping> oneToOneMapping;
              if (type == RelationshipType.HIERARCHIC) {
                // The mapped by of a one to one mapping discrimines between the parent and the child
                RelationshipMapping hierarchyMapping;
                AnnotatedPropertyQualifier<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);

                //
                if (mappedBy == null)
                {
                  throw new InvalidMappingException(javaClass, "Annotated one to one hierarchic relationship " +
                    "must be annotated by " + MappedBy.class.getName());
                }

                //
                NodeDefinitionMapping jcrMapping = new NodeDefinitionMapping(oneToOneRole.getOptions());
                AnnotatedPropertyQualifier<Owner> owner = propertyInfo.getAnnotated(Owner.class);
                if (owner != null) {
                  NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
                  hierarchyMapping = new NamedOneToOneMapping(definer, nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), true, jcrMapping);
                } else {
                  NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
                  hierarchyMapping = new NamedOneToOneMapping(definer, nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), false, jcrMapping);
                }
                oneToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
                propertyMappings.add(oneToOneMapping);
              } else if (type == RelationshipType.EMBEDDED) {
                AnnotatedPropertyQualifier<Owner> owner = propertyInfo.getAnnotated(Owner.class);
                boolean owning = owner != null;
                NodeTypeMapping relatedMapping = resolve(typeInfo, addedMappings);
                OneToOneMapping embeddedMapping = new OneToOneMapping(definer, nodeTypeMapping, relatedMapping, RelationshipType.EMBEDDED, owning);
                PropertyMapping<OneToOneMapping> a = new PropertyMapping<OneToOneMapping>(propertyInfo, embeddedMapping);
                propertyMappings.add(a);
              } else {
                throw new IllegalStateException();
              }
            } else {
              throw new IllegalStateException();
            }
          } else if (relationshipRole instanceof PropertyRole.OneToMany) {
            if (value instanceof MultiValueInfo) {
              MultiValueInfo multiValue = (MultiValueInfo)value;

              //
              if (multiValue instanceof MapValueInfo) {
                MapValueInfo mapValue = (MapValueInfo)multiValue;
                if (mapValue.getKey().getKind() != TypeKind.SIMPLE) {
                  throw new IllegalStateException("Wrong key value type " + mapValue.getKey());
                }
                ValueInfo svi = mapValue.getKey();
                TypeInfo ti = svi.getTypeInfo();
                if (!(ti instanceof ClassTypeInfo) || !((ClassTypeInfo)ti).getName().equals(String.class.getName())) {
                  throw new InvalidMappingException(javaClass);
                }
              }

              //
              ValueInfo beanElementType = multiValue.getElement();
              if (beanElementType.getKind() == TypeKind.BEAN) {
                if (type == RelationshipType.HIERARCHIC) {
                  AnnotatedPropertyQualifier<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
                  if (mappedBy != null) {
                    throw new IllegalStateException();
                  }
                  NodeTypeMapping relatedMapping = resolve((ClassTypeInfo)beanElementType.getTypeInfo(), addedMappings);
                  OneToManyMapping mapping = new OneToManyMapping(
                    definer,
                    nodeTypeMapping,
                    relatedMapping,
                    RelationshipType.HIERARCHIC);
                  PropertyMapping<OneToManyMapping> oneToManyMapping = new PropertyMapping<OneToManyMapping>(propertyInfo, mapping);
                  propertyMappings.add(oneToManyMapping);
                } else if (type == RelationshipType.REFERENCE || type == RelationshipType.PATH) {
                  AnnotatedPropertyQualifier<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
                  if (mappedBy == null) {
                    throw new InvalidMappingException(javaClass, "By reference or by path one to many must be annotated with " + MappedBy.class.getName());
                  }
                  NodeTypeMapping relatedMapping = resolve((ClassTypeInfo)beanElementType.getTypeInfo(), addedMappings);
                  NamedOneToManyMapping mapping = new NamedOneToManyMapping(definer, nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), type);
                  PropertyMapping<NamedOneToManyMapping> oneToManyMapping = new PropertyMapping<NamedOneToManyMapping>(propertyInfo, mapping);
                  propertyMappings.add(oneToManyMapping);
                }
              }
            }
          } else if (relationshipRole instanceof PropertyRole.ManyToOne) {
            if (value.getKind() == TypeKind.BEAN) {
              if (type == RelationshipType.HIERARCHIC) {
                NodeTypeMapping relatedMapping = resolve((ClassTypeInfo)value.getTypeInfo(), addedMappings);
                RelationshipMapping hierarchyMapping = new ManyToOneMapping(definer, nodeTypeMapping, relatedMapping, RelationshipType.HIERARCHIC);
                PropertyMapping<RelationshipMapping> manyToOneMapping = new PropertyMapping<RelationshipMapping>(propertyInfo, hierarchyMapping);
                propertyMappings.add(manyToOneMapping);
              } else {
                AnnotatedPropertyQualifier<MappedBy> mappedBy = propertyInfo.getAnnotated(MappedBy.class);
                if (mappedBy == null) {
                  throw new IllegalStateException();
                }
                NodeTypeMapping relatedMapping = resolve((ClassTypeInfo)value.getTypeInfo(), addedMappings);
                NamedManyToOneMapping referenceMapping = new NamedManyToOneMapping(definer, nodeTypeMapping, relatedMapping, mappedBy.getAnnotation().value(), type);
                PropertyMapping<NamedManyToOneMapping> manyToOneMapping = new PropertyMapping<NamedManyToOneMapping>(propertyInfo, referenceMapping);
                propertyMappings.add(manyToOneMapping);
              }
            } else {
              throw new IllegalStateException();
            }
          }
        }
      }
    }

    //
    if (!nodeTypeMapping.isAbstract()) {
      MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL);

      // Create
      for (AnnotationTarget<MethodInfo, Create> annotatedMethods : introspector.resolveMethods(javaClass, CREATE)) {
        MethodInfo method = annotatedMethods.getTarget();
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
            TypeInfo returnTypeInfo = javaClass.resolve(method.getReturnType());
            if (returnTypeInfo instanceof ClassTypeInfo) {
              methodMappings.add(new CreateMapping(method, (ClassTypeInfo)returnTypeInfo));
            } else {
              throw new InvalidMappingException(javaClass, "Invalid " + method + " method return type " + returnTypeInfo);
            }
          } else {
            throw new IllegalStateException();
          }
        }
      }

      // Destroy
      for (AnnotationTarget<MethodInfo, Destroy> annotatedMethods : introspector.resolveMethods(javaClass, DESTROY)) {
        MethodInfo method = annotatedMethods.getTarget();
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
      for (AnnotationTarget<MethodInfo, FindById> annotatedMethods : introspector.resolveMethods(javaClass, FIND_BY_ID)) {
        MethodInfo method = annotatedMethods.getTarget();
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
    } else {
      // todo find some better checks than doing nothing
    }

    //
    mappings.put(javaClass.getName(), nodeTypeMapping);

    //
    return nodeTypeMapping;
  }
}
