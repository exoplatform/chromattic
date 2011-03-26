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

import org.chromattic.core.mapping.TypeMapping;
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
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildPropertyMapper;
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildParentPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyMapPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyListPropertyMapper;
import org.chromattic.core.mapper.nodeattribute.JCRNodeAttributePropertyMapper;
import org.chromattic.core.jcr.NodeDef;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.core.bean.SingleValuedPropertyInfo;
import org.chromattic.core.bean.MultiValuedPropertyInfo;
import org.chromattic.core.bean.MapPropertyInfo;
import org.chromattic.core.bean.CollectionPropertyInfo;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.bean.BeanValueInfo;
import org.chromattic.api.RelationshipType;
import org.chromattic.api.BuilderException;
import org.chromattic.api.format.CodecFormat;
import org.chromattic.api.format.DefaultNodeNameFormat;
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
  private final Set<TypeMapping> typeMappings;

  /** . */
  private final Instrumentor instrumentor;

  public TypeMapperBuilder(Set<TypeMapping> typeMappings, Instrumentor instrumentor) {
    this.typeMappings = typeMappings;
    this.instrumentor = instrumentor;
  }

  public Collection<TypeMapper> build() {
    try {
      return _build();
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Collection<TypeMapper> _build() throws ClassNotFoundException {

    Map<String, TypeMapper> mappers = new HashMap<String, TypeMapper>();

    SetMap<ClassTypeInfo, RelatedPropertyMapper> relatedProperties = new SetMap<ClassTypeInfo, RelatedPropertyMapper>();
    SetMap<ClassTypeInfo, MethodMapper.Create> relatedMethods = new SetMap<ClassTypeInfo, MethodMapper.Create>();

    for (TypeMapping typeMapping : typeMappings) {

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
                  JCRAnyChildParentPropertyMapper bilto = new JCRAnyChildParentPropertyMapper((MultiValuedPropertyInfo<BeanValueInfo>)pm.getInfo());
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
      HashSet<String> mixinNames = new HashSet<String>();
      for (String mixinName : typeMapping.getMixinNames()) {
        mixinNames.add(mixinName);
      }
      NodeDef nodeDef = new NodeDef(typeMapping.getNodeTypeName(), mixinNames);

      //
      CodecFormat<String, String> nameFormat;
      if (typeMapping.getNameCodec() == DefaultNodeNameFormat.class) {
        nameFormat = DefaultNodeNameFormat.getInstance();
      } else {
        try {
          nameFormat = typeMapping.getNameCodec().newInstance();
        }
        catch (InstantiationException e) {
          throw new BuilderException(e);
        }
        catch (IllegalAccessException e) {
          throw new BuilderException(e);
        }
      }

      //
      TypeMapper mapper = new TypeMapper(
        (Class<?>)typeMapping.getObjectClass().getType(),
        nameFormat,
        propertyMappers,
        methodMappers,
        nodeDef,
        instrumentor);

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
      Set<TypeMapper> relatedTypes = new HashSet<TypeMapper>();
      for (TypeMapper type : mappers.values()) {
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
      TypeMapper relatedMapper = mappers.get(relatedType.getName());
      if (relatedMapper == null) {
        throw new IllegalStateException("Could not find mapper for " + relatedType.getName() + " referenced by " + methods);
      }
      for (MethodMapper.Create createMapper : methods) {
        createMapper.type = relatedMapper;
      }
    }

    //
    return new ArrayList<TypeMapper>(mappers.values());
  }
}