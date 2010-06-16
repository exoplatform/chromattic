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

import org.chromattic.metamodel.bean2.BeanInfo;
import org.chromattic.metamodel.mapping.NodeTypeKind;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanMapping {

  /** . */
  final BeanInfo bean;

  /** . */
  BeanMapping parent;

  /** . */
  final Map<String, PropertyMapping<?, ?>> properties;

  /** . */
  final Map<String, PropertyMapping<?, ?>> unmodifiableProperties;

  /** . */
  final NodeTypeKind nodeTypeKind;

  /** . */
  final String nodeTypeName;

  /** . */
  final boolean orderable;

  /** . */
  final boolean abstract_;

  public BeanMapping(
      BeanInfo bean,
      NodeTypeKind nodeTypeKind,
      String nodeTypeName,
      boolean orderable,
      boolean abstract_) {
    this.bean = bean;
    this.nodeTypeKind = nodeTypeKind;
    this.nodeTypeName = nodeTypeName;
    this.orderable = orderable;
    this.abstract_ = abstract_;
    this.properties = new HashMap<String, PropertyMapping<?,?>>();
    this.unmodifiableProperties = Collections.unmodifiableMap(properties);
  }

  public NodeTypeKind getNodeTypeKind() {
    return nodeTypeKind;
  }

  public String getNodeTypeName() {
    return nodeTypeName;
  }

  public boolean isOrderable() {
    return orderable;
  }

  public boolean isAbstract() {
    return abstract_;
  }

  public BeanInfo getBean() {
    return bean;
  }

  public Map<String, PropertyMapping<?, ?>> getProperties() {
    return properties;
  }

  public <M extends PropertyMapping<?, ?>> M getPropertyMapping(String name, Class<M> type) {
    PropertyMapping<?, ?> mapping = properties.get(name);
    if (type.isInstance(mapping)) {
      return type.cast(mapping);
    } else {
      return null;
    }
  }

  public void accept(MappingVisitor visitor) {
    visitor.startMapping(this);
    for (PropertyMapping<?, ?> property : properties.values()) {
      property.accept(visitor);
    }
    visitor.endMapping();
  }
}
