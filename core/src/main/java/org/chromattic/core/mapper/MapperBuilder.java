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

package org.chromattic.core.mapper;

import org.chromattic.api.BuilderException;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.core.EmbeddedContext;
import org.chromattic.core.EntityContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedParentPropertyMapper;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedPropertyMapper;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.chromattic.metamodel.mapping.MethodMapping;
import org.chromattic.metamodel.mapping.CreateMapping;
import org.chromattic.metamodel.mapping.DestroyMapping;
import org.chromattic.metamodel.mapping.FindByIdMapping;
import org.chromattic.metamodel.mapping.jcr.JCRMemberMapping;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.metamodel.mapping.value.*;
import org.chromattic.common.collection.SetMap;
import org.chromattic.core.mapper.onetomany.reference.JCRReferentCollectionPropertyMapper;
import org.chromattic.core.mapper.onetomany.reference.JCRNamedReferentPropertyMapper;
import org.chromattic.core.mapper.onetomany.hierarchical.JCRAnyChildCollectionPropertyMapper;
import org.chromattic.core.mapper.onetomany.hierarchical.JCRAnyChildParentPropertyMapper;
import org.chromattic.core.mapper.onetomany.hierarchical.AnyChildMultiValueMapper;
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildPropertyMapper;
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildParentPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyMapPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyListPropertyMapper;
import org.chromattic.core.mapper.nodeattribute.JCRNodeAttributePropertyMapper;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.metamodel.bean.SingleValuedPropertyInfo;
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.MapPropertyInfo;
import org.chromattic.metamodel.bean.CollectionPropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.ListPropertyInfo;
import org.chromattic.api.RelationshipType;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.spi.instrument.ProxyFactory;
import org.reflext.api.ClassTypeInfo;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MapperBuilder {

  /** . */
  private final static EnumMap<RelationshipType, LinkType> relationshipToLinkMapping;

  static {
    EnumMap<RelationshipType, LinkType> tmp = new EnumMap<RelationshipType, LinkType>(RelationshipType.class);
    tmp.put(RelationshipType.REFERENCE, LinkType.REFERENCE);
    tmp.put(RelationshipType.PATH, LinkType.PATH);
    relationshipToLinkMapping = tmp;
  }

  /** . */
  private static final ProxyFactory<?> NULL_PROXY_FACTORY = new ProxyFactory<Object>() {
    public Object createProxy(MethodHandler invoker) {
      throw new UnsupportedOperationException();
    }
  };

  /** . */
  private static final Instrumentor NULL_INSTRUMENTOR = new Instrumentor() {

    // This is OK as the class is *stateless*
    @SuppressWarnings("unchecked")
    public <O> ProxyFactory<O> getProxyClass(Class<O> clazz) {
      return (ProxyFactory<O>)NULL_PROXY_FACTORY;
    }

    public MethodHandler getInvoker(Object proxy) {
      throw new UnsupportedOperationException();
    }
  };

  /** . */
  private final Set<NodeTypeMapping> typeMappings;

  /** . */
  private final Instrumentor instrumentor;

  /** . */
  private final Map<ClassTypeInfo, NodeTypeMapping> classToMapping;

  public MapperBuilder(Set<NodeTypeMapping> typeMappings, Instrumentor instrumentor) {
    Map<ClassTypeInfo, NodeTypeMapping> classToMapping = new HashMap<ClassTypeInfo, NodeTypeMapping>();
    for (NodeTypeMapping typeMapping : typeMappings) {
      classToMapping.put(typeMapping.getType(), typeMapping);
    }

    //
    this.typeMappings = typeMappings;
    this.instrumentor = instrumentor;
    this.classToMapping = classToMapping;
  }

  private SetMap<ClassTypeInfo, RelatedPropertyMapper> relatedProperties = new SetMap<ClassTypeInfo, RelatedPropertyMapper>();
  private SetMap<ClassTypeInfo, MethodMapper.Create> relatedMethods = new SetMap<ClassTypeInfo, MethodMapper.Create>();

  public Collection<ObjectMapper> build() {
    try {
      return _build();
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<ObjectMapper> _build() throws ClassNotFoundException {

    //
    Map<String, ObjectMapper> mappers = new HashMap<String, ObjectMapper>();


    for (NodeTypeMapping typeMapping : typeMappings) {
      Class<? extends ObjectContext> contextType = typeMapping.isPrimary() ? EntityContext.class : EmbeddedContext.class;
      ObjectMapper<?> mapper = createMapper(contextType, typeMapping);
      mappers.put(typeMapping.getType().getName(), mapper);
    }

    // Resolve related types
    for (ClassTypeInfo relatedType : relatedProperties.keySet()) {

      //
      Set<RelatedPropertyMapper> properties = relatedProperties.get(relatedType);

      //
      Set<ObjectMapper> relatedTypes = new HashSet<ObjectMapper>();
      for (ObjectMapper type : mappers.values()) {
        Class relatedClass = Thread.currentThread().getContextClassLoader().loadClass(relatedType.getName());
        if (relatedClass.isAssignableFrom(type.getObjectClass())) {
          relatedTypes.add(type);
        }
      }

      //
      for (RelatedPropertyMapper propertyMapper : properties) {
        propertyMapper.relatedTypes = relatedTypes;
      }
    }

    //
    for (ClassTypeInfo relatedType : relatedMethods.keySet()) {
      Set<MethodMapper.Create> methods = relatedMethods.get(relatedType);
      ObjectMapper relatedMapper = mappers.get(relatedType.getName());
      if (relatedMapper == null) {
        throw new IllegalStateException("Could not find mapper for " + relatedType.getName() + " referenced by " + methods);
      }
      for (MethodMapper.Create createMapper : methods) {
        createMapper.type = relatedMapper;
      }
    }

    //
    return new ArrayList<ObjectMapper>(mappers.values());
  }

  private <C extends ObjectContext> ObjectMapper createMapper(Class<C> contextType, NodeTypeMapping typeMapping) throws ClassNotFoundException {

    //
    Set<MethodMapper<C>> methodMappers = new HashSet<MethodMapper<C>>();
    Set<MethodMapper<EntityContext>> methodMappersForE = new HashSet<MethodMapper<EntityContext>>();

    //
    Set<PropertyMapper<?, C>> propertyMappers = new HashSet<PropertyMapper<?, C>>();
    Set<PropertyMapper<?, EntityContext>> propertyMappersForEntity = new HashSet<PropertyMapper<?, EntityContext>>();
    Set<PropertyMapper<?, EmbeddedContext>> propertyMappersForEmbedded = new HashSet<PropertyMapper<?, EmbeddedContext>>();

    //
    for (PropertyMapping<?> pm : typeMapping.getPropertyMappings()) {

      //
      if (pm.getInfo() instanceof SingleValuedPropertyInfo) {
        ValueMapping pmvm = pm.getValueMapping();

        //
        SingleValuedPropertyInfo<BeanValueInfo> propertyInfo = (SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo();

        //
        if (pmvm instanceof SimpleMapping) {
          SimpleMapping pmdm = (SimpleMapping)pmvm;
          JCRMemberMapping jcrMember = pmdm.getJCRMember();

          //
          if (jcrMember instanceof JCRPropertyMapping) {
            JCRPropertyMapping jcrProperty = (JCRPropertyMapping)jcrMember;
            JCRPropertyPropertyMapper<C, ?, ?> mapper = createPropertyPM(contextType, pm, jcrProperty);
            propertyMappers.add(mapper);
          } else if (jcrMember instanceof JCRNodeAttributeMapping) {
            JCRNodeAttributeMapping nam = (JCRNodeAttributeMapping)jcrMember;
            JCRNodeAttributePropertyMapper bilto = new JCRNodeAttributePropertyMapper((SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), nam.getType());
            if (contextType == EntityContext.class) {
              propertyMappersForEntity.add(bilto);
            } else {
              throw new UnsupportedOperationException("todo");
            }
          }
        } else if (pmvm instanceof RelationshipMapping) {
          RelationshipMapping pmhm = (RelationshipMapping<?, ?>)pmvm;

          //
          if (pmhm.getType() == RelationshipType.HIERARCHIC) {
            if (pmhm instanceof ManyToOneMapping) {
              JCRChildNodePropertyMapper bilto = new JCRAnyChildCollectionPropertyMapper(propertyInfo);
              relatedProperties.get(pmhm.getRelatedMapping().getType()).add(bilto);
              propertyMappersForEntity.add(bilto);
            } if (pmhm instanceof NamedOneToOneMapping) {
              NamedOneToOneMapping ncpmpm = (NamedOneToOneMapping)pmhm;
              if (ncpmpm.isOwning()) {
                JCRNamedChildParentPropertyMapper<C> bilto = new JCRNamedChildParentPropertyMapper<C>(contextType, propertyInfo, ncpmpm.getName());
                relatedProperties.get(pmhm.getRelatedMapping().getType()).add(bilto);
                propertyMappers.add(bilto);
              } else {
                JCRChildNodePropertyMapper bilto = new JCRNamedChildPropertyMapper(propertyInfo, ncpmpm.getName());
                relatedProperties.get(ncpmpm.getRelatedMapping().getType()).add(bilto);
                propertyMappersForEntity.add(bilto);
              }
            }
          } else if (pmhm.getType() == RelationshipType.EMBEDDED) {
            NodeTypeMapping relatedMapping = classToMapping.get(propertyInfo.getValue().getTypeInfo());
            OneToOneMapping a = (OneToOneMapping)pmhm;
            if (typeMapping.isPrimary()) {
              if (a.isOwning()) {
                JCREmbeddedParentPropertyMapper mapper = new JCREmbeddedParentPropertyMapper(propertyInfo);
                propertyMappersForEntity.add(mapper);
              } else {
                JCREmbeddedPropertyMapper mapper = new JCREmbeddedPropertyMapper(propertyInfo);
                propertyMappersForEmbedded.add(mapper);
              }
            } else if (typeMapping.isMixin()) {
              if (a.isOwning()) {
                throw new BuilderException();
              }
              if (relatedMapping.isPrimary()) {
                JCREmbeddedPropertyMapper mapper = new JCREmbeddedPropertyMapper(propertyInfo);
                propertyMappersForEmbedded.add(mapper);
              } else {
                throw new BuilderException("Related class of a mixin in a one to one embedded relationship must be " +
                  "annotated with @" + PrimaryType.class.getSimpleName());
              }
            } else {
              throw new AssertionError();
            }
          }
        }

        //
        if (pmvm instanceof AbstractManyToOneMapping) {
          if (pmvm instanceof NamedManyToOneMapping) {
            NamedManyToOneMapping nmtovm = (NamedManyToOneMapping)pmvm;
            LinkType linkType = relationshipToLinkMapping.get(nmtovm.getType());
            if (linkType != null) {
              JCRNamedReferentPropertyMapper<C> blah = new JCRNamedReferentPropertyMapper<C>(
                contextType,
                propertyInfo,
                nmtovm.getRelatedName(),
                linkType
                );
              propertyMappers.add(blah);
              relatedProperties.get(nmtovm.getRelatedMapping().getType()).add(blah);
            }
          }
        }
      } else if (pm.getInfo() instanceof MultiValuedPropertyInfo) {
        ValueMapping pmvm = pm.getValueMapping();

        //
        if (pmvm instanceof RelationshipMapping) {
          RelationshipMapping pmhm = (RelationshipMapping)pmvm;

          //
          if (pmhm instanceof AbstractOneToManyMapping) {

            //
            if (pmhm instanceof NamedOneToManyMapping) {
              LinkType linkType = relationshipToLinkMapping.get(pmhm.getType());
              if (linkType != null) {
                NamedOneToManyMapping fff = (NamedOneToManyMapping)pmhm;
                JCRReferentCollectionPropertyMapper bilto = new JCRReferentCollectionPropertyMapper(
                  (CollectionPropertyInfo<BeanValueInfo>)pm.getInfo(),
                  fff.getName(),
                  linkType);
                relatedProperties.get(pmhm.getRelatedMapping().getType()).add(bilto);
                propertyMappersForEntity.add(bilto);
              }
            } else {
              if (pmhm.getType() == RelationshipType.HIERARCHIC) {

                MultiValuedPropertyInfo<BeanValueInfo> mpi = (MultiValuedPropertyInfo<BeanValueInfo>)pm.getInfo();
                AnyChildMultiValueMapper valueMapper;
                if (mpi instanceof MapPropertyInfo) {
                  valueMapper = new AnyChildMultiValueMapper.Map();
                } else if (mpi instanceof CollectionPropertyInfo) {
                  if (mpi instanceof ListPropertyInfo) {
                    valueMapper = new AnyChildMultiValueMapper.List();
                  } else {
                    valueMapper = new AnyChildMultiValueMapper.Collection();
                  }
                } else {
                  throw new IllegalStateException();
                }
                JCRAnyChildParentPropertyMapper<C> bilto = new JCRAnyChildParentPropertyMapper<C>(contextType, mpi, valueMapper);
                relatedProperties.get(pmhm.getRelatedMapping().getType()).add(bilto);
                propertyMappers.add(bilto);
              }
            }
          }
        } else if (pmvm instanceof SimpleMapping) {
          SimpleMapping sm = (SimpleMapping)pmvm;
          JCRMemberMapping jcrMember = sm.getJCRMember();

          //
          if (jcrMember instanceof JCRPropertyMapping) {
            JCRPropertyMapping jcrProperty = (JCRPropertyMapping)jcrMember;
            JCRPropertyListPropertyMapper<C, ?, ?> mapper = createPropertyListPM(contextType, pm, jcrProperty);
            propertyMappers.add(mapper);
          }
        } else if (pmvm instanceof PropertyMapMapping) {
          JCRPropertyMapPropertyMapper<C> bilto = new JCRPropertyMapPropertyMapper<C>(contextType, (MapPropertyInfo)pm.getInfo());
          propertyMappers.add(bilto);
        }
      }
    }

    //
    for (MethodMapping methodMapping : typeMapping.getMethodMappings()) {
      if (methodMapping instanceof CreateMapping) {
        CreateMapping createMapping = (CreateMapping)methodMapping;
        MethodMapper.Create<C> createMapper = new MethodMapper.Create<C>((Method)createMapping.getMethod().getMethod());
        methodMappers.add(createMapper);
        Set<MethodMapper.Create> alffl = relatedMethods.get(createMapping.getType());
        alffl.add(createMapper);
      } else if (methodMapping instanceof DestroyMapping) {
        MethodMapper.Destroy destroyMapper = new MethodMapper.Destroy((Method)methodMapping.getMethod().getMethod());
        methodMappersForE.add(destroyMapper);
      } else if (methodMapping instanceof FindByIdMapping) {
        FindByIdMapping findMapping = (FindByIdMapping)methodMapping;
        MethodMapper.FindById<C> findMapper = new MethodMapper.FindById<C>((Method)findMapping.getMethod().getMethod(), findMapping.getType());
        methodMappers.add(findMapper);
      } else {
        throw new UnsupportedOperationException();
      }
    }

    Instrumentor objectInstrumentor;
    if (typeMapping.getType().getName().equals(Object.class.getName())) {
      objectInstrumentor = NULL_INSTRUMENTOR;
    } else {
      objectInstrumentor = instrumentor;
    }

    //
    ObjectMapper<C> mapper;
    if (typeMapping.isPrimary()) {
      // Get the formatter
      ObjectFormatter formatter = null;
      if (typeMapping.getFormatterClass() != null) {
        formatter = ObjectInstantiator.newInstance(typeMapping.getFormatterClass());
      }

      // propertyMappers
      Set<PropertyMapper<?, EntityContext>> tmp = new HashSet<PropertyMapper<?, EntityContext>>(propertyMappersForEntity);

      // methodMappers
      Set<MethodMapper<EntityContext>> tmp2 = new HashSet<MethodMapper<EntityContext>>(methodMappersForE);

      //
      if (propertyMappersForEmbedded.size() > 0) {
        throw new AssertionError();
      }

      //
      for (PropertyMapper<?, C> pm : propertyMappers) {
        tmp.add((PropertyMapper<?, EntityContext>)pm);
      }

      //
      for (MethodMapper<C> pm : methodMappers) {
        tmp2.add((MethodMapper<EntityContext>)pm);
      }

      //
      mapper = (ObjectMapper<C>)new ObjectMapper<EntityContext>(
        (Class<?>)typeMapping.getType().getType(),
        tmp,
        tmp2,
        typeMapping.getOnDuplicate(),
        formatter,
        objectInstrumentor,
        typeMapping.getTypeName(),
        typeMapping.getKind());
    } else {
      if (propertyMappersForEntity.size() > 0) {
        throw new AssertionError();
      }

      //
      if (methodMappersForE.size() > 0) {
        throw new AssertionError();
      }

      // propertyMappers
      Set<PropertyMapper<?, EmbeddedContext>> tmp = new HashSet<PropertyMapper<?, EmbeddedContext>>(propertyMappersForEmbedded);

      // methodMappers
      Set<MethodMapper<EmbeddedContext>> tmp2 = new HashSet<MethodMapper<EmbeddedContext>>();

      //
      for (PropertyMapper<?, C> pm : propertyMappers) {
        tmp.add((PropertyMapper<?, EmbeddedContext>)pm);
      }

      //
      for (MethodMapper<C> pm : methodMappers) {
        tmp2.add((MethodMapper<EmbeddedContext>)pm);
      }

      //
      mapper = (ObjectMapper<C>)new ObjectMapper<EmbeddedContext>(
        (Class<?>)typeMapping.getType().getType(),
        tmp,
        tmp2,
        typeMapping.getOnDuplicate(),
        null,
        objectInstrumentor,
        typeMapping.getTypeName(),
        typeMapping.getKind());
    }

    // Finish wiring
    for (PropertyMapper pm : propertyMappers) {
      pm.mapper = mapper;
    }

    //
    for (MethodMapper methodMapper : methodMappers) {
      methodMapper.mapper = mapper;
    }

    //
    return mapper;
  }

  private <C extends ObjectContext, V, I> JCRPropertyPropertyMapper<C, V, I> createPropertyPM(
    Class<C> contextType,
    PropertyMapping<?> pm,
    JCRPropertyMapping<I> jcrProperty) {
    return new JCRPropertyPropertyMapper<C, V, I>(
      contextType,
      (SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(),
      jcrProperty.getName(),
      jcrProperty.getDefaultValue(),
      jcrProperty.getJCRType());
  }

  private static <C extends ObjectContext, V, I> JCRPropertyListPropertyMapper<C, V, I> createPropertyListPM(
    Class<C> contextType,
    PropertyMapping<?> pm,
    JCRPropertyMapping<I> jcrProperty) {
    return new JCRPropertyListPropertyMapper<C, V, I>(
      contextType,
      (MultiValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(),
      jcrProperty.getName(),
      jcrProperty.getJCRType(),
      jcrProperty.getDefaultValue());
  }
}