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
package org.chromattic.core;

import javax.jcr.RepositoryException;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.Node;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.NodeType;

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.common.logging.Logger;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.mapper.MethodMapper;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.core.bean.PropertyInfo;
import org.chromattic.core.bean.SimpleValueInfo;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectContext implements MethodHandler {

  /** . */
  private final Logger log = Logger.getLogger(ObjectContext.class);

  /** . */
  final TypeMapper mapper;

  /** . */
  final Object object;

  /** . */
  ContextState state;

  public ObjectContext(TypeMapper mapper) {
    this(mapper, null);
  }

  public ObjectContext(TypeMapper mapper, String name) {
    this.state = null;
    this.mapper = mapper;
    this.object = mapper.createObject(this);
    this.state = new TransientContextState(name);
  }

  public DomainSession getSession() {
    return state.getSession();
  }

  public Status getStatus() {
    return state.getStatus();
  }

  public Object getObject() {
    return object;
  }

  public String getAttribute(NodeAttributeType type) {
    switch (type) {
      case NAME:
        return state.getName();
      case ID:
        return state.getId();
      case PATH:
        return state.getPath();
      case WORKSPACE_NAME:
        return state.getSession().getJCRSession().getWorkspace().getName();
      default:
        throw new AssertionError();
    }
  }

  public void remove() {
    state.getSession().remove(this);
  }

  public <T> Iterator<T> getReferences(final String name, Class<T> filterClass) {
    return state.getSession().getRelateds(this, name, filterClass);
  }

  public String getName() {
    return getAttribute(NodeAttributeType.NAME);
  }

  public String getId() {
    return getAttribute(NodeAttributeType.ID);
  }

  public String getPath() {
    return getAttribute(NodeAttributeType.PATH);
  }

  public void setName(String name) {
    state.setName(name);
  }

  public Object getRelated(String name) {
    return state.getSession().getRelated(this, name);
  }

  public void setRelated(String name, Object related) {
    DomainSession session = state.getSession();
    ObjectContext relatedCtx = null;
    if (related != null) {
      relatedCtx = session.unwrap(related);
    }

    //
    session.setRelated(this, name, relatedCtx);
  }

  public boolean addReference(String name, Object related) {
    DomainSession session = state.getSession();
    ObjectContext relatedCtx = session.unwrap(related);
    return session.setRelated(relatedCtx, name, this);
  }

  private ValueMapper<?> getValueMapper(SimpleValueInfo type) {
    switch (type.getSimpleType()) {
      case STRING:
        return ValueMapper.STRING;
      case INT:
        return ValueMapper.INTEGER;
      case LONG:
        return ValueMapper.LONG;
      case BOOLEAN:
        return ValueMapper.BOOLEAN;
      case FLOAT:
        return ValueMapper.FLOAT;
      case DOUBLE:
        return ValueMapper.DOUBLE;
      case DATE:
        return ValueMapper.DATE;
      case BINARY:
        return ValueMapper.BINARY;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public Map<String, Object> getPropertyMap() {
    return new PropertyMap(this);
  }

  public Object getPropertyValue(String propertyName, SimpleValueInfo type) {
    try {
      Value value;
      Node node = state.getNode();
      if (node.hasProperty(propertyName)) {
        Property property = node.getProperty(propertyName);
        PropertyDefinition def = property.getDefinition();
        if (def.isMultiple()) {
          Value[] values = property.getValues();
          if (values.length == 0) {
            value = null;
          } else {
            value = values[0];
          }
        } else {
          value = property.getValue();
        }
      } else {
        value = null;
      }

      //
      ValueMapper<?> valueMapper = getValueMapper(type);

      //
      if (value != null) {
        return valueMapper.get(value);
      } else {
        if (type.isPrimitive()) {
          throw new IllegalStateException("Cannot convert null to primitive type " + type.getSimpleType());
        }
        return null;
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <T> T getPropertyValues(String propertyName, SimpleValueInfo simpleType, ListType<T> listType) {
    try {
      Value[] values;
      Node node = state.getNode();
      if (node.hasProperty(propertyName)) {
        Property property = node.getProperty(propertyName);
        PropertyDefinition def = property.getDefinition();
        if (def.isMultiple()) {
          values = property.getValues();
        } else {
          values = new Value[]{property.getValue()};
        }
      } else {
        values = new Value[0];
      }
      ValueFactory valueFactory = state.getSession().getJCRSession().getValueFactory();
      ValueMapper<Object> valueMapper = (ValueMapper<Object>)getValueMapper(simpleType);

      //
      if (listType == ListType.LIST) {
        List<Object> list = new ArrayList<Object>(values.length);
        for (Value value : values) {
          Object o = valueMapper.get(value);
          list.add(o);
        }
        return (T)list;
      } else {
        Object array = Array.newInstance((Class<?>)simpleType.getTypeInfo().getType(), values.length);
        for (int i = 0;i < values.length;i++) {
          Value value = values[i];
          Object o = valueMapper.get(value);
          Array.set(array, i, o);
        }
        return (T)array;
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public void setPropertyValue(String propertyName, SimpleValueInfo type, Object o) {
    try {
      ValueMapper<?> valueMapper = getValueMapper(type);

      //
      Value value;
      if (o != null) {
        ValueFactory valueFactory = state.getSession().getJCRSession().getValueFactory();
        value = ((ValueMapper<Object>)valueMapper).get(valueFactory, o);
      } else {
        value = null;
      }

      //
      Node node = state.getNode();
      PropertyDefinition def = getPropertyDefinition(node, propertyName);
      if (def.isMultiple()) {
        if (value == null) {
          node.setProperty(propertyName, new Value[0]);
        } else {
          node.setProperty(propertyName, new Value[]{value});
        }
      } else {
        node.setProperty(propertyName, value);
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <T> void setPropertyValues(String propertyName, SimpleValueInfo type, ListType<T> listType, T objects) {
    if (objects == null) {
      throw new NullPointerException();
    }
    try {
      ValueFactory valueFactory = state.getSession().getJCRSession().getValueFactory();
      ValueMapper<Object> valueMapper = (ValueMapper<Object>)getValueMapper(type);
      Value[] values;
      if (listType == ListType.LIST) {
        List<?> list = (List<?>)objects;
        values = new Value[list.size()];
        int i = 0;
        for (Object object : list) {
          values[i++] = valueMapper.get(valueFactory, object);
        }
      } else {
        values = new Value[Array.getLength(objects)];
        for (int i = 0;i < values.length;i++) {
          Object o = Array.get(objects, i);
          values[i] = valueMapper.get(valueFactory, o);
        }
      }

      //
      Node node = state.getNode();
      PropertyDefinition def = getPropertyDefinition(node, propertyName);
      if (def.isMultiple()) {
        node.setProperty(propertyName, values);
      } else {
        if (values.length > 1) {
          throw new IllegalArgumentException("Cannot update with an array of length greater than 1");
        } else if (values.length == 1) {
          node.setProperty(propertyName, values[0]);
        } else {
          node.setProperty(propertyName, (Value)null);
        }
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  private static PropertyDefinition getPropertyDefinition(NodeType nodeType, String propertyName) throws RepositoryException {
    for (PropertyDefinition def : nodeType.getPropertyDefinitions()) {
      if (def.getName().equals(propertyName)) {
        return def;
      }
    }
    return null;
  }

  private static PropertyDefinition getPropertyDefinition(Node node, String propertyName) throws RepositoryException {
    if (node.hasProperty(propertyName)) {
      return node.getProperty(propertyName).getDefinition();
    } else {
      NodeType primaryNodeType = node.getPrimaryNodeType();
      PropertyDefinition def = getPropertyDefinition(primaryNodeType, propertyName);
      if (def == null) {
        for (NodeType mixinNodeType : node.getMixinNodeTypes()) {
          def = getPropertyDefinition(mixinNodeType, propertyName);
          if (def != null) {
            break;
          }
        }
      }
      return def;
    }
  }

  public void removeChild(String name) {
    if (getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException("Can only insert/remove a child of a persistent object");
    }

    //
    state.getSession().removeChild(this, name);
  }

  public void addChild(ObjectContext childCtx) {
    String name = childCtx.state.getName();
    addChild(name, childCtx);
  }

  public void addChild(Object child) {
    DomainSession session = state.getSession();
    ObjectContext childCtx = session.unwrap(child);
    addChild(childCtx);
  }

  public void addChild(String name, ObjectContext childCtx) {
    state.getSession().insert(this, name, childCtx);
  }

  public void addChild(String name, Object child) {
    DomainSession session = state.getSession();
    ObjectContext childCtx = session.unwrap(child);
    addChild(name, childCtx);
  }

  public Object getChild(String name) {
    return state.getSession().getChild(this, name);
  }

  public <T> Iterator<T> getChildren(Class<T> filterClass) {
    return state.getSession().getChildren(this, filterClass);
  }

  public Object getParent() {
    return state.getSession().getParent(this);
  }

  @Override
  public String toString() {
    return "ObjectContext[status=" + state + ",mapper=" + mapper + "]";
  }

  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    for (PropertyMapper propertyMapper : mapper.getPropertyMappers()) {
      PropertyInfo info = propertyMapper.getInfo();
      MethodInfo getter = info.getGetter();
      if (getter != null && method.equals(getter.getMethod())) {
        return propertyMapper.get(this);
      } else {
        MethodInfo setter = info.getSetter();
        if (setter != null && method.equals(info.getSetter().getMethod())) {
          propertyMapper.set(this, args[0]);
          return null;
        }
      }
    }

    //
    for (MethodMapper methodMapper : mapper.getMethodMappers()) {
      if (method.equals(methodMapper.getMethod())) {
        return methodMapper.invoke(this, args);
      }
    }

    //
    throw new AssertionError("Could not handle invocation of method " + method.getName());
  }
}
