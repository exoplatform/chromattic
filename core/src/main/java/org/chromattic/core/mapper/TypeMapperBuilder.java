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
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.core.EntityContext;
import org.chromattic.core.MixinContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedParentPropertyMapper;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedPropertyMapper;
import org.chromattic.core.mapping.MixinTypeMapping;
import org.chromattic.core.mapping.PrimaryTypeMapping;
import org.chromattic.core.mapping.NodeTypeMapping;
import org.chromattic.core.mapping.PropertyMapping;
import org.chromattic.core.mapping.MethodMapping;
import org.chromattic.core.mapping.CreateMapping;
import org.chromattic.core.mapping.DestroyMapping;
import org.chromattic.core.mapping.FindByIdMapping;
import org.chromattic.core.mapping.jcr.JCRMemberMapping;
import org.chromattic.core.mapping.jcr.JCRPropertyMapping;
import org.chromattic.core.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.core.mapping.value.ValueMapping;
import org.chromattic.core.mapping.value.SimpleMapping;
import org.chromattic.core.mapping.value.NamedOneToOneMapping;
import org.chromattic.core.mapping.value.ManyToOneMapping;
import org.chromattic.core.mapping.value.NamedManyToOneMapping;
import org.chromattic.core.mapping.value.RelationshipMapping;
import org.chromattic.core.mapping.value.OneToManyMapping;
import org.chromattic.core.mapping.value.NamedOneToManyMapping;
import org.chromattic.core.mapping.value.PropertyMapMapping;
import org.chromattic.common.SetMap;
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
import org.chromattic.core.bean.SingleValuedPropertyInfo;
import org.chromattic.core.bean.MultiValuedPropertyInfo;
import org.chromattic.core.bean.MapPropertyInfo;
import org.chromattic.core.bean.CollectionPropertyInfo;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.bean.BeanValueInfo;
import org.chromattic.core.bean.ListPropertyInfo;
import org.chromattic.api.RelationshipType;
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
public class TypeMapperBuilder {

  /** . */
  private final static EnumMap<RelationshipType, LinkType> relationshipToLinkMapping;

  static {
    EnumMap<RelationshipType, LinkType> tmp = new EnumMap<RelationshipType, LinkType>(RelationshipType.class);
    tmp.put(RelationshipType.REFERENCE, LinkType.REFERENCE);
    tmp.put(RelationshipType.PATH, LinkType.PATH);
    relationshipToLinkMapping = tmp;
  }

  /** . */
  private final Set<NodeTypeMapping> typeMappings;

  /** . */
  private final Instrumentor instrumentor;

  /** . */
  private final Map<ClassTypeInfo, NodeTypeMapping> classToMapping;

  public TypeMapperBuilder(Set<NodeTypeMapping> typeMappings, Instrumentor instrumentor) {
    Map<ClassTypeInfo, NodeTypeMapping> classToMapping = new HashMap<ClassTypeInfo, NodeTypeMapping>();
    for (NodeTypeMapping typeMapping : typeMappings) {
      classToMapping.put(typeMapping.getObjectClass(), typeMapping);
    }

    //
    this.typeMappings = typeMappings;
    this.instrumentor = instrumentor;
    this.classToMapping = classToMapping;
  }

  private SetMap<ClassTypeInfo, RelatedPropertyMapper> relatedProperties = new SetMap<ClassTypeInfo, RelatedPropertyMapper>();
  private SetMap<ClassTypeInfo, MethodMapper.Create> relatedMethods = new SetMap<ClassTypeInfo, MethodMapper.Create>();

