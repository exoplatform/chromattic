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

package org.chromattic.core.mapper2;

import org.chromattic.core.EmbeddedContext;
import org.chromattic.core.EntityContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.onetomany.hierarchical.AnyChildMultiValueMapper;
import org.chromattic.core.mapper2.nodeattribute.JCRNodeAttributePropertyMapper;
import org.chromattic.core.mapper2.onetomany.hierarchical.JCRAnyChildCollectionPropertyMapper;
import org.chromattic.core.mapper2.onetomany.hierarchical.JCRAnyChildParentPropertyMapper;
import org.chromattic.core.mapper2.onetomany.reference.JCRNamedReferentPropertyMapper;
import org.chromattic.core.mapper2.onetomany.reference.JCRReferentCollectionPropertyMapper;
import org.chromattic.core.mapper2.onetoone.embedded.JCREmbeddedParentPropertyMapper;
import org.chromattic.core.mapper2.onetoone.embedded.JCREmbeddedPropertyMapper;
import org.chromattic.core.mapper2.onetoone.hierarchical.JCRNamedChildParentPropertyMapper;
import org.chromattic.core.mapper2.onetoone.hierarchical.JCRNamedChildPropertyMapper;
import org.chromattic.core.mapper2.property.JCRPropertyListPropertyMapper;
import org.chromattic.core.mapper2.property.JCRPropertyMapPropertyMapper;
import org.chromattic.core.mapper2.property.JCRPropertyPropertyMapper;
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
import org.chromattic.spi.type.SimpleTypeProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

  public void build(Collection<BeanMapping> beanMappings) {

    Context ctx = new Context();

    ctx.start();

    for (BeanMapping beanMapping : beanMappings) {
      beanMapping.accept(ctx);
    }

    ctx.end();


  }

  private class Context extends MappingVisitor {

    private BeanMapping beanMapping;

    private Class<? extends ObjectContext> contextType;

    private List<PropertyMapper<?, ?, ?>> propertyMappers;

    private List<MethodMapper<?>> methodMappers;

    @Override
    public void startBean(BeanMapping mapping) {
      this.beanMapping = mapping;
      this.contextType = mapping.getNodeTypeKind() == NodeTypeKind.PRIMARY ? EntityContext.class : EmbeddedContext.class;
      this.propertyMappers = new ArrayList<PropertyMapper<?,?,?>>();
      this.methodMappers = new ArrayList<MethodMapper<?>>();
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
      MethodMapper mapper = new MethodMapper.Create((Method)mapping.getMethod().getMethod());
      methodMappers.add(mapper);
    }

    @Override
    public void visit(DestroyMapping mapping) {
      MethodMapper mapper = new MethodMapper.Destroy((Method)mapping.getMethod().getMethod());
      methodMappers.add(mapper);
    }

    @Override
    public void visit(FindByIdMapping mapping) {
      try {
        MethodMapper mapper = new MethodMapper.FindById((Method)mapping.getMethod().getMethod(), mapping.getType());
        methodMappers.add(mapper);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedOperationException(e);
      }
    }

    @Override
    public void endBean() {

    }
  }

}
