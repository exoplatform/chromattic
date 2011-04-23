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

package org.chromattic.metamodel.typegen;

import org.chromattic.api.RelationshipType;
import org.chromattic.common.JCR;
import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.annotations.NotReferenceable;
import org.chromattic.metamodel.annotations.Skip;
import org.chromattic.metamodel.bean.BeanInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.BeanMappingBuilder;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.MappingVisitor;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;
import org.chromattic.metamodel.mapping.ValueMapping;
import org.chromattic.metamodel.mapping.jcr.PropertyMetaType;
import org.chromattic.metamodel.mapping.PropertiesMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SchemaBuilder {

  /** . */
  private final SimpleTypeResolver simpleTypeResolver;

  public SchemaBuilder() {
    this(new SimpleTypeResolver());
  }

  public SchemaBuilder(SimpleTypeResolver simpleTypeResolver) {
    this.simpleTypeResolver = simpleTypeResolver;
  }

  public Map<ClassTypeInfo, NodeType> build(Collection<BeanMapping> mappings) {
    Map<ClassTypeInfo, NodeType> schema = new HashMap<ClassTypeInfo, NodeType>();
    Visitor visitor = new Visitor();
    for (BeanMapping mapping : mappings) {
      mapping.accept(visitor);
      BeanInfo bean = mapping.getBean();
      if (bean.isDeclared()) {
        ClassTypeInfo key = bean.getClassType();
        schema.put(key, visitor.getNodeType(key));
      }
    }
    visitor.end();
    return schema;
  }

  public Map<ClassTypeInfo, NodeType> build(Set<ClassTypeInfo> classTypes) {
    BeanMappingBuilder amp = new BeanMappingBuilder(simpleTypeResolver);
    Map<ClassTypeInfo, BeanMapping> mappings = amp.build(classTypes);
    return build(mappings.values());
  }

  private static class Visitor extends MappingVisitor {

    /** . */
    private final LinkedHashMap<ClassTypeInfo, NodeType> nodeTypes;

    /** . */
    private NodeType current;

    /** . */
    private final SetMap<ClassTypeInfo, ClassTypeInfo> embeddedSuperTypesMap;

    private Visitor() {
      this.nodeTypes = new LinkedHashMap<ClassTypeInfo, NodeType>();
      this.embeddedSuperTypesMap = new SetMap<ClassTypeInfo, ClassTypeInfo>();
    }

    public NodeType getNodeType(ClassTypeInfo type) {
      return nodeTypes.get(type);
    }

    private NodeType resolve(BeanMapping mapping) {
      NodeType nodeType = nodeTypes.get(mapping.getBean().getClassType());
      if (nodeType == null) {
        if (mapping.getBean().getAnnotation(Skip.class) == null) {
          boolean referenceable = mapping.getBean().getAnnotation(NotReferenceable.class) == null;
          nodeType = new NodeType(mapping, referenceable);
          nodeTypes.put(mapping.getBean().getClassType(), nodeType);
        }
      }
      return nodeType;
    }

    @Override
    public void singleValueMapping(ValueMapping<ValueKind.Single> mapping) {
      if (current != null) {
        if (mapping.getValue().getValueKind() == ValueKind.SINGLE) {
          if (mapping.isTypeCovariant() && mapping.getProperty().getAnnotation(Skip.class) == null) {
            current.properties.put(mapping.getPropertyDefinition().getName(), new PropertyDefinition(mapping.getPropertyDefinition(), false));
          }
        } else {
          if (mapping.isTypeCovariant() && mapping.getProperty().getAnnotation(Skip.class) == null) {
            current.properties.put(mapping.getPropertyDefinition().getName(), new PropertyDefinition(mapping.getPropertyDefinition(), true));
          }
        }
      }
    }

    @Override
    public void oneToManyReference(RelationshipMapping.OneToMany.Reference mapping) {
      if (mapping.isTypeCovariant() && mapping.getProperty().getAnnotation(Skip.class) == null) {
        BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
        NodeType related = resolve(relatedBeanMapping);
        int propertyType = mapping.getType() == RelationshipType.REFERENCE ? PropertyType.REFERENCE : PropertyType.PATH;
        related.properties.put(mapping.getMappedBy(), new PropertyDefinition(mapping.getMappedBy(), false, propertyType));
      }
    }

    @Override
    public void manyToOneReference(RelationshipMapping.ManyToOne.Reference mapping) {
      if (mapping.isTypeCovariant() && mapping.getProperty().getAnnotation(Skip.class) == null) {
        int propertyType = mapping.getType() == RelationshipType.REFERENCE ? PropertyType.REFERENCE : PropertyType.PATH;
        current.properties.put(mapping.getMappedBy(), new PropertyDefinition(mapping.getMappedBy(), false, propertyType));
      }
    }

    @Override
    public void propertiesMapping(PropertiesMapping<?> mapping) {
      if (current != null) {
        if (mapping.getProperty().getAnnotation(Skip.class) == null) {
          PropertyMetaType metatype = mapping.getMetaType();
          int code = metatype != null ? metatype.getCode() : PropertyType.UNDEFINED;
          PropertyDefinition pd = current.properties.get("*");
          if (pd == null) {
            current.properties.put("*", new PropertyDefinition("*", false, code));
          } else {
            if (pd.getType() != code) {
              current.properties.put("*", new PropertyDefinition("*", false, PropertyType.UNDEFINED));            }
          }
        }
      }
    }

    @Override
    public void manyToOneHierarchic(RelationshipMapping.ManyToOne.Hierarchic mapping) {
      if (current != null) {
        if (mapping.isTypeCovariant()) {
          BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
          NodeType related = resolve(relatedBeanMapping);
          if (related != null) {
            related.addChildNodeType("*", false, false, current.mapping);
          }
        }
      }
    }

    @Override
    public void oneToManyHierarchic(RelationshipMapping.OneToMany.Hierarchic mapping) {
      if (current != null) {
        BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
        if (mapping.isTypeCovariant()) {
          current.addChildNodeType("*", false, false, relatedBeanMapping);
        }
      }
    }

    @Override
    public void oneToOneEmbedded(RelationshipMapping.OneToOne.Embedded mapping) {
      if (current != null) {
        BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
        if (mapping.isOwner()) {
          if (relatedBeanMapping.getNodeTypeKind() == NodeTypeKind.PRIMARY) {
            embeddedSuperTypesMap.get(current.mapping.getBean().getClassType()).add(relatedBeanMapping.getBean().getClassType());
          }
        } else {
          if (current.mapping.getNodeTypeKind() == NodeTypeKind.PRIMARY) {
            embeddedSuperTypesMap.get(relatedBeanMapping.getBean().getClassType()).add(current.mapping.getBean().getClassType());
          }
        }
      }
    }

    @Override
    public void oneToOneHierarchic(RelationshipMapping.OneToOne.Hierarchic mapping) {
      if (current != null) {
        if (mapping.isTypeCovariant()) {
          BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
          if (mapping.isOwner()) {
            current.addChildNodeType(
                JCR.qualify(mapping.getPrefix(), mapping.getLocalName()),
                mapping.getMandatory(),
                mapping.getAutocreated(),
                relatedBeanMapping);
          } else {
            NodeType related = resolve(relatedBeanMapping);
            if (related != null) {
              related.addChildNodeType(
                  JCR.qualify(mapping.getPrefix(), mapping.getLocalName()),
                  false,
                  mapping.getAutocreated(),
                  current.mapping);
            }
          }
        }
      }
    }

    @Override
    public void startBean(BeanMapping mapping) {
      current = resolve(mapping);
    }

    @Override
    public void endBean() {
      current = null;
    }

    public void end() {
      // Resolve super types
      for (NodeType nodeType : nodeTypes.values()) {
        ClassTypeInfo cti = nodeType.mapping.getBean().getClassType();

        // Take all delcared node types and find out which are the super types
        // based on the relationship between the java types
        for (NodeType otherNodeType : nodeTypes.values()) {
          if (otherNodeType != nodeType) {
            if (cti.isSubType((otherNodeType).mapping.getBean().getClassType())) {
              nodeType.superTypes.add(otherNodeType);
            }
          }
        }

        // Add the embedded super types
        for (ClassTypeInfo embeddedSuperTypeInfo : embeddedSuperTypesMap.get(cti)) {
          nodeType.superTypes.add(nodeTypes.get(embeddedSuperTypeInfo));
        }

        // Now resolve the minimum set of declared super types
        foo:
        for (NodeType superNodeType : nodeType.superTypes) {
          for (NodeType otherSuperNodeType : nodeType.superTypes) {
            if (otherSuperNodeType != superNodeType && ((NodeType)otherSuperNodeType).mapping.getBean().getClassType().isSubType(((NodeType)superNodeType).mapping.getBean().getClassType())) {
              continue foo;
            }
          }
          nodeType.declaredSuperTypes.add(superNodeType);
        }
      }
    }
  }
}
