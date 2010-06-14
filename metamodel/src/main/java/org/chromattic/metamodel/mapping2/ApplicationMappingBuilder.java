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

import org.chromattic.api.annotations.*;
import org.chromattic.metamodel.bean2.*;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.type.SimpleTypeMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.reflext.api.ClassTypeInfo;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ApplicationMappingBuilder {



  public ApplicationMapping build(Set<ClassTypeInfo> classTypes) {

    ApplicationMapping app = new ApplicationMapping();

    //
    Collection<BeanInfo> beans = new BeanInfoBuilder().build(classTypes).values();

    //
    Context ctx = new Context(new SimpleTypeResolver(), new HashSet<BeanInfo>(beans));

    //
    ctx.build();

    //
    return app;
  }

  private class Context {

    /** . */
    final SimpleTypeResolver typeResolver;

    /** . */
    final Set<BeanInfo> beans;

    /** . */
    final Map<BeanInfo, NodeTypeMapping> mappings;

    private Context(SimpleTypeResolver typeResolver, Set<BeanInfo> beans) {
      this.typeResolver = typeResolver;
      this.beans = beans;
      this.mappings = new HashMap<BeanInfo, NodeTypeMapping>();
    }

    public void build() {
      while (true) {
        Iterator<BeanInfo> iterator = beans.iterator();
        if (iterator.hasNext()) {
          BeanInfo bean = iterator.next();
          resolve(bean);
        } else {
          break;
        }
      }
    }

    private NodeTypeMapping resolve(BeanInfo bean) {
      NodeTypeMapping mapping = mappings.get(bean);
      if (mapping == null) {
        if (beans.remove(bean)) {
          mapping = new NodeTypeMapping(bean);
          mappings.put(bean, mapping);
          build(mapping);
        } else {
          throw new AssertionError();
        }
      }
      return mapping;
    }

    private void build(NodeTypeMapping beanMapping) {

      BeanInfo bean = beanMapping.bean;

      // First build the parent mapping
      beanMapping.parent = resolve(bean.getParent());

      //
      Map<String, PropertyMapping<?, ?>> properties = new HashMap<String, PropertyMapping<?, ?>>();
      for (PropertyInfo<?> property : bean.getProperties().values()) {

        // Determine kind
        Collection<? extends Annotation> annotations = property.getAnnotateds(
            Property.class,
            OneToOne.class,
            OneToMany.class,
            ManyToOne.class
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
              } else {
                throw new UnsupportedOperationException();
              }
            } else if (value instanceof BeanValueInfo) {
              if (annotation instanceof OneToOne) {
                OneToOne oneToOne =  (OneToOne)annotation;
                switch (oneToOne.type()) {
                  case HIERARCHIC:
                    mapping = createHierarchicOneToOne((SingleValuedPropertyInfo<BeanValueInfo>)property);
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
                    mapping = createReferenceManyToOne((SingleValuedPropertyInfo<BeanValueInfo>)property);
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
              } else {
                throw new UnsupportedOperationException();
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
                    mapping = createReferenceOneToMany((MultiValuedPropertyInfo<BeanValueInfo>)property);
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
          } else {
            throw new AssertionError();
          }
        }

        //
        properties.put(mapping.property.getName(), mapping);
      }

      //
      beanMapping.properties = properties;
    }

    private <P extends PropertyInfo<SimpleValueInfo>> PropertyMapping<P, SimpleValueInfo> createProperty(
        Property propertyAnnotation,
        P property) {

      //
      PropertyMetaType<?> propertyMetaType = PropertyMetaType.get(propertyAnnotation.type());

      //
      SimpleTypeMapping abc = typeResolver.resolveType(property.getValue().getType(), propertyMetaType);
      if (abc == null) {
        throw new UnsupportedOperationException("No simple type mapping for " + property.getValue().getType());
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
      PropertyMapping<P, SimpleValueInfo> mapping;
      mapping = new SimplePropertyMapping<P>(property, propertyDefinition);
      return mapping;
    }

    private RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>> createReferenceOneToMany(MultiValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.OneToMany.Reference());
      return mapping;
    }

    private RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>> createHierarchicOneToMany(MultiValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<MultiValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.OneToMany.Hierarchic());
      return mapping;
    }

    private RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> createReferenceManyToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.ManyToOne.Reference());
      return mapping;
    }

    private RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> createHierarchicManyToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.ManyToOne.Hierarchic());
      return mapping;
    }

    private RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> createEmbeddedOneToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.OneToOne.Embedded());
      return mapping;
    }

    private RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> createHierarchicOneToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>> mapping;
      mapping = new RelationshipPropertyMapping<SingleValuedPropertyInfo<BeanValueInfo>>(property, new Relationship.OneToOne.Hierarchic());
      return mapping;
    }
  }
}
