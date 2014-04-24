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

package org.chromattic.core.mapper;

import org.chromattic.api.NameConflictResolution;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.core.MethodInvoker;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.property.JCRPropertyMapper;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.reflext.api.MethodInfo;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectMapper<C extends ObjectContext<C>> {

  /** . */
  private final BeanMapping mapping;

  /** . */
  protected final Class<?> objectClass;

  /** . */
  private final String nodeTypeName;

  /** . */
  final Set<MethodMapper<C>> methodMappers;

  /** . */
  final Set<PropertyMapper<?, ?, C, ?>> propertyMappers;

  /** . */
  private final Map<Method, MethodInvoker<C>> dispatchers;

  /** The optional formatter for this object. */
  private final ObjectFormatter formatter;

  /** . */
  private final NameConflictResolution onDuplicate;

  /** . */
  private final NodeTypeKind kind;

  /** . */
  private final boolean abstract_;

  /** . */
  private final Map<String, PropertyMapper<?, ?, C, ?>> propertyMapperMap;

  /** . */
  private final Map<String, ValueDefinition<?, ?>> properties;

  /** . */
  private final String propertiesPattern;

  public ObjectMapper(
    BeanMapping mapping,
    boolean abstract_,
    Class<?> objectClass,
    Set<PropertyMapper<?, ?, C, ?>> propertyMappers,
    Set<MethodMapper<C>> methodMappers,
    NameConflictResolution onDuplicate,
    ObjectFormatter formatter,
    String typeName,
    NodeTypeKind kind) {

    // Build the mapper map
    Map<String, PropertyMapper<?, ?, C, ?>> propertyMapperMap = new HashMap<String, PropertyMapper<?, ?, C, ?>>();
    for (PropertyMapper<?, ?, C, ?> propertyMapper : propertyMappers) {
      propertyMapperMap.put(propertyMapper.getInfo().getName(), propertyMapper);
    }

    // Build the dispatcher map
    Map<Method, MethodInvoker<C>> dispatchers = new HashMap<Method, MethodInvoker<C>>();
    for (PropertyMapper<?, ?, C, ?> propertyMapper : propertyMappers) {
      PropertyMapping<?, ?, ?> info = propertyMapper.getInfo();
      MethodInfo getter = info.getProperty().getGetter();
      if (getter != null) {
        dispatchers.put((Method)getter.unwrap(), propertyMapper.getGetter());
      }
      MethodInfo setter = info.getProperty().getSetter();
      if (setter != null) {
        dispatchers.put((Method)setter.unwrap(), propertyMapper.getSetter());
      }
    }
    for (MethodMapper<C> methodMapper : methodMappers) {
      dispatchers.put((Method)methodMapper.getMethod().unwrap(), methodMapper);
    }

    // Compute properties
    Map<String, ValueDefinition<?, ?>> properties = new HashMap<String, ValueDefinition<?, ?>>();
    for (PropertyMapper<?, ?, ?, ?> pm : propertyMappers) {
      if (pm instanceof JCRPropertyMapper) {
        JCRPropertyMapper jpm = ((JCRPropertyMapper)pm);
        String propertyName = jpm.getJCRPropertyName();
        properties.put(propertyName, jpm.getValueDefinition());
      }
    }
    properties = Collections.unmodifiableMap(properties);

    // Compute properties pattern once
    StringBuilder propertiesPattern = new StringBuilder();
    for (String propertyName : properties.keySet()) {
      if (propertiesPattern.length() > 0) {
        propertiesPattern.append('|');
      }
      propertiesPattern.append(propertyName);
    }

    //
    this.mapping = mapping;
    this.abstract_ = abstract_;
    this.dispatchers = dispatchers;
    this.objectClass = objectClass;
    this.methodMappers = methodMappers;
    this.formatter = formatter;
    this.onDuplicate = onDuplicate;
    this.propertyMappers = propertyMappers;
    this.nodeTypeName = typeName;
    this.kind = kind;
    this.propertyMapperMap = propertyMapperMap;
    this.properties = properties;
    this.propertiesPattern = propertiesPattern.toString();
  }

  public ObjectMapper(ObjectMapper<C> mapper, ObjectFormatter formatter) {
    this.mapping = mapper.mapping;
    this.abstract_ = mapper.abstract_;
    this.dispatchers = mapper.dispatchers;
    this.objectClass = mapper.objectClass;
    this.methodMappers = mapper.methodMappers;
    this.formatter = formatter;
    this.onDuplicate = mapper.onDuplicate;
    this.propertyMappers = mapper.propertyMappers;
    this.nodeTypeName = mapper.nodeTypeName;
    this.kind = mapper.kind;
    this.propertyMapperMap = mapper.propertyMapperMap;
    this.properties = mapper.properties;
    this.propertiesPattern = mapper.propertiesPattern;
  }

  public ObjectMapper<C> with(ObjectFormatter formatter) {
    return new ObjectMapper<C>(this, formatter);
  }

  public Map<String, ValueDefinition<?, ?>> getProperties() {
    return properties;
  }

  public String getPropertiesPattern() {
    return propertiesPattern;
  }

  public MethodInvoker<C> getInvoker(Method method) {
    return dispatchers.get(method);
  }

  public BeanMapping getMapping() {
    return mapping;
  }

  public boolean isAbstract() {
    return abstract_; 
  }

  public NodeTypeKind getKind() {
    return kind;
  }

  public String getNodeTypeName() {
    return nodeTypeName;
  }

  public ObjectFormatter getFormatter() {
    return formatter;
  }

  public Set<MethodMapper<C>> getMethodMappers() {
    return methodMappers;
  }

  public Set<PropertyMapper<?, ?, C, ?>> getPropertyMappers() {
    return propertyMappers;
  }

  public PropertyMapper<?, ?, C, ?> getPropertyMapper(String name) {
    return propertyMapperMap.get(name);
  }

  public Class<?> getObjectClass() {
    return objectClass;
  }

  public NameConflictResolution getOnDuplicate() {
    return onDuplicate;
  }

  @Override
  public String toString() {
    return "EntityMapper[class=" + objectClass + ",typeName=" + nodeTypeName + "]";
  }
}