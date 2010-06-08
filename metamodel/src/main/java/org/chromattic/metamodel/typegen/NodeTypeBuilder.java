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

package org.chromattic.metamodel.typegen;

import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.mapping.BaseTypeMappingVisitor;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.mapping.value.NamedOneToOneMapping;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeBuilder extends BaseTypeMappingVisitor {

  /** . */
  private final LinkedHashMap<ClassTypeInfo, NodeType> nodeTypes;

  /** . */
  private NodeType current;

  /** . */
  private final SetMap<ClassTypeInfo, ClassTypeInfo> embeddedSuperTypesMap;

  public NodeTypeBuilder() {
    this.nodeTypes = new LinkedHashMap<ClassTypeInfo, NodeType>();
    this.embeddedSuperTypesMap = new SetMap<ClassTypeInfo, ClassTypeInfo>();
  }

  public NodeType getNodeType(ClassTypeInfo type) {
    return nodeTypes.get(type);
  }

  private NodeType resolve(NodeTypeMapping mapping) {
    NodeType nodeType = nodeTypes.get(mapping.getType());
    if (nodeType == null) {
      nodeType = new NodeType(mapping);
      nodeTypes.put(mapping.getType(), nodeType);
    }
    return nodeType;
  }

  public void start() {
    nodeTypes.clear();
  }

  @Override
  protected void startMapping(NodeTypeMapping mapping) {
    current = resolve(mapping);
  }

  @Override
  protected <V> void propertyMapping(ClassTypeInfo definer, JCRPropertyMapping propertyMapping, boolean multiple) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(propertyMapping.getName(), new PropertyDefinition(propertyMapping, multiple));
    }
  }

  @Override
  protected void propertyMapMapping(ClassTypeInfo definer) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put("*", new PropertyDefinition("*", false, PropertyType.UNDEFINED));
    }
  }

  @Override
  protected void oneToManyByReference(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).properties.put(relatedName, new PropertyDefinition(relatedName, false, PropertyType.REFERENCE));
    }
  }

  @Override
  protected void oneToManyByPath(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).properties.put(relatedName, new PropertyDefinition(relatedName, false, PropertyType.PATH));
    }
  }

  @Override
  protected void oneToManyHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      current.addChildNodeType("*", false, relatedMapping);
    }
  }

  @Override
  protected void manyToOneByReference(ClassTypeInfo definer, String name, NodeTypeMapping relatedType) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(name, new PropertyDefinition(name, false, PropertyType.REFERENCE));
    }
  }

  @Override
  protected void oneToOneEmbedded(ClassTypeInfo definer, NodeTypeMapping relatedMapping, boolean owner) {
    if (owner) {
      if (relatedMapping.isPrimary()) {
        embeddedSuperTypesMap.get(current.mapping.getType()).add(relatedMapping.getType());
      }
    } else {
      if (current.mapping.isPrimary()) {
        embeddedSuperTypesMap.get(relatedMapping.getType()).add(current.mapping.getType());
      }
    }
  }

  @Override
  protected void manyToOneByPath(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(name, new PropertyDefinition(name, false, PropertyType.PATH));
    }
  }

  @Override
  protected void manyToOneHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).addChildNodeType("*", false, current.mapping);
    }
  }

  @Override
  protected void oneToOneHierarchic(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping, int mode) {
    if (definer.equals(current.mapping.getType())) {
      switch (mode) {
        case NamedOneToOneMapping.MODE_OWNER:
          current.addChildNodeType(name, false, relatedMapping);
          break;
        case NamedOneToOneMapping.MODE_MANDATORY_OWNER:
          current.addChildNodeType(name, true, relatedMapping);
          break;
        case NamedOneToOneMapping.MODE_OWNED:
          resolve(relatedMapping).addChildNodeType(name, false, current.mapping);
          break;
        default:
          throw new AssertionError();
      }
    }
  }

  @Override
  protected void endMapping() {
    current = null;
  }

  public void end() {

    // Resolve super types
    for (NodeType nodeType : nodeTypes.values()) {
      ClassTypeInfo cti = nodeType.mapping.getType();

      // Take all delcared node types and find out which are the super types
      // based on the relationship between the java types
      for (NodeType otherNodeType : nodeTypes.values()) {
        if (otherNodeType != nodeType) {
          if (cti.isSubType(otherNodeType.mapping.getType())) {
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
          if (otherSuperNodeType != superNodeType && otherSuperNodeType.mapping.getType().isSubType(superNodeType.mapping.getType())) {
            continue foo;
          }
        }
        nodeType.declaredSuperTypes.add(superNodeType);
      }
    }
  }

  public void writeTo(Writer writer) throws Exception {
    new XMLNodeTypeSerializer(new ArrayList<NodeType>(nodeTypes.values())).writeTo(writer);
    new CNDNodeTypeSerializer(new ArrayList<NodeType>(nodeTypes.values())).writeTo(writer);
  }
}
