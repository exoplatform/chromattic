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

package org.chromattic.core.mapper;

import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.common.collection.SetMap;
import org.chromattic.core.EmbeddedContext;
import org.chromattic.core.EntityContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.onetomany.hierarchical.AnyChildMultiValueMapper;
import org.chromattic.core.mapper.nodeattribute.JCRNodeAttributePropertyMapper;
import org.chromattic.core.mapper.onetomany.hierarchical.JCRAnyChildCollectionPropertyMapper;
import org.chromattic.core.mapper.onetomany.hierarchical.JCRAnyChildParentPropertyMapper;
import org.chromattic.core.mapper.onetomany.reference.JCRNamedReferentPropertyMapper;
import org.chromattic.core.mapper.onetomany.reference.JCRReferentCollectionPropertyMapper;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedParentPropertyMapper;
import org.chromattic.core.mapper.onetoone.embedded.JCREmbeddedPropertyMapper;
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildParentPropertyMapper;
import org.chromattic.core.mapper.onetoone.hierarchical.JCRNamedChildPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyDetypedPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyMultiValuedPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertySingleValuedPropertyMapper;
import org.chromattic.core.vt2.ValueTypeFactory;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.CreateMapping;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;
import org.chromattic.metamodel.mapping.AttributeMapping;
import org.chromattic.metamodel.mapping.DestroyMapping;
import org.chromattic.metamodel.mapping.FindByIdMapping;
import org.chromattic.metamodel.mapping.MappingVisitor;
import org.chromattic.metamodel.mapping.PropertiesMapping;
import org.chromattic.metamodel.mapping.ValueMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.chromattic.spi.type.SimpleTypeProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MapperBuilder {

  /** . */
  private final SimpleTypeResolver simpleTypeResolver;

  /** . */
  private final ValueTypeFactory valueTypeFactory;

  public MapperBuilder(SimpleTypeResolver simpleTypeResolver) {
    this.simpleTypeResolver = simpleTypeResolver;
    this.valueTypeFactory = new ValueTypeFactory(simpleTypeResolver);
  }

  public Collection<ObjectMapper<?>> build(Collection<BeanMapping> beanMappings) {

    Context ctx = new Context();

    ctx.start();

    for (BeanMapping beanMapping : beanMappings) {
      beanMapping.accept(ctx);
    }

    ctx.end();

    return ctx.beanMappers.values();
  }

  private class Context extends MappingVisitor {

    private BeanMapping beanMapping;

    private SetMap<BeanMapping, MethodMapper.Create> createMethods;

    private Map<BeanMapping, ObjectMapper<?>> beanMappers;

    private Class<? extends ObjectContext> contextType;

    Set<MethodMapper<?>> methodMappers;
    Set<PropertyMapper<?, ?, ?, ?>> propertyMappers;

    public void start() {
      this.beanMappers = new HashMap<BeanMapping, ObjectMapper<?>>();
      this.createMethods = new SetMap<BeanMapping, MethodMapper.Create>();
    }

    @Override
    public void startBean(BeanMapping mapping) {
      this.beanMapping = mapping;
      this.contextType = mapping.getNodeTypeKind() == NodeTypeKind.PRIMARY ? EntityContext.class : EmbeddedContext.class;
      this.propertyMappers = new HashSet<PropertyMapper<?, ?, ?, ?>>();
      this.methodMappers = new HashSet<MethodMapper<?>>();
    }

    @Override
    public void singleValueMapping(ValueMapping<ValueKind.Single> mapping) {
      if (mapping.getValue().getValueKind() == ValueKind.SINGLE) {
        SimpleTypeProvider vt = valueTypeFactory.create(mapping.getValue().getDeclaredType(), mapping.getPropertyDefinition().getMetaType());
        JCRPropertySingleValuedPropertyMapper mapper = new JCRPropertySingleValuedPropertyMapper(contextType, vt, mapping);
        propertyMappers.add(mapper);
      } else {
        SimpleTypeProvider vt = valueTypeFactory.create(mapping.getValue().getDeclaredType(), mapping.getPropertyDefinition().getMetaType());
        JCRPropertyMultiValuedPropertyMapper mapper = new JCRPropertyMultiValuedPropertyMapper(contextType, vt, mapping);
        propertyMappers.add(mapper);
      }
    }

    @Override
    public void multiValueMapping(ValueMapping<ValueKind.Multi> mapping) {
    }

    @Override
    public void oneToOneHierarchic(RelationshipMapping.OneToOne.Hierarchic mapping) {
      try {
        if (mapping.isOwner()) {
          JCRNamedChildParentPropertyMapper mapper = new JCRNamedChildParentPropertyMapper(contextType, mapping);
          propertyMappers.add(mapper);
        } else {
          JCRNamedChildPropertyMapper mapper = new JCRNamedChildPropertyMapper(mapping);
          propertyMappers.add(mapper);
        }
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void oneToManyHierarchic(RelationshipMapping.OneToMany.Hierarchic mapping) {
      AnyChildMultiValueMapper valueMapper;
      ValueKind valueKind = mapping.getProperty().getValueKind();
      if (valueKind instanceof ValueKind.Map) {
        valueMapper = new AnyChildMultiValueMapper.Map();
      } else if (valueKind instanceof ValueKind.List) {
        valueMapper = new AnyChildMultiValueMapper.List();
      } else if (valueKind instanceof ValueKind.Collection) {
        valueMapper = new AnyChildMultiValueMapper.Collection();
      } else {
        throw new AssertionError();
      }
      try {
        JCRAnyChildParentPropertyMapper mapper = new JCRAnyChildParentPropertyMapper(contextType, mapping, valueMapper);
        propertyMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void manyToOneHierarchic(RelationshipMapping.ManyToOne.Hierarchic mapping) {
      try {
        JCRAnyChildCollectionPropertyMapper mapper = new JCRAnyChildCollectionPropertyMapper(mapping);
        propertyMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void oneToManyReference(RelationshipMapping.OneToMany.Reference mapping) {
      try {
        JCRReferentCollectionPropertyMapper mapper = new JCRReferentCollectionPropertyMapper(mapping);
        propertyMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void manyToOneReference(RelationshipMapping.ManyToOne.Reference mapping) {
      try {
        JCRNamedReferentPropertyMapper mapper = new JCRNamedReferentPropertyMapper(contextType, mapping);
        propertyMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void oneToOneEmbedded(RelationshipMapping.OneToOne.Embedded mapping) {
      try {
        if (mapping.isOwner()) {
          JCREmbeddedParentPropertyMapper mapper = new JCREmbeddedParentPropertyMapper(mapping);
          propertyMappers.add(mapper);
        } else {
          JCREmbeddedPropertyMapper mapper = new JCREmbeddedPropertyMapper(mapping);
          propertyMappers.add(mapper);
        }
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void propertiesMapping(PropertiesMapping<?> mapping) {
      JCRPropertyDetypedPropertyMapper mapper = new JCRPropertyDetypedPropertyMapper(contextType, mapping);
      propertyMappers.add(mapper);
    }

    @Override
    public void attributeMapping(AttributeMapping mapping) {
      JCRNodeAttributePropertyMapper mapper = new JCRNodeAttributePropertyMapper(mapping);
      propertyMappers.add(mapper);
    }

    @Override
    public void visit(CreateMapping mapping) {
      MethodMapper.Create mapper = new MethodMapper.Create(mapping.getMethod());
      methodMappers.add(mapper);
      createMethods.get(mapping.getBeanMapping()).add(mapper);
    }

    @Override
    public void visit(DestroyMapping mapping) {
      MethodMapper mapper = new MethodMapper.Destroy(mapping.getMethod());
      methodMappers.add(mapper);
    }

    @Override
    public void visit(FindByIdMapping mapping) {
      try {
        MethodMapper mapper = new MethodMapper.FindById(mapping.getMethod(), mapping.getType());
        methodMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void endBean() {

      ObjectMapper<?> mapper;
      if (beanMapping.getNodeTypeKind() == NodeTypeKind.PRIMARY) {

        // Get the formatter
        ObjectFormatter formatter = null;
        if (beanMapping.getFormatterClassType() != null) {
          Class<? extends ObjectFormatter> formatterClass = (Class<ObjectFormatter>)beanMapping.getFormatterClassType().unwrap();
          formatter = ObjectInstantiator.newInstance(formatterClass);
        }

        mapper = new ObjectMapper(
            beanMapping,
            beanMapping.isAbstract(),
            (Class<?>)beanMapping.getBean().getClassType().unwrap(),
            propertyMappers,
            methodMappers,
            beanMapping.getOnDuplicate(),
            formatter,
            beanMapping.getNodeTypeName(),
            beanMapping.getNodeTypeKind()
        );

      } else {

        mapper = new ObjectMapper(
            beanMapping,
            beanMapping.isAbstract(),
            (Class<?>)beanMapping.getBean().getClassType().unwrap(),
            propertyMappers,
            methodMappers,
            beanMapping.getOnDuplicate(),
            null,
            beanMapping.getNodeTypeName(),
            beanMapping.getNodeTypeKind()
        );
      }

      //
      beanMappers.put(beanMapping, mapper);
    }

    public void end() {
      for (BeanMapping beanMapping : createMethods.keySet()) {
        ObjectMapper beanMapper = beanMappers.get(beanMapping);
        for (MethodMapper.Create createMapper : createMethods.get(beanMapping)) {
          createMapper.mapper = beanMapper ;
        }
      }
    }
  }
}
