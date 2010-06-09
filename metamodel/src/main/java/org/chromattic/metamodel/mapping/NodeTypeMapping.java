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

package org.chromattic.metamodel.mapping;

import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.metamodel.mapping.jcr.NodeTypeDefinitionMapping;
import org.chromattic.metamodel.mapping.value.ValueMapping;
import org.reflext.api.ClassTypeInfo;
import org.chromattic.api.NameConflictResolution;

import java.util.Collections;
import java.util.Set;

/**
 * The meta data for the mapping of a java class to a node type and set of mixins. Note that
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeMapping
{

  public static NodeTypeMapping createMixinType(
    TypeMappingDomain domain,
    ClassTypeInfo objectClass,
    Set<PropertyMapping<? extends ValueMapping>> propertyMappings,
    Set<MethodMapping> methodMappings,
    NameConflictResolution onDuplicate,
    NodeTypeDefinitionMapping nodeTypeDefinitionMapping) {
    return new NodeTypeMapping(
      domain,
      objectClass,
      propertyMappings,
      methodMappings,
      onDuplicate,
      null,
      nodeTypeDefinitionMapping);
  }

  public static NodeTypeMapping createPrimaryType(
    TypeMappingDomain domain,
    ClassTypeInfo objectClass,
    Set<PropertyMapping<? extends ValueMapping>> propertyMappings,
    Set<MethodMapping> methodMappings,
    NameConflictResolution onDuplicate,
    Class<? extends ObjectFormatter> formatterClass,
    NodeTypeDefinitionMapping nodeTypeDefinitionMapping) {
    return new NodeTypeMapping(
      domain,
      objectClass,
      propertyMappings,
      methodMappings,
      onDuplicate,
      formatterClass,
      nodeTypeDefinitionMapping);
  }

  /** . */
  private final TypeMappingDomain domain;

  /** . */
  protected final ClassTypeInfo type;

  /** . */
  private final Set<PropertyMapping<? extends ValueMapping>> propertyMappings;

  /** . */
  private final Set<MethodMapping> methodMappings;

  /** . */
  private final NameConflictResolution onDuplicate;

  /** . */
  private Class<? extends ObjectFormatter> formatterClass;

  /** . */
  private final NodeTypeDefinitionMapping nodeTypeDefinitionMapping;

  public NodeTypeMapping(
    TypeMappingDomain domain,
    ClassTypeInfo type,
    Set<PropertyMapping<? extends ValueMapping>> propertyMappings,
    Set<MethodMapping> methodMappings,
    NameConflictResolution onDuplicate,
    Class<? extends ObjectFormatter> formatterClass,
    NodeTypeDefinitionMapping nodeTypeDefinitionMapping) {

    //
    this.domain = domain;
    this.type = type;
    this.propertyMappings = Collections.unmodifiableSet(propertyMappings);
    this.methodMappings = Collections.unmodifiableSet(methodMappings);
    this.onDuplicate = onDuplicate;
    this.formatterClass = formatterClass;
    this.nodeTypeDefinitionMapping = nodeTypeDefinitionMapping;
  }

  public boolean isAbstract() {
    return nodeTypeDefinitionMapping.isAbstract();
  }

  public boolean isOrderable() {
    return nodeTypeDefinitionMapping.isOrderable();
  }

  public TypeMappingDomain getDomain() {
    return domain;
  }

  public boolean isPrimary() {
    return getKind() == NodeTypeKind.PRIMARY;
  }

  public boolean isMixin() {
    return getKind() == NodeTypeKind.MIXIN;
  }

  public NodeTypeKind getKind() {
    return nodeTypeDefinitionMapping.getKind();
  }

  public String getTypeName() {
    return nodeTypeDefinitionMapping.getTypeName();
  }

  public ClassTypeInfo getType() {
    return type;
  }

  public Class<? extends ObjectFormatter> getFormatterClass() {
    return formatterClass;
  }

  public PropertyMapping<? extends ValueMapping> getPropertyMapping(String propertyName) {
    for (PropertyMapping<? extends ValueMapping> propertyMapping : propertyMappings) {
      if (propertyMapping.getName().equals(propertyName)) {
        return propertyMapping;
      }
    }
    return null;
  }

  public Set<PropertyMapping<? extends ValueMapping>> getPropertyMappings() {
    return propertyMappings;
  }

  public Set<MethodMapping> getMethodMappings() {
    return methodMappings;
  }

  public NameConflictResolution getOnDuplicate() {
    return onDuplicate;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof NodeTypeMapping) {
      NodeTypeMapping that = (NodeTypeMapping)obj;
      return type.equals(that.type);
    }
    return false;
  }

  @Override
  public String toString() {
    return "NodeTypeMapping[type=" + type + "]";
  }
}