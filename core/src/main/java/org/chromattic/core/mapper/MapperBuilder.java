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
import org.chromattic.core.mapper.property.JCRPropertyListPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyMapPropertyMapper;
import org.chromattic.core.mapper.property.JCRPropertyPropertyMapper;
import org.chromattic.core.vt2.ValueTypeFactory;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping2.AttributeMapping;
import org.chromattic.metamodel.mapping2.BeanMapping;
import org.chromattic.metamodel.mapping2.CreateMapping;
import org.chromattic.metamodel.mapping2.DestroyMapping;
import org.chromattic.metamodel.mapping2.FindByIdMapping;
import org.chromattic.metamodel.mapping2.MappingVisitor;
import org.chromattic.metamodel.mapping2.PropertiesMapping;
import org.chromattic.metamodel.mapping2.RelationshipMapping;
import org.chromattic.metamodel.mapping2.ValueMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.spi.instrument.ProxyFactory;
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
  private static final ProxyFactory<?> NULL_PROXY_FACTORY = new ProxyFactory<Object>() {
    public Object createProxy(MethodHandler invoker) {
      throw new UnsupportedOperationException("Cannot create proxy for " + invoker);
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
  private final SimpleTypeResolver simpleTypeResolver;

  /** . */
  private final ValueTypeFactory valueTypeFactory;

  /** . */
  private final Instrumentor instrumentor;

  public MapperBuilder(
      SimpleTypeResolver simpleTypeResolver,
      Instrumentor instrumentor) {
    this.simpleTypeResolver = simpleTypeResolver;
    this.valueTypeFactory = new ValueTypeFactory(simpleTypeResolver);
    this.instrumentor = instrumentor;
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
//    Set<MethodMapper<EntityContext>> methodMappersForE;
    Set<PropertyMapper<?, ?, ?>> propertyMappers;
//    Set<PropertyMapper<?, ?, EntityContext>> propertyMappersForEntity;
//    Set<PropertyMapper<?, ?, EmbeddedContext>> propertyMappersForEmbedded;

    @Override
    public void start() {
      this.beanMappers = new HashMap<BeanMapping, ObjectMapper<?>>();
      this.createMethods = new SetMap<BeanMapping, MethodMapper.Create>();
    }

    @Override
    public void startBean(BeanMapping mapping) {
      this.beanMapping = mapping;
      this.contextType = mapping.getNodeTypeKind() == NodeTypeKind.PRIMARY ? EntityContext.class : EmbeddedContext.class;
      this.propertyMappers = new HashSet<PropertyMapper<?,?,?>>();
      this.methodMappers = new HashSet<MethodMapper<?>>();
//      this.methodMappersForE = new HashSet<MethodMapper<EntityContext>>();
//      this.propertyMappersForEntity = new HashSet<PropertyMapper<?, ?, EntityContext>>();
//      this.propertyMappersForEmbedded = new HashSet<PropertyMapper<?, ?, EmbeddedContext>>();
    }

    @Override
    public void singleValueMapping(ValueMapping.Single mapping) {
      SimpleTypeProvider vt = valueTypeFactory.create(mapping.getValue().getDeclaredType(), mapping.getPropertyDefinition().getMetaType());
      JCRPropertyPropertyMapper mapper = new JCRPropertyPropertyMapper(contextType, vt, mapping);
      propertyMappers.add(mapper);
    }

    @Override
    public void multiValueMapping(ValueMapping.Multi mapping) {
      SimpleTypeProvider vt = valueTypeFactory.create(mapping.getValue().getDeclaredType(), mapping.getPropertyDefinition().getMetaType());
      JCRPropertyListPropertyMapper mapper = new JCRPropertyListPropertyMapper(contextType, vt, mapping);
      propertyMappers.add(mapper);
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
      switch (mapping.getProperty().getKind()) {
        case MAP:
          valueMapper = new AnyChildMultiValueMapper.Map();
          break;
        case LIST:
          valueMapper = new AnyChildMultiValueMapper.List();
          break;
        case COLLECTION:
          valueMapper = new AnyChildMultiValueMapper.Collection();
          break;
        default:
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
      JCRPropertyMapPropertyMapper mapper = new JCRPropertyMapPropertyMapper(contextType, mapping);
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

      Instrumentor objectInstrumentor;
      if (beanMapping.getBean().getClassType().getName().equals(Object.class.getName())) {
        objectInstrumentor = NULL_INSTRUMENTOR;
      } else {
        objectInstrumentor = instrumentor;
      }

      ObjectMapper<?> mapper;
      if (beanMapping.getNodeTypeKind() == NodeTypeKind.PRIMARY) {

        // Get the formatter
        ObjectFormatter formatter = null;
        if (beanMapping.getFormatterClassType() != null) {
          Class<? extends ObjectFormatter> formatterClass = (Class<ObjectFormatter>)beanMapping.getFormatterClassType().getType();
          formatter = ObjectInstantiator.newInstance(formatterClass);
        }

        mapper = new ObjectMapper(
            beanMapping.isAbstract(),
            (Class<?>)beanMapping.getBean().getClassType().getType(),
            propertyMappers,
            methodMappers,
            beanMapping.getOnDuplicate(),
            formatter,
            objectInstrumentor,
            beanMapping.getNodeTypeName(),
            beanMapping.getNodeTypeKind()
        );

      } else {

        mapper = new ObjectMapper(
            beanMapping.isAbstract(),
            (Class<?>)beanMapping.getBean().getClassType().getType(),
            propertyMappers,
            methodMappers,
            beanMapping.getOnDuplicate(),
            null,
            objectInstrumentor,
            beanMapping.getNodeTypeName(),
            beanMapping.getNodeTypeKind()
        );
      }

      //
      beanMappers.put(beanMapping, mapper);
    }

    @Override
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
