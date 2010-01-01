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

import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.core.mapper.onetoone.mixin.JCRMixinParentPropertyMapper;
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

  public TypeMapperBuilder(Set<NodeTypeMapping> typeMappings, Instrumentor instrumentor) {
    this.typeMappings = typeMappings;
    this.instrumentor = instrumentor;
  }

  public Collection<NodeTypeMapper> build() {
    try {
      return _build();
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<NodeTypeMapper> _build() throws ClassNotFoundException {

    Map<String, NodeTypeMapper> mappers = new HashMap<String, NodeTypeMapper>();

    SetMap<ClassTypeInfo, RelatedPropertyMapper> relatedProperties = new SetMap<ClassTypeInfo, RelatedPropertyMapper>();
    SetMap<ClassTypeInfo, MethodMapper.Create> relatedMethods = new SetMap<ClassTypeInfo, MethodMapper.Create>();

    for (NodeTypeMapping typeMapping : typeMappings) {

      Set<MethodMapper> methodMappers = new HashSet<MethodMapper>();
      Set<PropertyMapper> propertyMappers = new HashSet<PropertyMapper>();

      for (PropertyMapping pm : typeMapping.getPropertyMappings()) {

        //
        if (pm.getInfo() instanceof SingleValuedPropertyInfo) {
          ValueMapping pmvm = pm.getValueMapping();

          //
          if (pmvm instanceof SimpleMapping) {
            SimpleMapping pmdm = (SimpleMapping)pmvm;
            JCRMemberMapping jcrMember = pmdm.getJCRMember();

            //
            if (jcrMember instanceof JCRPropertyMapping) {
              JCRPropertyMapping jcrProperty = (JCRPropertyMapping)jcrMember;
              JCRPropertyPropertyMapper bilto = new JCRPropertyPropertyMapper((SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), jcrProperty.getName());
              propertyMappers.add(bilto);
            } else if (jcrMember instanceof JCRNodeAttributeMapping) {
              JCRNodeAttributeMapping nam = (JCRNodeAttributeMapping)jcrMember;
              propertyMappers.add(new JCRNodeAttributePropertyMapper((SingleValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), nam.getType()));
            }
          } else if (pmvm instanceof RelationshipMapping) {
            RelationshipMapping pmhm = (RelationshipMapping)pmvm;

            //
            if (pmhm.getType() == RelationshipType.HIERARCHIC) {
              if (pmhm instanceof ManyToOneMapping) {
                JCRChildNodePropertyMapper bilto = new JCRAnyChildCollectionPropertyMapper((SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo());
                relatedProperties.get(pmhm.getRelatedType()).add(bilto);
                propertyMappers.add(bilto);
              } if (pmhm instanceof NamedOneToOneMapping) {
                NamedOneToOneMapping ncpmpm = (NamedOneToOneMapping)pmhm;
                if (ncpmpm.isOwner()) {
                  JCRNamedChildParentPropertyMapper bilto = new JCRNamedChildParentPropertyMapper((SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo(), ncpmpm.getName());
                  relatedProperties.get(pmhm.getRelatedType()).add(bilto);
                  propertyMappers.add(bilto);
                } else {
                  JCRChildNodePropertyMapper bilto = new JCRNamedChildPropertyMapper((SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo(), ncpmpm.getName());
                  relatedProperties.get(ncpmpm.getRelatedType()).add(bilto);
                  propertyMappers.add(bilto);
                }
              }
            } else if (pmhm.getType() == RelationshipType.MIXIN) {
              if (typeMapping instanceof PrimaryTypeMapping) {
                JCRMixinParentPropertyMapper mapper = new JCRMixinParentPropertyMapper((SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo());
                propertyMappers.add(mapper);
              } else if (typeMapping instanceof MixinTypeMapping) {
                throw new UnsupportedOperationException("todo");
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
                JCRNamedReferentPropertyMapper blah = new JCRNamedReferentPropertyMapper(
                  (SingleValuedPropertyInfo<BeanValueInfo>)pm.getInfo(),
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
                  propertyMappers.add(bilto);
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
                  JCRAnyChildParentPropertyMapper bilto = new JCRAnyChildParentPropertyMapper(mpi, valueMapper);
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
              JCRPropertyListPropertyMapper bilto = new JCRPropertyListPropertyMapper((MultiValuedPropertyInfo<SimpleValueInfo>)pm.getInfo(), jcrProperty.getName());
              propertyMappers.add(bilto);
            }
          } else if (pmvm instanceof PropertyMapMapping) {
            JCRPropertyMapPropertyMapper bilto = new JCRPropertyMapPropertyMapper((MapPropertyInfo)pm.getInfo());
            propertyMappers.add(bilto);
          }
        }
      }

      //
      for (MethodMapping methodMapping : typeMapping.getMethodMappings()) {
        if (methodMapping instanceof CreateMapping) {
          CreateMapping createMapping = (CreateMapping)methodMapping;
          MethodMapper.Create createMapper = new MethodMapper.Create((Method)createMapping.getMethod().getMethod());
          methodMappers.add(createMapper);
          Set<MethodMapper.Create> alffl = relatedMethods.get(createMapping.getType());
          alffl.add(createMapper);
        } else if (methodMapping instanceof DestroyMapping) {
          MethodMapper.Destroy destroyMapper = new MethodMapper.Destroy((Method)methodMapping.getMethod().getMethod());
          methodMappers.add(destroyMapper);
        } else if (methodMapping instanceof FindByIdMapping) {
          FindByIdMapping findMapping = (FindByIdMapping)methodMapping;
          MethodMapper.FindById findMapper = new MethodMapper.FindById((Method)findMapping.getMethod().getMethod(), findMapping.getType());
          methodMappers.add(findMapper);
        } else {
          throw new UnsupportedOperationException();
        }
      }

      //
      NodeTypeMapper mapper;
      if (typeMapping instanceof PrimaryTypeMapping) {
        PrimaryTypeMapping nodeTypeMapping = (PrimaryTypeMapping)typeMapping;

        // Get the formatter
        ObjectFormatter formatter = null;
        if (nodeTypeMapping.getFormatterClass() != null) {
          formatter = ObjectInstantiator.newInstance(nodeTypeMapping.getFormatterClass());
        }

        //
        mapper = new PrimaryTypeMapper(
          (Class<?>)typeMapping.getObjectClass().getType(),
          propertyMappers,
          methodMappers,
          typeMapping.getOnDuplicate(),
          instrumentor,
          formatter,
          nodeTypeMapping.getNodeTypeName());
      } else {
        MixinTypeMapping mixinTypeMapping = (MixinTypeMapping)typeMapping;

        //
        mapper = new MixinTypeMapper(
          (Class<?>)typeMapping.getObjectClass().getType(),
          propertyMappers,
          methodMappers,
          typeMapping.getOnDuplicate(),
          instrumentor,
          mixinTypeMapping.getMixinTypeName());
      }

      // Finish wiring
      for (PropertyMapper pm : propertyMappers) {
        pm.mapper = mapper;
      }
      for (MethodMapper methodMapper : methodMappers) {
        methodMapper.mapper = mapper;
      }

      //
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
}