  public Collection<NodeTypeMapper> build() {
    try {
      return _build();
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<NodeTypeMapper> _build() throws ClassNotFoundException {

    //
    Map<String, NodeTypeMapper> mappers = new HashMap<String, NodeTypeMapper>();


    for (NodeTypeMapping typeMapping : typeMappings) {
      Class<? extends ObjectContext> contextType = typeMapping instanceof PrimaryTypeMapping ? EntityContext.class : MixinContext.class;
      NodeTypeMapper<?> mapper = createMapper(contextType, typeMapping);
      mappers.put(typeMapping.getObjectClass().getName(), mapper);
    }

    // Resolve related types
    for (ClassTypeInfo relatedType : relatedProperties.keySet()) {

      //
      Set<RelatedPropertyMapper> properties = relatedProperties.get(relatedType);

      //
      Set<NodeTypeMapper> relatedTypes = new HashSet<NodeTypeMapper>();
      for (NodeTypeMapper type : mappers.values()) {
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
      NodeTypeMapper relatedMapper = mappers.get(relatedType.getName());
      if (relatedMapper == null) {
        throw new IllegalStateException("Could not find mapper for " + relatedType.getName() + " referenced by " + methods);
      }
      for (MethodMapper.Create createMapper : methods) {
        createMapper.type = relatedMapper;
      }
    }

    //
    return new ArrayList<NodeTypeMapper>(mappers.values());
  }

  private <C extends ObjectContext> NodeTypeMapper createMapper(Class<C> contextType, NodeTypeMapping typeMapping) throws ClassNotFoundException {

    //
    Set<MethodMapper<C>> methodMappers = new HashSet<MethodMapper<C>>();
    Set<MethodMapper<EntityContext>> methodMappersForE = new HashSet<MethodMapper<EntityContext>>();

    //
    Set<PropertyMapper<?, C>> propertyMappers = new HashSet<PropertyMapper<?, C>>();
    Set<PropertyMapper<?, EntityContext>> propertyMappersForE = new HashSet<PropertyMapper<?, EntityContext>>();
    Set<PropertyMapper<?, MixinContext>> propertyMappersForM = new HashSet<PropertyMapper<?, MixinContext>>();

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
            JCRPropertyPropertyMapper<C> bilto = new JCRPropertyPropertyMapper<C>(contextType, (SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), jcrProperty.getName());
            propertyMappers.add(bilto);
          } else if (jcrMember instanceof JCRNodeAttributeMapping) {
            JCRNodeAttributeMapping nam = (JCRNodeAttributeMapping)jcrMember;
            JCRNodeAttributePropertyMapper bilto = new JCRNodeAttributePropertyMapper((SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), nam.getType());
            if (contextType == EntityContext.class) {
              propertyMappersForE.add(bilto);
            } else {
              throw new UnsupportedOperationException("todo");
            }
          }
        } else if (pmvm instanceof RelationshipMapping) {
          RelationshipMapping pmhm = (RelationshipMapping)pmvm;

          //
          if (pmhm.getType() == RelationshipType.HIERARCHIC) {
            if (pmhm instanceof ManyToOneMapping) {
              JCRChildNodePropertyMapper bilto = new JCRAnyChildCollectionPropertyMapper(propertyInfo);
              relatedProperties.get(pmhm.getRelatedType()).add(bilto);
              propertyMappersForE.add(bilto);
            } if (pmhm instanceof NamedOneToOneMapping) {
              NamedOneToOneMapping ncpmpm = (NamedOneToOneMapping)pmhm;
              if (ncpmpm.isOwner()) {
                JCRNamedChildParentPropertyMapper<C> bilto = new JCRNamedChildParentPropertyMapper<C>(contextType, propertyInfo, ncpmpm.getName());
                relatedProperties.get(pmhm.getRelatedType()).add(bilto);
                propertyMappers.add(bilto);
              } else {
                JCRChildNodePropertyMapper bilto = new JCRNamedChildPropertyMapper(propertyInfo, ncpmpm.getName());
                relatedProperties.get(ncpmpm.getRelatedType()).add(bilto);
                propertyMappersForE.add(bilto);
              }
            }
          } else if (pmhm.getType() == RelationshipType.EMBEDDED) {
            NodeTypeMapping relatedMapping = classToMapping.get(propertyInfo.getValue().getTypeInfo());
            if (typeMapping instanceof PrimaryTypeMapping) {
              if (relatedMapping instanceof MixinTypeMapping) {
                JCREmbeddedParentPropertyMapper mapper = new JCREmbeddedParentPropertyMapper(propertyInfo);
                propertyMappersForE.add(mapper);
              } else {
                throw new BuilderException("Related class in mixin mapping must be annotated with @" + MixinType.class.getSimpleName());
              }
            } else if (typeMapping instanceof MixinTypeMapping) {
              if (relatedMapping instanceof PrimaryTypeMapping) {
                JCREmbeddedPropertyMapper mapper = new JCREmbeddedPropertyMapper(propertyInfo);
                propertyMappersForM.add(mapper);
              } else {
                throw new BuilderException("Related class in mixin mapping must be annotated with @" + NodeMapping.class.getSimpleName());
              }
            } else {
              throw new AssertionError();
            }
          }
        }

        //
        if (pmvm instanceof ManyToOneMapping) {
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
              relatedProperties.get(nmtovm.getRelatedType()).add(blah);
            }
          }
        }
      } else if (pm.getInfo() instanceof MultiValuedPropertyInfo) {
        ValueMapping pmvm = pm.getValueMapping();

        //
        if (pmvm instanceof RelationshipMapping) {
          RelationshipMapping pmhm = (RelationshipMapping)pmvm;

          //
          if (pmhm instanceof OneToManyMapping) {

            //
            if (pmhm instanceof NamedOneToManyMapping) {
              LinkType linkType = relationshipToLinkMapping.get(pmhm.getType());
              if (linkType != null) {
                NamedOneToManyMapping fff = (NamedOneToManyMapping)pmhm;
                JCRReferentCollectionPropertyMapper bilto = new JCRReferentCollectionPropertyMapper(
                  (CollectionPropertyInfo<BeanValueInfo>)pm.getInfo(),
                  fff.getName(),
                  linkType);
                relatedProperties.get(pmhm.getRelatedType()).add(bilto);
                propertyMappersForE.add(bilto);
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
                relatedProperties.get(pmhm.getRelatedType()).add(bilto);
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
            JCRPropertyListPropertyMapper<C> bilto = new JCRPropertyListPropertyMapper<C>(contextType, (MultiValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), jcrProperty.getName());
            propertyMappers.add(bilto);
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

    //
    NodeTypeMapper<C> mapper;
    if (typeMapping instanceof PrimaryTypeMapping) {
      PrimaryTypeMapping nodeTypeMapping = (PrimaryTypeMapping)typeMapping;

      // Get the formatter
      ObjectFormatter formatter = null;
      if (nodeTypeMapping.getFormatterClass() != null) {
        formatter = ObjectInstantiator.newInstance(nodeTypeMapping.getFormatterClass());
      }

      // propertyMappers
      Set<PropertyMapper<?, EntityContext>> tmp = new HashSet<PropertyMapper<?, EntityContext>>(propertyMappersForE);

      // methodMappers
      Set<MethodMapper<EntityContext>> tmp2 = new HashSet<MethodMapper<EntityContext>>(methodMappersForE);

      //
      if (propertyMappersForM.size() > 0) {
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
      mapper = (NodeTypeMapper<C>)new PrimaryTypeMapper(
        (Class<?>)typeMapping.getObjectClass().getType(),
        tmp,
        tmp2,
        typeMapping.getOnDuplicate(),
        instrumentor,
        formatter,
        nodeTypeMapping.getNodeTypeName());
    } else {
      MixinTypeMapping mixinTypeMapping = (MixinTypeMapping)typeMapping;

      //
      if (propertyMappersForE.size() > 0) {
        throw new AssertionError();
      }

      //
      if (methodMappersForE.size() > 0) {
        throw new AssertionError();
      }

      // propertyMappers
      Set<PropertyMapper<?, MixinContext>> tmp = new HashSet<PropertyMapper<?, MixinContext>>(propertyMappersForM);

      // methodMappers
      Set<MethodMapper<MixinContext>> tmp2 = new HashSet<MethodMapper<MixinContext>>();

      //
      for (PropertyMapper<?, C> pm : propertyMappers) {
        tmp.add((PropertyMapper<?, MixinContext>)pm);
      }

      //
      for (MethodMapper<C> pm : methodMappers) {
        tmp2.add((MethodMapper<MixinContext>)pm);
      }

      //
      mapper = (NodeTypeMapper<C>)new MixinTypeMapper(
        (Class<?>)typeMapping.getObjectClass().getType(),
        tmp,
        tmp2,
        typeMapping.getOnDuplicate(),
        instrumentor,
        mixinTypeMapping.getMixinTypeName());
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
}