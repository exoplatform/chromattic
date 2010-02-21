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
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.SimpleType;
import org.chromattic.metamodel.mapping.*;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeBuilder extends BaseTypeMappingVisitor {

  /** . */
  private final NodeTypeVisitor builder;

  /** . */
  private final Map<NodeTypeMapping, NodeType> nodeTypes;

  /** . */
  private NodeType current;

  public NodeTypeBuilder(NodeTypeVisitor builder) {
    this.builder = builder;
    this.nodeTypes = new HashMap<NodeTypeMapping, NodeType>();
  }

  private NodeType resolve(NodeTypeMapping mapping) {
    NodeType nodeType = nodeTypes.get(mapping);
    if (nodeType == null) {
      nodeType = new NodeType(mapping);
      nodeTypes.put(mapping, nodeType);
    }
    return nodeType;
  }

  public void start() {
    builder.start();
  }

  @Override
  protected void startMapping(NodeTypeMapping mapping) {
    current = resolve(mapping);
    builder.startType(mapping.getTypeName(), mapping.getKind() == NodeTypeKind.PRIMARY);
  }

  @Override
  protected void propertyMapping(JCRPropertyMapping propertyMapping, PropertyInfo<SimpleValueInfo> propertyInfo) {
    current.properties.put(propertyMapping.getName(), new Property(propertyMapping, propertyInfo));
  }

  @Override
  protected void propertyMapMapping() {
    current.properties.put("*", new Property("*", false, PropertyType.UNDEFINED));
  }

  @Override
  protected void oneToManyByReference(String relatedName, NodeTypeMapping relatedMapping) {
    resolve(relatedMapping).properties.put(relatedName, new Property(relatedName, false, PropertyType.REFERENCE));
  }

  @Override
  protected void oneToManyByPath(String relatedName, NodeTypeMapping relatedMapping) {
    resolve(relatedMapping).properties.put(relatedName, new Property(relatedName, false, PropertyType.PATH));
  }

  @Override
  protected void oneToManyHierarchic(NodeTypeMapping relatedMapping) {
    current.children.get("*").add(relatedMapping);
  }

  @Override
  protected void manyToOneByReference(String name, NodeTypeMapping relatedType) {
    current.properties.put(name, new Property(name, false, PropertyType.REFERENCE));
  }

  @Override
  protected void manyToOneByPath(String name, NodeTypeMapping relatedMapping) {
    current.properties.put(name, new Property(name, false, PropertyType.PATH));
  }

  @Override
  protected void manyToOneHierarchic(NodeTypeMapping relatedMapping) {
    resolve(relatedMapping).children.get("*").add(current.mapping);
  }

  @Override
  protected void oneToOneHierarchic(String name, NodeTypeMapping relatedMapping, boolean owner) {
    if (owner) {
      current.children.get(name).add(relatedMapping);
    } else {
      resolve(relatedMapping).children.get(name).add(current.mapping);
    }
  }

  @Override
  protected void endMapping() {
    current = null;
  }

  public void end() {
    for (NodeType nodeType : nodeTypes.values()) {
      builder.startType(nodeType.mapping.getTypeName(), nodeType.mapping.getKind() == NodeTypeKind.PRIMARY);

      //
      for (Property property : nodeType.properties.values()) {
        builder.addProperty(property.name, property.multiple, property.type);
      }

      //
      for (String childName : nodeType.children.keySet()) {
        Set<NodeTypeMapping> children = nodeType.children.get(childName);

        // Try to find the common ancestor type of all types
        NodeTypeMapping ancestorMapping = null;
        foo:
        for (NodeTypeMapping relatedMapping1 : children) {
          for (NodeTypeMapping relatedMapping2 : children) {
            if (!relatedMapping1.getType().isAssignableFrom(relatedMapping2.getType())) {
              continue foo;
            }
          }
          ancestorMapping = relatedMapping1;
          break;
        }

        //
        String typeName = ancestorMapping == null ? "nt:base" : ancestorMapping.getTypeName();

        //
        builder.addChildNodeDefinition(childName, typeName);
      }
      builder.endType();
    }
    builder.end();
  }
}
