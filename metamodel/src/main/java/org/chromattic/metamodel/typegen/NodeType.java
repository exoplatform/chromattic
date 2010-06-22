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

import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping2.BeanMapping;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeType {

  /** . */
  final String name;

  /** . */
  final String className;

  /** . */
  final boolean mixin;

  /** . */
  final Map<String, NodeDefinition> children;

  /** . */
  final Map<String, PropertyDefinition> properties;

  /** . */
  final Set<NodeType> superTypes;

  /** . */
  final Set<NodeType> declaredSuperTypes;

  /** . */
  final boolean skip;

  /** . */
  final boolean orderable;

  NodeType(NodeTypeMapping mapping, boolean skip) {
    this.name = mapping.getTypeName();
    this.className = mapping.getType().getName();
    this.mixin = mapping.isMixin();
    this.orderable = mapping.isOrderable();
    this.children = new HashMap<String, NodeDefinition>();
    this.properties = new HashMap<String, PropertyDefinition>();
    this.superTypes = new HashSet<NodeType>();
    this.declaredSuperTypes = new HashSet<NodeType>();
    this.skip = skip;
  }

  NodeType(BeanMapping mapping, boolean skip) {
    this.name = mapping.getNodeTypeName();
    this.className = mapping.getBean().getClassType().getName();
    this.mixin = mapping.getNodeTypeKind() == NodeTypeKind.MIXIN;
    this.orderable = mapping.isOrderable();
    this.children = new HashMap<String, NodeDefinition>();
    this.properties = new HashMap<String, PropertyDefinition>();
    this.superTypes = new HashSet<NodeType>();
    this.declaredSuperTypes = new HashSet<NodeType>();
    this.skip = skip;
  }

  public String getClassName() {
    return className;
  }

  public boolean isOrderable() {
    return orderable;
  }

  public Collection<NodeType> getSuperTypes() {
    return superTypes;
  }

  public Set<NodeType> getDeclaredSuperTypes() {
    return declaredSuperTypes;
  }

  public PropertyDefinition getPropertyDefinition(String propertyName) {
    return properties.get(propertyName);
  }

  public Map<String, PropertyDefinition> getPropertyDefinitions() {
    return properties;
  }

  public String getName() {
    return name;
  }

  public boolean isMixin() {
    return mixin;
  }

  public boolean isPrimary() {
    return !mixin;
  }

  public Map<String, NodeDefinition> getChildNodeDefinitions() {
    return children;
  }

  public NodeDefinition getChildNodeDefinition(String childNodeName) {
    return children.get(childNodeName);
  }

  void addChildNodeType(String childNodeName, boolean mandatory, boolean autocreated, NodeTypeMapping childNodeTypeMapping) {
    NodeDefinition1 nodeDefinition = (NodeDefinition1)children.get(childNodeName);
    if (nodeDefinition == null) {
      nodeDefinition = new NodeDefinition1(childNodeName, mandatory, autocreated);
      children.put(childNodeName, nodeDefinition);
    }
    nodeDefinition.mappings.add(childNodeTypeMapping);
  }

  void addChildNodeType(String childNodeName, boolean mandatory, boolean autocreated, BeanMapping childNodeTypeMapping) {
    NodeDefinition2 nodeDefinition = (NodeDefinition2)children.get(childNodeName);
    if (nodeDefinition == null) {
      nodeDefinition = new NodeDefinition2(childNodeName, mandatory, autocreated);
      children.put(childNodeName, nodeDefinition);
    }
    nodeDefinition.mappings.add(childNodeTypeMapping);
  }

  @Override
  public String toString() {
    return "NodeType[name=" + name + "]";
  }
}
