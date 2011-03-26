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

import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.NoSuchPropertyException;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.bean.SimpleType;
import org.chromattic.core.mapper.ValueMapper;
import org.chromattic.core.jcr.info.NodeInfo;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.common.CopyingInputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.Property;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.lang.reflect.Array;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentEntityContextState extends EntityContextState {

  /** . */
  private final String name;

  /** . */
  private final String path;

  /** . */
  private final String id;

  /** . */
  private final Node node;

  /** . */
  private final DomainSession session;

  /** . */
  private final Map<String, Object> propertyCache;

  /** . */
  private final NodeInfo nodeInfo;

  PersistentEntityContextState(Node node, DomainSession session) throws RepositoryException {

    //
    this.id = node.getUUID();
    this.path = node.getPath();
    this.node = node;
    this.name = node.getName();
    this.session = session;
    this.propertyCache = session.domain.stateCacheEnabled ? new HashMap<String, Object>() : null;
    this.nodeInfo = session.domain.nodeInfoManager.getNodeInfo(node);
  }

  String getId() {
    return id;
  }

  String getPath() {
    return path;
  }

  String getName() {
    return name;
  }

  void setName(String name) {
    throw new IllegalStateException("Node name are read only");
  }

  Node getNode() {
    return node;
  }

  DomainSession getSession() {
    return session;
  }

  Status getStatus() {
    return Status.PERSISTENT;
  }

  Object getPropertyValue(String propertyName, SimpleValueInfo type) {
    try {
      Object value = null;

      //
      if (propertyCache != null) {
        value = propertyCache.get(propertyName);
      }

      //
      if (value == null) {
        Value jcrValue;
        Property property = session.getSessionWrapper().getProperty(node, propertyName);
        if (property != null) {
          PropertyDefinition def = property.getDefinition();
          if (def.isMultiple()) {
            Value[] values = property.getValues();
            if (values.length == 0) {
              jcrValue = null;
            } else {
              jcrValue = values[0];
            }
          } else {
            jcrValue = property.getValue();
          }
        } else {
          jcrValue = null;
        }

        //
        if (jcrValue != null) {
          SimpleType st = type != null ? type.getSimpleType() : null;
          value = ValueMapper.instance.get(jcrValue, st);

          //
          if (propertyCache != null) {
            if (value instanceof InputStream) {
              try {
                value = new CloneableInputStream((InputStream)value);
              }
              catch (IOException e) {
                throw new AssertionError(e);
              }
            }
          }
        }
      }

      //
      if (value == null) {
        if (type != null && type.isPrimitive()) {
          throw new IllegalStateException("Cannot convert null to primitive type " + type.getSimpleType());
        }
      } else {
        if (propertyCache != null) {
          if (value instanceof InputStream) {
            value = ((CloneableInputStream)value).clone();
          } else if (value instanceof Date) {
            value = ((Date)value).clone();
          }
        }
      }

      //
      return value;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <T> T getPropertyValues(String propertyName, SimpleValueInfo simpleType, ListType<T> listType) {
    try {
      Value[] values;
      Property property = session.getSessionWrapper().getProperty(node, propertyName);
      if (property != null) {
        PropertyDefinition def = property.getDefinition();
        if (def.isMultiple()) {
          values = property.getValues();
        } else {
          values = new Value[]{property.getValue()};
        }
      } else {
        values = new Value[0];
      }

      //
      if (listType == ListType.LIST) {
        List<Object> list = new ArrayList<Object>(values.length);
        for (Value value : values) {
          Object o = ValueMapper.instance.get(value, simpleType.getSimpleType());
          list.add(o);
        }
        return (T)list;
      } else {
        Object array = Array.newInstance((Class<?>)simpleType.getTypeInfo().getType(), values.length);
        for (int i = 0;i < values.length;i++) {
          Value value = values[i];
          Object o = ValueMapper.instance.get(value, simpleType.getSimpleType());
          Array.set(array, i, o);
        }
        return (T)array;
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  void setPropertyValue(String propertyName, SimpleValueInfo type, Object propertyValue) {
    try {
      if (propertyCache != null) {
        if (propertyValue instanceof InputStream) {
          propertyValue = new CopyingInputStream((InputStream)propertyValue);
        }
      }

      //
      Value jcrValue;
      if (propertyValue != null) {
        ValueFactory valueFactory = session.getJCRSession().getValueFactory();
        SimpleType st = type != null ? type.getSimpleType() : null;
        jcrValue = ValueMapper.instance.get(valueFactory, propertyValue, st);
      } else {
        jcrValue = null;
      }

      //
      PropertyDefinitionInfo def = nodeInfo.findPropertyDefinition(propertyName);

      //
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() + "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      if (jcrValue != null) {
        int neededType = def.getType();
        if (neededType != PropertyType.UNDEFINED) {
          if (neededType != jcrValue.getType()) {
            throw new ClassCastException("Cannot cast type " + jcrValue.getType() + " to type " + neededType + " when setting property " + propertyName);
          }
        }
      }

      //
      if (def.isMultiple()) {
        if (jcrValue == null) {
          node.setProperty(propertyName, new Value[0]);
        } else {
          node.setProperty(propertyName, new Value[]{jcrValue});
        }
      } else {
        node.setProperty(propertyName, jcrValue);
      }

      //
      if (propertyCache != null) {
        if (propertyValue != null) {
          if (propertyValue instanceof InputStream) {
            byte[] bytes = ((CopyingInputStream)propertyValue).getBytes();
            propertyValue = new CloneableInputStream(bytes);
          } else if (propertyValue instanceof Date) {
            propertyValue = ((Date)propertyValue).clone();
          }
          propertyCache.put(propertyName, propertyValue);
        } else {
          propertyCache.remove(propertyName);
        }
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <T> void setPropertyValues(String propertyName, SimpleValueInfo type, ListType<T> listType, T objects) {
    if (objects == null) {
      throw new NullPointerException();
    }
    try {
      ValueFactory valueFactory = session.getJCRSession().getValueFactory();
      SimpleType st = type != null ? type.getSimpleType() : null;
      Value[] values;
      if (listType == ListType.LIST) {
        List<?> list = (List<?>)objects;
        values = new Value[list.size()];
        int i = 0;
        for (Object object : list) {
          values[i++] = ValueMapper.instance.get(valueFactory, object, st);
        }
      } else {
        values = new Value[Array.getLength(objects)];
        for (int i = 0;i < values.length;i++) {
          Object o = Array.get(objects, i);
          values[i] = ValueMapper.instance.get(valueFactory, o, st);
        }
      }

      //
      PropertyDefinitionInfo def = nodeInfo.getPropertyDefinitionInfo(propertyName);
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

  public String toString() {
    return "ObjectStatus[id=" + id + ",status=" + Status.PERSISTENT + "]";
  }
}
