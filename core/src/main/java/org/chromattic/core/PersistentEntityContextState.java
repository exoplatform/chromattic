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

import org.chromattic.api.ChromatticException;
import org.chromattic.api.ChromatticIOException;
import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.NoSuchPropertyException;
import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.bean.SimpleType;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.mapper.ValueMapper;
import org.chromattic.core.jcr.info.NodeInfo;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.common.CopyingInputStream;

import javax.jcr.*;
import javax.jcr.nodetype.PropertyDefinition;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentEntityContextState extends EntityContextState {

  /** . */
  private final Node node;

  /** . */
  private final DomainSession session;

  /** . */
  private final Map<String, Object> propertyCache;

  /** . */
  private final NodeInfo nodeInfo;

  PersistentEntityContextState(Node node, DomainSession session) throws RepositoryException {
    this.node = node;
    this.session = session;
    this.propertyCache = session.domain.stateCacheEnabled ? new HashMap<String, Object>() : null;
    this.nodeInfo = session.domain.nodeInfoManager.getNodeInfo(node);
  }

  String getId() {
    try {
      return node.getUUID();
    }
    catch (UnsupportedRepositoryOperationException e) {
      return null;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  String getPath() {
    try {
      return node.getPath();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  String getName() {
    try {
      ObjectFormatter formatter = null;
      Node parentNode = node.getParent();
      String nodeTypeName = parentNode.getPrimaryNodeType().getName();
      TypeMapper parentMapper = session.domain.getTypeMapper(nodeTypeName);
      if (parentMapper != null) {
        formatter = parentMapper.getFormatter();
      }
      if (formatter == null) {
        formatter = session.domain.objectFormatter;
      }

      //
      String internalName = node.getName();

      //
      String external;
      try {
        external = formatter.decodeNodeName(null, internalName);
      }
      catch (Exception e) {
        if (e instanceof IllegalStateException) {
          throw (IllegalStateException)e;
        }
        throw new UndeclaredThrowableException(e);
      }
      if (external == null) {
        throw new IllegalStateException("Null name returned by decoder");
      }
      return external;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
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

  <V> V getPropertyValue(String propertyName, SimpleValueInfo<V> svi) {
    try {
      V value = null;

      //
      if (propertyCache != null) {
        // That must be ok
        value = (V)propertyCache.get(propertyName);
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
          SimpleType<V> st = svi != null ? svi.getSimpleType() : null;
          value = ValueMapper.instance.get(jcrValue, st);

          //
          if (propertyCache != null) {
            if (value instanceof InputStream) {
              try {
                value = (V)new CloneableInputStream((InputStream)value);
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
        if (svi != null) {
          // Let's try default value
          List<V> defaultValue = svi.getDefaultValue();

          //
          if (defaultValue != null && defaultValue.size() > 0) {
            value = defaultValue.get(0);
          }

          //
          if (value == null && svi.getSimpleType().isPrimitive()) {
            throw new IllegalStateException("Cannot convert null to primitive type " + svi.getSimpleType());
          }
        }
      } else {
        if (propertyCache != null) {
          if (value instanceof InputStream) {
            value = (V)((CloneableInputStream)value).clone();
          } else if (value instanceof Date) {
            value = (V)((Date)value).clone();
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

  <V> List<V> getPropertyValues(String propertyName, SimpleValueInfo<V> svi, ListType listType) {
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
      List<V> list = listType.create(svi.getSimpleType(), values.length);
      for (int i = 0;i < values.length;i++) {
        Value value = values[i];
        V v = ValueMapper.instance.get(value, svi.getSimpleType());
        list.set(i, v);
      }
      return list;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <V> void setPropertyValue(String propertyName, SimpleValueInfo<V> svi, V propertyValue) {
    try {
      if (propertyCache != null) {
        if (propertyValue instanceof InputStream && (propertyValue instanceof CloneableInputStream)) {
          try {
            propertyValue = (V)new CloneableInputStream((InputStream)propertyValue);
          }
          catch (IOException e) {
            throw new ChromatticIOException("Could not read stream", e);
          }
        }
      }

      //
      Value jcrValue;
      if (propertyValue != null) {
        ValueFactory valueFactory = session.getJCRSession().getValueFactory();
        SimpleType<V> st = svi != null ? svi.getSimpleType() : null;
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
            CloneableInputStream stream = ((CloneableInputStream)propertyValue);
            propertyValue = (V)stream.clone();
          } else if (propertyValue instanceof Date) {
            propertyValue = (V)((Date)propertyValue).clone();
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

  <V> void setPropertyValues(String propertyName, SimpleValueInfo<V> svi, ListType listType, List<V> objects) {
    if (objects == null) {
      throw new NullPointerException();
    }
    try {
      ValueFactory valueFactory = session.getJCRSession().getValueFactory();
      SimpleType<V> st = svi != null ? svi.getSimpleType() : null;
      Value[] values;
      int size = objects.size();
      values = new Value[size];
      for (int i = 0;i < size;i++) {
        V element = objects.get(i);
        values[i] = ValueMapper.instance.get(valueFactory, element, st);
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
    return "ObjectStatus[path=" + getPath() + ",status=" + Status.PERSISTENT + "]";
  }
}
