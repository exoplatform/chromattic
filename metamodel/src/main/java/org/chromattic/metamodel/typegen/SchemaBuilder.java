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

import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.bean2.SimpleValueInfo;
import org.chromattic.metamodel.bean2.ValueInfo;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping2.*;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;
import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SchemaBuilder extends MappingVisitor {

  /** . */
  private final LinkedHashMap<ClassTypeInfo, NodeType2> nodeTypes;

  /** . */
  private NodeType2 current;

  /** . */
  private final SetMap<ClassTypeInfo, ClassTypeInfo> embeddedSuperTypesMap;

  public SchemaBuilder() {
    this.nodeTypes = new LinkedHashMap<ClassTypeInfo, NodeType2>();
    this.embeddedSuperTypesMap = new SetMap<ClassTypeInfo, ClassTypeInfo>();
  }

  public NodeType2 getNodeType(ClassTypeInfo type) {
    return nodeTypes.get(type);
  }

  private NodeType2 resolve(BeanMapping mapping) {
    NodeType2 nodeType = nodeTypes.get(mapping.getBean().getClassType());
    if (nodeType == null) {
//      boolean skip = mapping.getType().getDeclaredAnnotation(AnnotationType.get(Skip.class)) != null;
      nodeType = new NodeType2(mapping, false);
      nodeTypes.put(mapping.getBean().getClassType(), nodeType);
    }
    return nodeType;
  }

  @Override
  public void singleValueMapping(ValueMapping.Single mapping) {
    if (mapping.isNew()) {
      current.properties.put(mapping.getPropertyDefinition().getName(), new PropertyDefinition(mapping.getPropertyDefinition(), false));
    }
  }

  @Override
  public void multiValueMapping(ValueMapping.Multi mapping) {
    if (mapping.isNew()) {
      current.properties.put(mapping.getPropertyDefinition().getName(), new PropertyDefinition(mapping.getPropertyDefinition(), true));
    }
  }

  @Override
  public void propertiesMapping(PropertiesMapping<?> mapping) {

    // For now do simple until we figure out something better
    current.properties.put("*", new PropertyDefinition("*", false, PropertyType.UNDEFINED));

/*
    if (definer.equals(current.mapping.getType())) {
      int jcrType = metaType != null ? metaType.getCode() : PropertyType.UNDEFINED;
       PropertyDefinition pd = current.properties.get("*");
      if (pd != null) {
        if (pd.getType() != jcrType) {
          current.properties.put("*", new PropertyDefinition("*", false, PropertyType.UNDEFINED));
        }
      } else {
        current.properties.put("*", new PropertyDefinition("*", false, jcrType));
      }
    }
*/
  }

  @Override
  public void manyToOneHierarchic(RelationshipMapping.ManyToOne.Hierarchic mapping) {
    if (mapping.isNew()) {
      BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
      resolve(relatedBeanMapping).addChildNodeType("*", false, false, current.mapping);
    }
  }

  @Override
  public void oneToManyHierarchic(RelationshipMapping.OneToMany.Hierarchic mapping) {
    BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
    if (mapping.isNew()) {
      current.addChildNodeType("*", false, false, relatedBeanMapping);
    }
  }

  @Override
  public void oneToOneEmbedded(RelationshipMapping.OneToOne.Embedded mapping) {
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

  @Override
  public void oneToOneHierarchic(RelationshipMapping.OneToOne.Hierarchic mapping) {
    if (mapping.isNew()) {
      BeanMapping relatedBeanMapping = mapping.getRelatedBeanMapping();
      if (mapping.isOwner()) {
        current.addChildNodeType(
            mapping.getMappedBy(),
            mapping.getMandatory(),
            mapping.getAutocreated(),
            relatedBeanMapping);
      } else {
        resolve(relatedBeanMapping).addChildNodeType(
            mapping.getMappedBy(),
            false,
            mapping.getAutocreated(),
            current.mapping);
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

  @Override
  public void end() {
    // Resolve super types
    for (NodeType2 nodeType : nodeTypes.values()) {
      ClassTypeInfo cti = nodeType.mapping.getBean().getClassType();

      // Take all delcared node types and find out which are the super types
      // based on the relationship between the java types
      for (NodeType2 otherNodeType : nodeTypes.values()) {
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
          if (otherSuperNodeType != superNodeType && ((NodeType2)otherSuperNodeType).mapping.getBean().getClassType().isSubType(((NodeType2)superNodeType).mapping.getBean().getClassType())) {
            continue foo;
          }
        }
        nodeType.declaredSuperTypes.add(superNodeType);
      }
    }
  }
}
