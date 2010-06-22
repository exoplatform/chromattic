/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.mapping2;

import org.chromattic.api.AttributeOption;
import org.chromattic.api.NameConflictResolution;
import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.DefaultValue;
import org.chromattic.api.annotations.Destroy;
import org.chromattic.api.annotations.FindById;
import org.chromattic.api.annotations.FormattedBy;
import org.chromattic.api.annotations.Id;
import org.chromattic.api.annotations.ManyToOne;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.NamingPolicy;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.OneToOne;
import org.chromattic.api.annotations.Owner;
import org.chromattic.api.annotations.Path;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Properties;
import org.chromattic.api.annotations.Property;
import org.chromattic.api.annotations.WorkspaceName;
import org.chromattic.metamodel.bean.BeanInfo;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.SingleValuedPropertyInfo;
import org.chromattic.metamodel.bean.BeanInfoBuilder;
import org.chromattic.metamodel.bean.MultiValueKind;
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.ValueInfo;
import org.chromattic.metamodel.mapping.InvalidMappingException;
import org.chromattic.metamodel.mapping.NodeAttributeType;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.type.SimpleTypeMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.annotation.AnnotationInfo;
import org.reflext.api.annotation.AnnotationParameterInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationTarget;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ApplicationMappingBuilder {

  /** Used for retrieving {@code java.lang.Object} info. */
  private final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private final ClassTypeInfo FORMATTED_BY = (ClassTypeInfo)domain.resolve(FormattedBy.class);

  /** . */
  private final AnnotationType<AnnotationInfo, ?> FORMATTED_BY_ANNOTATION_TYPE = AnnotationType.get(FORMATTED_BY);

  /** . */
  private final SimpleTypeResolver simpleTypeResolver;

  public ApplicationMappingBuilder() {
    this(new SimpleTypeResolver());
  }

  public ApplicationMappingBuilder(SimpleTypeResolver simpleTypeResolver) {
    this.simpleTypeResolver = simpleTypeResolver;
  }

  public Map<ClassTypeInfo, BeanMapping> build(ClassTypeInfo... classTypes) {
    return build(org.chromattic.common.collection.Collections.set(classTypes));
  }

  public Map<ClassTypeInfo, BeanMapping> build(Set<ClassTypeInfo> classTypes) {

    // Clone for modification
    classTypes = new HashSet<ClassTypeInfo>(classTypes);

    // Get object type
    ClassTypeInfo objectClassType = (ClassTypeInfo)domain.resolve(Object.class);

    // Add object type
    classTypes.add((ClassTypeInfo)domain.resolve(Object.class));

    // Build beans
    Map<ClassTypeInfo, BeanInfo> beans = new BeanInfoBuilder(simpleTypeResolver).build(classTypes);

    // Remove object bean
    BeanInfo objectBean = beans.remove(objectClassType);

    // Create context
    Context ctx = new Context(new SimpleTypeResolver(), new HashSet<BeanInfo>(beans.values()));

    // Build object bean info ahead as it does not contain any annotation
    BeanMapping objectMapping = new BeanMapping(
        objectBean,
        NodeTypeKind.PRIMARY,
        "nt:base",
        NameConflictResolution.FAIL,
        null,
        false,
        true);
    ctx.beanMappings.put(objectBean, objectMapping);

    // Build mappings
    Map<BeanInfo, BeanMapping> beanMappings = ctx.build();

    // Resolve relationships
    new OneToOneHierarchicRelationshipResolver(beanMappings).resolve();
    new OneToManyHierarchicRelationshipResolver(beanMappings).resolve();
    new ManyToOneHierarchicRelationshipResolver(beanMappings).resolve();
    new OneToManyReferenceRelationshipResolver(beanMappings).resolve();
    new ManyToOneReferenceRelationshipResolver(beanMappings).resolve();

    //
    Map<ClassTypeInfo, BeanMapping> classTypeMappings = new HashMap<ClassTypeInfo, BeanMapping>();
    for (Map.Entry<BeanInfo, BeanMapping> beanMapping : beanMappings.entrySet()) {
      classTypeMappings.put(beanMapping.getKey().getClassType(), beanMapping.getValue());
    }

    //
    return classTypeMappings;
  }

  private static abstract class RelationshipResolver<F extends RelationshipMapping<?, T>, T extends RelationshipMapping<?, F>> {

    /** . */
    final Class<F> fromClass;

    /** . */
    final Class<T> toClass;

    /** . */
    Map<BeanInfo, BeanMapping> beanMappings;

    protected RelationshipResolver(Class<F> fromClass, Class<T> toClass, Map<BeanInfo, BeanMapping> beanMappings) {
      this.fromClass = fromClass;
      this.toClass = toClass;
      this.beanMappings = beanMappings;
    }

    void resolve() {
      for (BeanMapping beanMapping : beanMappings.values()) {
        for (PropertyMapping propertyMapping : beanMapping.getProperties().values()) {
          if (propertyMapping instanceof RelationshipMapping<?, ?>) {
            RelationshipMapping<?, ?> relationshipMapping = (RelationshipMapping<?, ?>)propertyMapping;
            BeanInfo relatedBean = relationshipMapping.getRelatedBean();
            BeanMapping relatedBeanMapping = beanMappings.get(relatedBean);
            if (fromClass.isInstance(relationshipMapping)) {
              F fromRelationship = fromClass.cast(relationshipMapping);
              for (PropertyMapping relatedBeanPropertyMapping : relatedBeanMapping.getProperties().values()) {
                if (relatedBeanPropertyMapping instanceof RelationshipMapping) {
                  RelationshipMapping<?, ?> relatedBeanRelationshipMapping = (RelationshipMapping<?, ?>)relatedBeanPropertyMapping;
                  if (toClass.isInstance(relatedBeanRelationshipMapping)) {
                    T toRelationship = toClass.cast(relatedBeanRelationshipMapping);
                    if (fromRelationship != toRelationship) {
                      if (resolves(fromRelationship, toRelationship)) {
                        if (relationshipMapping.relatedRelationshipMapping != null) {
                          throw new UnsupportedOperationException();
                        }
                        fromRelationship.relatedRelationshipMapping = toRelationship;
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    protected abstract boolean resolves(F from, T to);

  }

  private static class OneToOneHierarchicRelationshipResolver extends RelationshipResolver<RelationshipMapping.OneToOne.Hierarchic, RelationshipMapping.OneToOne.Hierarchic> {

    private OneToOneHierarchicRelationshipResolver(Map<BeanInfo, BeanMapping> beanMappings) {
      super(RelationshipMapping.OneToOne.Hierarchic.class, RelationshipMapping.OneToOne.Hierarchic.class, beanMappings);
    }

    @Override
    protected boolean resolves(RelationshipMapping.OneToOne.Hierarchic from, RelationshipMapping.OneToOne.Hierarchic to) {
      return from.mappedBy.equals(to.mappedBy) && from.owner != to.owner;
    }
  }

  private static class OneToManyHierarchicRelationshipResolver extends RelationshipResolver<RelationshipMapping.OneToMany.Hierarchic, RelationshipMapping.ManyToOne.Hierarchic> {

    private OneToManyHierarchicRelationshipResolver(Map<BeanInfo, BeanMapping> beanMappings) {
      super(RelationshipMapping.OneToMany.Hierarchic.class, RelationshipMapping.ManyToOne.Hierarchic.class, beanMappings);
    }

    @Override
    protected boolean resolves(RelationshipMapping.OneToMany.Hierarchic from, RelationshipMapping.ManyToOne.Hierarchic to) {
      return true;
    }
  }

  private static class ManyToOneHierarchicRelationshipResolver extends RelationshipResolver<RelationshipMapping.ManyToOne.Hierarchic, RelationshipMapping.OneToMany.Hierarchic> {

    private ManyToOneHierarchicRelationshipResolver(Map<BeanInfo, BeanMapping> beanMappings) {
      super(RelationshipMapping.ManyToOne.Hierarchic.class, RelationshipMapping.OneToMany.Hierarchic.class, beanMappings);
    }

    @Override
    protected boolean resolves(RelationshipMapping.ManyToOne.Hierarchic from, RelationshipMapping.OneToMany.Hierarchic to) {
      return true;
    }
  }

  private static class OneToManyReferenceRelationshipResolver extends RelationshipResolver<RelationshipMapping.OneToMany.Reference, RelationshipMapping.ManyToOne.Reference> {

    private OneToManyReferenceRelationshipResolver(Map<BeanInfo, BeanMapping> beanMappings) {
      super(RelationshipMapping.OneToMany.Reference.class, RelationshipMapping.ManyToOne.Reference.class, beanMappings);
    }

    @Override
    protected boolean resolves(RelationshipMapping.OneToMany.Reference from, RelationshipMapping.ManyToOne.Reference to) {
      return from.getMappedBy().equals(to.getMappedBy());
    }
  }

  private static class ManyToOneReferenceRelationshipResolver extends RelationshipResolver<RelationshipMapping.ManyToOne.Reference, RelationshipMapping.OneToMany.Reference> {

    private ManyToOneReferenceRelationshipResolver(Map<BeanInfo, BeanMapping> beanMappings) {
      super(RelationshipMapping.ManyToOne.Reference.class, RelationshipMapping.OneToMany.Reference.class, beanMappings);
    }

    @Override
    protected boolean resolves(RelationshipMapping.ManyToOne.Reference from, RelationshipMapping.OneToMany.Reference to) {
      return from.getMappedBy().equals(to.getMappedBy());
    }
  }

  private class Context {

    /** . */
    final SimpleTypeResolver typeResolver;

    /** . */
    final Map<ClassTypeInfo, BeanInfo> beanClassTypeMap;

    /** . */
    final Set<BeanInfo> beans;

    /** . */
    final Map<BeanInfo, BeanMapping> beanMappings;

    private Context(SimpleTypeResolver typeResolver, Set<BeanInfo> beans) {

      //
      Map<ClassTypeInfo, BeanInfo> beanClassTypeMap = new HashMap<ClassTypeInfo, BeanInfo>();
      for (BeanInfo bean : beans) {
        beanClassTypeMap.put(bean.getClassType(), bean);
      }

      //
      this.typeResolver = typeResolver;
      this.beanClassTypeMap = beanClassTypeMap;
      this.beans = beans;
      this.beanMappings = new HashMap<BeanInfo, BeanMapping>();
    }

    public Map<BeanInfo, BeanMapping> build() {
      while (true) {
        Iterator<BeanInfo> iterator = beans.iterator();
        if (iterator.hasNext()) {
          BeanInfo bean = iterator.next();
          resolve(bean);
        } else {
          return beanMappings;
        }
      }
    }

    private BeanMapping resolve(ClassTypeInfo classType) {
      BeanInfo bean = beanClassTypeMap.get(classType);
      if (bean != null) {
        return resolve(bean);
      } else {
        return null;
      }
    }

    private BeanMapping resolve(BeanInfo bean) {
      BeanMapping mapping = beanMappings.get(bean);
      if (mapping == null) {
        if (beans.remove(bean)) {
          mapping = create(bean);
          beanMappings.put(bean, mapping);
          build(mapping);
        } else {
          // It does not resolve
        }
      }
      return mapping;
    }

    private BeanMapping create(BeanInfo bean) {

      Collection<? extends Annotation> annotations = bean.getAnnotations(PrimaryType.class, MixinType.class);
      if (annotations.size() != 1) {
        throw new InvalidMappingException(bean.getClassType(), "Class is not annotated with a primary type of mixin type");
      }

      //
      Annotation mappingAnnotation = annotations.iterator().next();

      //
      NameConflictResolution onDuplicate = NameConflictResolution.FAIL;
      NamingPolicy namingPolicy = bean.getAnnotation(NamingPolicy.class);
      if (namingPolicy != null) {
        onDuplicate = namingPolicy.onDuplicate();
      }

      ClassTypeInfo formatter = null;
      AnnotationInfo formattedBy = bean.getAnnotation(FORMATTED_BY_ANNOTATION_TYPE);
      if (formattedBy != null) {
        AnnotationParameterInfo<ClassTypeInfo> valueParameter = (AnnotationParameterInfo<ClassTypeInfo>)formattedBy.getParameter("value");
        formatter = valueParameter.getValue();
      }

      //
      NodeTypeKind nodeTypeKind;
      String nodeTypeName;
      boolean orderable;
      boolean abstract_;
      if (mappingAnnotation instanceof PrimaryType) {
        PrimaryType primaryTypeAnnotation = (PrimaryType)mappingAnnotation;
        nodeTypeKind = NodeTypeKind.PRIMARY;
        nodeTypeName = primaryTypeAnnotation.name();
        orderable = primaryTypeAnnotation.orderable();
        abstract_ = primaryTypeAnnotation.abstract_();
      } else {
        MixinType primaryTypeAnnotation = (MixinType)mappingAnnotation;
        nodeTypeKind = NodeTypeKind.MIXIN;
        nodeTypeName = primaryTypeAnnotation.name();
        orderable = false;
        abstract_ = true;
      }

      //
      return new BeanMapping(bean, nodeTypeKind, nodeTypeName, onDuplicate, formatter, orderable, abstract_);
    }

    private void build(BeanMapping beanMapping) {

      BeanInfo bean = beanMapping.bean;

      // First build the parent mapping if any
      if (bean.getParent() != null) {
        beanMapping.parent = resolve(bean.getParent());
      }

      //
      Map<String, PropertyMapping<?, ?>> properties = new HashMap<String, PropertyMapping<?, ?>>();
      for (PropertyInfo<?> property : bean.getProperties().values()) {

        // Determine kind
        Collection<? extends Annotation> annotations = property.getAnnotations(
            Property.class,
            Properties.class,
            OneToOne.class,
            OneToMany.class,
            ManyToOne.class,
            Id.class,
            Path.class,
            Name.class,
            WorkspaceName.class
        );

        //
        if (annotations.size() > 1) {
          throw new UnsupportedOperationException();
        }

        // Build the correct mapping or fail
        PropertyMapping<?, ?> mapping = null;
        if (annotations.size() == 1) {
          Annotation annotation = annotations.iterator().next();
          ValueInfo value = property.getValue();
          if (property instanceof SingleValuedPropertyInfo<?>) {
            if (value instanceof SimpleValueInfo) {
              if (annotation instanceof Property) {
                Property propertyAnnotation = (Property)annotation;
                mapping = createProperty(propertyAnnotation, (SingleValuedPropertyInfo<SimpleValueInfo>)property);
              } else if (annotation instanceof Id) {
                mapping = createAttribute((SingleValuedPropertyInfo<SimpleValueInfo>)property, NodeAttributeType.ID);
              } else if (annotation instanceof Path) {
                mapping = createAttribute((SingleValuedPropertyInfo<SimpleValueInfo>)property, NodeAttributeType.PATH);
              } else if (annotation instanceof Name) {
                mapping = createAttribute((SingleValuedPropertyInfo<SimpleValueInfo>)property, NodeAttributeType.NAME);
              } else if (annotation instanceof WorkspaceName) {
                mapping = createAttribute((SingleValuedPropertyInfo<SimpleValueInfo>)property, NodeAttributeType.WORKSPACE_NAME);
              } else {
                throw new InvalidMappingException(bean.getClassType(), "The property " + property + " is not annotated");
              }
            } else if (value instanceof BeanValueInfo) {
              if (annotation instanceof OneToOne) {
                OneToOne oneToOne =  (OneToOne)annotation;
                switch (oneToOne.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicOneToOne(oneToOne, (SingleValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  case EMBEDDED:
                    mapping = createEmbeddedOneToOne((SingleValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  default:
                    throw new UnsupportedOperationException();
                }
              } else if (annotation instanceof ManyToOne) {
                ManyToOne manyToOne = (ManyToOne)annotation;
                switch (manyToOne.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicManyToOne((SingleValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  case PATH:
                  case REFERENCE:
                    mapping = createReferenceManyToOne(manyToOne, (SingleValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  default:
                    throw new UnsupportedOperationException();
                }
              } else {
                throw new UnsupportedOperationException();
              }
            } else {
              throw new AssertionError();
            }
          } else if (property instanceof MultiValuedPropertyInfo<?>) {
            if (value instanceof SimpleValueInfo) {
              if (annotation instanceof Property) {
                Property propertyAnnotation = (Property)annotation;
                mapping = createProperty(propertyAnnotation, (MultiValuedPropertyInfo<SimpleValueInfo>)property);
              } else if (annotation instanceof Properties) {
                mapping = createProperties((MultiValuedPropertyInfo<? extends ValueInfo>)property);
              } else {
                throw new InvalidMappingException(bean.getClassType(), "No annotation found on property " + property);
              }
            } else if (value instanceof BeanValueInfo) {
              if (annotation instanceof OneToMany) {
                OneToMany oneToMany = (OneToMany)annotation;
                switch (oneToMany.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicOneToMany((MultiValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  case PATH:
                  case REFERENCE:
                    mapping = createReferenceOneToMany(oneToMany, (MultiValuedPropertyInfo<BeanValueInfo>)property);
                    break;
                  default:
                    throw new UnsupportedOperationException();
                }
              } else if (annotation instanceof Properties) {
                mapping = createProperties((MultiValuedPropertyInfo<? extends ValueInfo>)property);
              }  else {
                throw new InvalidMappingException(bean.getClassType(), "The property " + property + " should be annotated with " + OneToMany.class.getName());
              }
            } else {
              throw new AssertionError();
            }
          } else {
            throw new AssertionError();
          }
        }

        //
        if (mapping != null) {

          // Resolve parent property without any check for now
          PropertyInfo parentProperty = property.getParent();
          if (parentProperty != null) {
            BeanInfo ancestor = parentProperty.getOwner();
            BeanMapping ancestorMapping = resolve(ancestor);
            mapping.parent = ancestorMapping.properties.get(parentProperty.getName());
          }

          //
          properties.put(mapping.property.getName(), mapping);
        }
      }

      // Wire
      beanMapping.properties.putAll(properties);
      for (PropertyMapping<?, ?> propertyMapping : beanMapping.properties.values()) {
        propertyMapping.owner = beanMapping;
      }

      // Take care of methods
      MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL);
      Set<MethodMapping> methodMappings = new HashSet<MethodMapping>();

      // Create
      for (AnnotationTarget<MethodInfo, Create> annotatedMethods : introspector.resolveMethods(bean.getClassType(), Constants.CREATE)) {
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
            ClassTypeInfo returnTypeInfo = bean.resolveToClass(method.getReturnType());
            if (returnTypeInfo != null) {
              BeanMapping createBeanMapping = resolve(returnTypeInfo);
              if (createBeanMapping == null) {
                throw new UnsupportedOperationException();
              }
              methodMappings.add(new CreateMapping(method, createBeanMapping));
            } else {
              throw new InvalidMappingException(bean.getClassType(), "Invalid " + method + " method return type " + returnTypeInfo);
            }
          } else {
            throw new IllegalStateException();
          }
        }
      }

      // Destroy
      for (AnnotationTarget<MethodInfo, Destroy> annotatedMethods : introspector.resolveMethods(bean.getClassType(), Constants.DESTROY)) {
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
      for (AnnotationTarget<MethodInfo, FindById> annotatedMethods : introspector.resolveMethods(bean.getClassType(), Constants.FIND_BY_ID)) {
        MethodInfo method = annotatedMethods.getTarget();
        if (!method.isStatic()) {
          List<TypeInfo> parameterTypes = method.getParameterTypes();
          if (parameterTypes.size() == 1) {
            TypeInfo argTI = parameterTypes.get(0);
            if (argTI instanceof ClassTypeInfo) {
              ClassTypeInfo argCTI = (ClassTypeInfo)argTI;
              if (argCTI.getName().equals(String.class.getName())) {
                ClassTypeInfo cti = (ClassTypeInfo)bean.getClassType().resolve(method.getReturnType());
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
      beanMapping.methods.addAll(methodMappings);
    }

    private AttributeMapping createAttribute(SingleValuedPropertyInfo<SimpleValueInfo> property, NodeAttributeType type) {
      TypeInfo effectiveType = property.getValue().getEffectiveType();
      if (!(effectiveType instanceof ClassTypeInfo)) {
        throw new UnsupportedOperationException();
      }
      ClassTypeInfo effectiveClassType = (ClassTypeInfo)effectiveType;
      if (!effectiveClassType.getName().equals(String.class.getName())) {
        throw new UnsupportedOperationException();
      }
      return new AttributeMapping(property, type);
    }

    private <V extends ValueInfo> PropertiesMapping<V> createProperties(MultiValuedPropertyInfo<V> property) {
      if (property.getKind() != MultiValueKind.MAP) {
        throw new UnsupportedOperationException();
      }
      TypeInfo type = property.getValue().getEffectiveType();
      PropertyMetaType<?> mt =  null;
      if (type.getName().equals(Object.class.getName())) {
        mt = null;
      } else {
        SimpleTypeMapping stm = typeResolver.resolveType(type);
        if (stm == null) {
          throw new InvalidMappingException(property.getOwner().getClassType(), "Cannot map type " + type + " as properties");
        }
        mt = stm.getPropertyMetaType();
      }
      return new PropertiesMapping<V>(property, mt);
    }

    private <P extends PropertyInfo<SimpleValueInfo>> PropertyMapping<P, SimpleValueInfo> createProperty(
        Property propertyAnnotation,
        P property) {

      //
      PropertyMetaType<?> propertyMetaType = PropertyMetaType.get(propertyAnnotation.type());

      //
      SimpleTypeMapping abc = typeResolver.resolveType(property.getValue().getDeclaredType(), propertyMetaType);
      if (abc == null) {
        throw new UnsupportedOperationException("No simple type mapping for " + property.getValue().getDeclaredType());
      }

      //
      List<String> defaultValueList = null;
      DefaultValue defaultValueAnnotation = property.getAnnotation(DefaultValue.class);
      if (defaultValueAnnotation != null) {
        String[] defaultValues = defaultValueAnnotation.value();
        defaultValueList = new ArrayList<String>(defaultValues.length);
        defaultValueList.addAll(Arrays.asList(defaultValues));
        defaultValueList = Collections.unmodifiableList(defaultValueList);
      }

      //
      PropertyDefinitionMapping propertyDefinition = new PropertyDefinitionMapping(
          propertyAnnotation.name(),
          abc.getPropertyMetaType(),
          defaultValueList);

      //
      if (property instanceof SingleValuedPropertyInfo<?>) {
        return (PropertyMapping<P, SimpleValueInfo>)new ValueMapping.Single((SingleValuedPropertyInfo<SimpleValueInfo>)property, propertyDefinition);
      } else if (property instanceof MultiValuedPropertyInfo<?>) {
        return (PropertyMapping<P, SimpleValueInfo>)new ValueMapping.Multi((MultiValuedPropertyInfo<SimpleValueInfo>)property, propertyDefinition);
      } else {
        throw new AssertionError();
      }
    }

    private RelationshipMapping.OneToMany.Reference createReferenceOneToMany(OneToMany annotation, MultiValuedPropertyInfo<BeanValueInfo> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new UnsupportedOperationException();
      }
      RelationshipMapping.OneToMany.Reference mapping;
      mapping = new RelationshipMapping.OneToMany.Reference(property, mappedBy.value(), annotation.type());
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToMany.Hierarchic createHierarchicOneToMany(MultiValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipMapping.OneToMany.Hierarchic mapping;
      mapping = new RelationshipMapping.OneToMany.Hierarchic(property);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.ManyToOne.Reference createReferenceManyToOne(ManyToOne annotation, SingleValuedPropertyInfo<BeanValueInfo> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new UnsupportedOperationException();
      }
      RelationshipMapping.ManyToOne.Reference mapping;
      mapping = new RelationshipMapping.ManyToOne.Reference(property, mappedBy.value(), annotation.type());
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.ManyToOne.Hierarchic createHierarchicManyToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipMapping.ManyToOne.Hierarchic mapping;
      mapping = new RelationshipMapping.ManyToOne.Hierarchic(property);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToOne.Embedded createEmbeddedOneToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipMapping.OneToOne.Embedded mapping;
      boolean owner = property.getAnnotation(Owner.class) != null;
      mapping = new RelationshipMapping.OneToOne.Embedded(property, owner);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToOne.Hierarchic createHierarchicOneToOne(
        OneToOne annotation,
        SingleValuedPropertyInfo<BeanValueInfo> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new UnsupportedOperationException();
      }
      boolean owner = property.getAnnotation(Owner.class) != null;
      Set<AttributeOption> attributes = new HashSet<AttributeOption>();
      attributes.addAll(Arrays.asList(annotation.options()));
      boolean autocreated = attributes.contains(AttributeOption.AUTOCREATED);
      boolean mandatory = attributes.contains(AttributeOption.MANDATORY);
      RelationshipMapping.OneToOne.Hierarchic mapping;
      mapping = new RelationshipMapping.OneToOne.Hierarchic(property, owner, mappedBy.value(), mandatory, autocreated);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }
  }
}
