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

package org.chromattic.metamodel.mapping;

import org.chromattic.api.NameConflictResolution;
import org.chromattic.api.RelationshipType;
import org.chromattic.api.annotations.AutoCreated;
import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.DefaultValue;
import org.chromattic.api.annotations.Destroy;
import org.chromattic.api.annotations.FindById;
import org.chromattic.api.annotations.FormattedBy;
import org.chromattic.api.annotations.Id;
import org.chromattic.api.annotations.Mandatory;
import org.chromattic.api.annotations.ManyToOne;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.NamingPolicy;
import org.chromattic.api.annotations.NamingPrefix;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.OneToOne;
import org.chromattic.api.annotations.Owner;
import org.chromattic.api.annotations.Path;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Properties;
import org.chromattic.api.annotations.Property;
import org.chromattic.api.annotations.WorkspaceName;
import org.chromattic.metamodel.bean.BeanFilter;
import org.chromattic.metamodel.bean.BeanInfo;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.BeanInfoBuilder;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.bean.ValueInfo;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * The bean mapping builder.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanMappingBuilder {

  /** Used for retrieving {@code java.lang.Object} info. */
  private final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private final ClassTypeInfo FORMATTED_BY = (ClassTypeInfo)domain.resolve(FormattedBy.class);

  /** . */
  private final AnnotationType<AnnotationInfo, ?> FORMATTED_BY_ANNOTATION_TYPE = AnnotationType.get(FORMATTED_BY);

  /** . */
  private final SimpleTypeResolver simpleTypeResolver;

  public BeanMappingBuilder() {
    this(new SimpleTypeResolver());
  }

  public BeanMappingBuilder(SimpleTypeResolver simpleTypeResolver) {
    this.simpleTypeResolver = simpleTypeResolver;
  }

  public Map<ClassTypeInfo, BeanMapping> build(ClassTypeInfo... classTypes) {
    return build(org.chromattic.common.collection.Collections.set(classTypes));
  }

  public Map<ClassTypeInfo, BeanMapping> build(Set<ClassTypeInfo> classTypes) {

    // Clone for modification
    classTypes = new HashSet<ClassTypeInfo>(classTypes);

    // Build beans
    final AtomicReference<ClassTypeInfo> objectCTI = new AtomicReference<ClassTypeInfo>();
    BeanFilter filter = new BeanFilter() {
      public boolean accept(ClassTypeInfo cti) {
        boolean accept = false;
        if (cti.getName().equals(Object.class.getName())) {
          objectCTI.set(cti);
          accept = true;
        } else {
          accept |= cti.getDeclaredAnnotation(AnnotationType.get(PrimaryType.class)) != null;
          accept |= cti.getDeclaredAnnotation(AnnotationType.get(MixinType.class)) != null;
        }
        return accept;
      }
    };
    Map<ClassTypeInfo, BeanInfo> beans = new BeanInfoBuilder(simpleTypeResolver, filter).build(classTypes);

    // Create context
    Context ctx = new Context(new SimpleTypeResolver(), new HashSet<BeanInfo>(beans.values()));

    // Build object bean info ahead as it does not contain any annotation
    if (objectCTI.get() != null) {
      BeanInfo objectBean = beans.remove(objectCTI.get());
      BeanMapping objectMapping = new BeanMapping(
          objectBean,
          NodeTypeKind.PRIMARY,
          "nt:base",
          NameConflictResolution.FAIL,
          null,
          false,
          true,
          null);
      ctx.beanMappings.put(objectBean, objectMapping);
      ctx.beans.remove(objectBean);
    }

    // Build mappings
    Map<BeanInfo, BeanMapping> beanMappings = ctx.build();

    // Resolve relationships
    for (BeanMapping beanMapping : beanMappings.values()) {
      for (PropertyMapping propertyMapping : beanMapping.getProperties().values()) {
        if (propertyMapping instanceof RelationshipMapping<?, ?, ?>) {
          RelationshipMapping<?, ?, ?> relationshipMapping = (RelationshipMapping<?, ?, ?>)propertyMapping;
          relationshipMapping.resolve();
        }
      }
    }

    //
    Map<ClassTypeInfo, BeanMapping> classTypeMappings = new HashMap<ClassTypeInfo, BeanMapping>();
    for (Map.Entry<BeanInfo, BeanMapping> beanMapping : beanMappings.entrySet()) {
      classTypeMappings.put(beanMapping.getKey().getClassType(), beanMapping.getValue());
    }

    //
    return classTypeMappings;
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

      //
      ClassTypeInfo formatter = null;
      AnnotationInfo formattedBy = bean.getAnnotation(FORMATTED_BY_ANNOTATION_TYPE);
      if (formattedBy != null) {
        AnnotationParameterInfo<ClassTypeInfo> valueParameter = (AnnotationParameterInfo<ClassTypeInfo>)formattedBy.getParameter("value");
        formatter = valueParameter.getValue();
      }

      //
      NamingPrefix namingPrefix = bean.getAnnotation(NamingPrefix.class);
      String prefix = null;
      if (namingPrefix != null) {
        prefix = namingPrefix.value();
      }

      //
      NodeTypeKind nodeTypeKind;
      String nodeTypeName;
      boolean orderable;
      boolean abstract_;
      if (mappingAnnotation instanceof PrimaryType) {
        PrimaryType typeAnnotation = (PrimaryType)mappingAnnotation;
        nodeTypeKind = NodeTypeKind.PRIMARY;
        nodeTypeName = typeAnnotation.name();
        orderable = typeAnnotation.orderable();
        abstract_ = typeAnnotation.abstract_();
      } else {
        MixinType typeAnnotation = (MixinType)mappingAnnotation;
        nodeTypeKind = NodeTypeKind.MIXIN;
        nodeTypeName = typeAnnotation.name();
        orderable = false;
        abstract_ = true;
      }

      //
      return new BeanMapping(bean, nodeTypeKind, nodeTypeName, onDuplicate, formatter, orderable, abstract_, prefix);
    }

    private void build(BeanMapping beanMapping) {

      BeanInfo bean = beanMapping.bean;

      // First build the parent mapping if any
      if (bean.getParent() != null) {
        beanMapping.parent = resolve(bean.getParent());
      }

      //
      Map<String, PropertyMapping<?, ?, ?>> properties = new HashMap<String, PropertyMapping<?, ?, ?>>();
      for (PropertyInfo<?, ?> property : bean.getProperties().values()) {

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
          throw new InvalidMappingException(bean.getClassType(), "The property " + property + " declares too many annotations " + annotations);
        }

        // Build the correct mapping or fail
        PropertyMapping<?, ?, ?> mapping = null;
        if (annotations.size() == 1) {
          Annotation annotation = annotations.iterator().next();
          ValueInfo value = property.getValue();
          if (property.getValueKind() == ValueKind.SINGLE) {
            if (value instanceof SimpleValueInfo<?>) {
              SimpleValueInfo<?> simpleValue = (SimpleValueInfo<?>)value;
              if (annotation instanceof Property) {
                Property propertyAnnotation = (Property)annotation;
                if (simpleValue.getValueKind() instanceof ValueKind.Single) {
                  PropertyInfo<SimpleValueInfo<ValueKind.Single>, ValueKind.Single> a = (PropertyInfo<SimpleValueInfo<ValueKind.Single>, ValueKind.Single>)property;
                  mapping = createValueMapping(propertyAnnotation, a);
                } else {
                  PropertyInfo<SimpleValueInfo<ValueKind.Multi>, ValueKind.Single> a = (PropertyInfo<SimpleValueInfo<ValueKind.Multi>, ValueKind.Single>)property;
                  mapping = createValueMapping(propertyAnnotation, a);
                }
              } else if (annotation instanceof Id) {
                mapping = createAttribute((PropertyInfo<SimpleValueInfo, ValueKind.Single>)property, NodeAttributeType.ID);
              } else if (annotation instanceof Path) {
                mapping = createAttribute((PropertyInfo<SimpleValueInfo, ValueKind.Single>)property, NodeAttributeType.PATH);
              } else if (annotation instanceof Name) {
                mapping = createAttribute((PropertyInfo<SimpleValueInfo, ValueKind.Single>)property, NodeAttributeType.NAME);
              } else if (annotation instanceof WorkspaceName) {
                mapping = createAttribute((PropertyInfo<SimpleValueInfo, ValueKind.Single>)property, NodeAttributeType.WORKSPACE_NAME);
              } else {
                throw new InvalidMappingException(bean.getClassType(), "The property " + property + " is not annotated");
              }
            } else if (value instanceof BeanValueInfo) {
              if (annotation instanceof OneToOne) {
                OneToOne oneToOne =  (OneToOne)annotation;
                switch (oneToOne.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicOneToOne(beanMapping, oneToOne, (PropertyInfo<BeanValueInfo, ValueKind.Single>) property);
                    break;
                  case EMBEDDED:
                    mapping = createEmbeddedOneToOne((PropertyInfo<BeanValueInfo, ValueKind.Single>) property);
                    break;
                  default:
                    throw new InvalidMappingException(bean.getClassType(), "Expecting that the @OneToOne property " +
                        property + " to be annotated with " + RelationshipType.HIERARCHIC + " or "
                        + RelationshipType.EMBEDDED + " instead of " + oneToOne.type());
                }
              } else if (annotation instanceof ManyToOne) {
                ManyToOne manyToOne = (ManyToOne)annotation;
                switch (manyToOne.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicManyToOne(beanMapping, manyToOne, (PropertyInfo<BeanValueInfo, ValueKind.Single>)property);
                    break;
                  case PATH:
                  case REFERENCE:
                    mapping = createReferenceManyToOne(manyToOne, (PropertyInfo<BeanValueInfo, ValueKind.Single>)property);
                    break;
                  default:
                    throw new InvalidMappingException(bean.getClassType(), "Expecting that the @ManyToOne property " +
                        property + " to be annotated with " + RelationshipType.HIERARCHIC + ", "
                        + RelationshipType.PATH + " or " + RelationshipType.REFERENCE + " instead of " +
                        manyToOne.type());
                }
              } else {
                throw new InvalidMappingException(bean.getClassType(), "Annotation " + annotation + " is forbidden " +
                " on property " + property);
              }
            } else {
              throw new AssertionError();
            }
          } else if (property.getValueKind() instanceof ValueKind.Multi) {
            if (value instanceof SimpleValueInfo) {
              SimpleValueInfo<?> simpleValue = (SimpleValueInfo<?>)value;
              if (annotation instanceof Property) {
                Property propertyAnnotation = (Property)annotation;
                if (simpleValue.getValueKind() instanceof ValueKind.Single) {
                  PropertyInfo<SimpleValueInfo<ValueKind.Single>, ValueKind.Single> a = (PropertyInfo<SimpleValueInfo<ValueKind.Single>, ValueKind.Single>)property;
                  mapping = createValueMapping(propertyAnnotation, a);
                } else {
                  PropertyInfo<SimpleValueInfo<ValueKind.Multi>, ValueKind.Single> a = (PropertyInfo<SimpleValueInfo<ValueKind.Multi>, ValueKind.Single>)property;
                  mapping = createValueMapping(propertyAnnotation, a);
                }
              } else if (annotation instanceof Properties) {
                mapping = createProperties((PropertyInfo<?, ValueKind.Map>)property);
              } else {
                throw new InvalidMappingException(bean.getClassType(), "Annotation " + annotation + " is forbidden " +
                " on property " + property);
              }
            } else if (value instanceof BeanValueInfo) {
              if (annotation instanceof OneToMany) {
                OneToMany oneToMany = (OneToMany)annotation;
                switch (oneToMany.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicOneToMany(beanMapping, oneToMany, (PropertyInfo<BeanValueInfo, ?>)property);
                    break;
                  case PATH:
                  case REFERENCE:
                    mapping = createReferenceOneToMany(oneToMany, (PropertyInfo<BeanValueInfo, ?>)property);
                    break;
                  default:
                    throw new InvalidMappingException(bean.getClassType(), "Expecting that the @OneToMany property " +
                        property + " to be annotated with " + RelationshipType.HIERARCHIC + ", "
                        + RelationshipType.PATH + " or " + RelationshipType.REFERENCE + " instead of " +
                        oneToMany.type());
                }
              } else if (annotation instanceof Properties) {
                mapping = createProperties((PropertyInfo<?, ValueKind.Map>)property);
              }  else {
                throw new InvalidMappingException(bean.getClassType(), "Annotation " + annotation + " is forbidden " +
                " on property " + property);
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
      for (PropertyMapping<?, ?, ?> propertyMapping : beanMapping.properties.values()) {
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
                  throw new InvalidMappingException(bean.getClassType(), "The argument of the @Create method " +
                  method + " must be a java.lang.String instead of " + method.getSignature());
                }
              } else {
                throw new InvalidMappingException(bean.getClassType(), "The argument of the @Create method " +
                method + " must be a java.lang.String instead of " + method.getSignature());
              }
            }
            ClassTypeInfo returnTypeInfo = bean.resolveToClass(method.getReturnType());
            if (returnTypeInfo != null) {
              BeanMapping createBeanMapping = resolve(returnTypeInfo);
              if (createBeanMapping == null) {
                throw new InvalidMappingException(bean.getClassType(), "Could not resolve the return type " + returnTypeInfo + " to a chromattic bean among beans " +
                beans + " and mappings " + beanMappings.values());
              }
              methodMappings.add(new CreateMapping(method, createBeanMapping));
            } else {
              throw new InvalidMappingException(bean.getClassType(), "Invalid @Create method " + method +
                  " return type " + returnTypeInfo);
            }
          } else {
            throw new InvalidMappingException(bean.getClassType(), "The signature of the @Create method " +
            method + "should have zero or one argument instead of " + method.getSignature());
          }
        } else {
          throw new InvalidMappingException(bean.getClassType(), "The @Create method " +
          method + " must not be static");
        }
      }

      // Destroy
      for (AnnotationTarget<MethodInfo, Destroy> annotatedMethods : introspector.resolveMethods(bean.getClassType(), Constants.DESTROY)) {
        MethodInfo method = annotatedMethods.getTarget();
        if (!method.isStatic()) {
          List<TypeInfo> parameterTypes = method.getParameterTypes();
          if (parameterTypes.size() != 0) {
            throw new InvalidMappingException(bean.getClassType(), "The @Destroy method " +
            method + " must have no arguments");
          }
          if (!(method.getReturnType() instanceof VoidTypeInfo)) {
            throw new InvalidMappingException(bean.getClassType(), "The @Destroy method " +
            method + " must have a void return type");
          }
          methodMappings.add(new DestroyMapping(method));
        } else {
          throw new InvalidMappingException(bean.getClassType(), "The @Destroy method " +
          method + " must not be static");
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
                throw new InvalidMappingException(bean.getClassType(), "The argument of the @FindById method " +
                method + " must be a java.lang.String instead of " + method.getSignature());
              }
            } else {
              throw new InvalidMappingException(bean.getClassType(), "The argument of the @FindById method " +
              method + " must be a java.lang.String instead of " + method.getSignature());
            }
          } else {
            throw new InvalidMappingException(bean.getClassType(), "The signature of the @FindById method " +
            method + "should a single java.lang.String argument instead of " + method.getSignature());
          }
        } else {
          throw new InvalidMappingException(bean.getClassType(), "The @FindById method " +
          method + " must not be static");
        }
      }

      //
      beanMapping.methods.addAll(methodMappings);
    }

    private AttributeMapping createAttribute(PropertyInfo<SimpleValueInfo, ValueKind.Single> property, NodeAttributeType type) {
      TypeInfo effectiveType = property.getValue().getEffectiveType();
      if (!(effectiveType instanceof ClassTypeInfo)) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The property " + property +
            " must be of type java.lang.String");
      }
      ClassTypeInfo effectiveClassType = (ClassTypeInfo)effectiveType;
      if (!effectiveClassType.getName().equals(String.class.getName())) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The property " + property +
            " must be of type java.lang.String");
      }
      return new AttributeMapping(property, type);
    }

    private <V extends ValueInfo> PropertiesMapping<V> createProperties(PropertyInfo<V, ValueKind.Map> property) {
      if (property.getValueKind() != ValueKind.MAP) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The @Properties " + property +
            " must be of type java.util.Map instead of " + property.getValue().getEffectiveType());
      }
      TypeInfo type = property.getValue().getEffectiveType();

      //
      PropertyMetaType<?> mt = null;
      ValueKind valueKind;
      ValueInfo vi = property.getValue();
      if (vi instanceof SimpleValueInfo<?>) {
        SimpleValueInfo<?> svi = (SimpleValueInfo<?>)vi;
        if (svi.getTypeMapping() != null) {
          mt = svi.getTypeMapping().getPropertyMetaType();
        }
        valueKind = svi.getValueKind();
      } else {
        if (type.getName().equals(Object.class.getName())) {
          mt = null;
        }
        valueKind = ValueKind.SINGLE;
      }

      //
      String prefix = null;
      NamingPrefix namingPrefix = property.getAnnotation(NamingPrefix.class);
      if (namingPrefix != null) {
        prefix = namingPrefix.value();
      }

      //
      return new PropertiesMapping<V>(property, prefix, mt, valueKind);
    }

    private
        <K extends ValueKind>
        ValueMapping<K>
        createValueMapping(
        Property propertyAnnotation,
        PropertyInfo<SimpleValueInfo<K>, ValueKind.Single> property) {

      //
      PropertyMetaType<?> propertyMetaType = PropertyMetaType.get(propertyAnnotation.type());

      //
      SimpleTypeMapping resolved = typeResolver.resolveType(property.getValue().getDeclaredType(), propertyMetaType);
      if (resolved == null) {
        throw new InvalidMappingException(property.getOwner().getClassType(),  "No simple type mapping "
            + property.getValue().getDeclaredType() + " for property " + property);
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
      PropertyDefinitionMapping<?> propertyDefinition = new PropertyDefinitionMapping(
          propertyAnnotation.name(),
          resolved.getPropertyMetaType(),
          defaultValueList,
          false);

      //
      return new ValueMapping<K>(property, propertyDefinition);
    }

    private RelationshipMapping.OneToMany.Reference createReferenceOneToMany(OneToMany annotation, PropertyInfo<BeanValueInfo, ?> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The reference @OneToMany relationship " +
            property + "must carry an @MappedBy annotation");
      }
      RelationshipMapping.OneToMany.Reference mapping;
      mapping = new RelationshipMapping.OneToMany.Reference(property, mappedBy.value(), annotation.type());
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToMany.Hierarchic createHierarchicOneToMany(BeanMapping beanMapping, OneToMany annotation, PropertyInfo<BeanValueInfo, ?> property) {
      RelationshipMapping.OneToMany.Hierarchic mapping;
      NamingPrefix namingPrefix = property.getAnnotation(NamingPrefix.class);
      String declaredPrefix = namingPrefix != null ? namingPrefix.value() : null;
      String prefix = declaredPrefix == null ? beanMapping.getPrefix() : declaredPrefix;
      mapping = new RelationshipMapping.OneToMany.Hierarchic(property, declaredPrefix, prefix);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.ManyToOne.Reference createReferenceManyToOne(ManyToOne annotation, PropertyInfo<BeanValueInfo, ValueKind.Single> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The reference @ManyToOne relationship " +
            property + "must carry an @MappedBy annotation");
      }
      RelationshipMapping.ManyToOne.Reference mapping;
      mapping = new RelationshipMapping.ManyToOne.Reference(property, mappedBy.value(), annotation.type());
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.ManyToOne.Hierarchic createHierarchicManyToOne(BeanMapping beanMapping, ManyToOne annotation, PropertyInfo<BeanValueInfo, ValueKind.Single> property) {
      RelationshipMapping.ManyToOne.Hierarchic mapping;
      NamingPrefix namingPrefix = property.getAnnotation(NamingPrefix.class);
      String declaredPrefix = namingPrefix != null ? namingPrefix.value() : null;
      String prefix = declaredPrefix == null ? beanMapping.getPrefix() : declaredPrefix;
      mapping = new RelationshipMapping.ManyToOne.Hierarchic(property, declaredPrefix, prefix);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToOne.Embedded createEmbeddedOneToOne(PropertyInfo<BeanValueInfo, ValueKind.Single> property) {
      RelationshipMapping.OneToOne.Embedded mapping;
      boolean owner = property.getAnnotation(Owner.class) != null;
      mapping = new RelationshipMapping.OneToOne.Embedded(property, owner);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }

    private RelationshipMapping.OneToOne.Hierarchic createHierarchicOneToOne(
        BeanMapping beanMapping,
        OneToOne annotation,
        PropertyInfo<BeanValueInfo, ValueKind.Single> property) {
      MappedBy mappedBy = property.getAnnotation(MappedBy.class);
      if (mappedBy == null) {
        throw new InvalidMappingException(property.getOwner().getClassType(), "The @OneToOne relationship " +
            property + "must carry an @MappedBy annotation");
      }
      boolean owner = property.getAnnotation(Owner.class) != null;
      boolean autocreated = property.getAnnotation(AutoCreated.class) != null;
      boolean mandatory = property.getAnnotation(Mandatory.class) != null;
      String declaredPrefix;
      String localName;
      int index = mappedBy.value().indexOf(':');
      if (index == -1) {
        declaredPrefix = null;
        localName = mappedBy.value();
      } else {
        declaredPrefix = mappedBy.value().substring(0, index);
        localName = mappedBy.value().substring(index + 1);
      }
      String prefix = declaredPrefix == null ? beanMapping.getPrefix() : declaredPrefix;
      RelationshipMapping.OneToOne.Hierarchic mapping;
      mapping = new RelationshipMapping.OneToOne.Hierarchic(property, owner, declaredPrefix, prefix, localName, mandatory, autocreated);
      mapping.relatedBeanMapping = resolve(property.getValue().getBean());
      return mapping;
    }
  }
}
