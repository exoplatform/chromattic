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

import org.chromattic.api.ChromatticIOException;
import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.NoSuchPropertyException;
import org.chromattic.core.bean.SimpleType;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.jcr.info.PrimaryTypeInfo;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.core.vt.ValueMapper;
import org.chromattic.core.vt.ValueType;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
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
  private final DomainSession session;

  /** . */
  private final Map<String, Object> propertyCache;

  /** . */
  private final Node node;

  /** . */
  private final PrimaryTypeInfo typeInfo;

  PersistentEntityContextState(Node node, DomainSession session) throws RepositoryException {
    this.session = session;
    this.propertyCache = session.domain.stateCacheEnabled ? new HashMap<String, Object>() : null;
    this.node = node;
    this.typeInfo = session.domain.nodeInfoManager.getPrimaryTypeInfo(node.getPrimaryNodeType());
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
      return node.getName();
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

  @Override
  PrimaryTypeInfo getTypeInfo() {
    return typeInfo;
  }

  <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt) {
    try {
      //
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
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
          if (vt != null) {
            value = vt.get(jcrValue);
          } else {
            SimpleType<V> tmp = null;
            value = ValueMapper.instance.get(jcrValue, tmp);
          }

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
        if (vt != null) {
          // Let's try default value
          List<V> defaultValue = vt.getDefaultValue();

          //
          if (defaultValue != null && defaultValue.size() > 0) {
            value = defaultValue.get(0);
          }

          //
          if (value == null && vt.isPrimitive()) {
            throw new IllegalStateException("Cannot convert null to primitive type " + vt);
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

  <V> List<V> getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, ListType listType) {
    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      Value[] values;
      Property property = session.getSessionWrapper().getProperty(node, propertyName);
      if (property != null) {
        if (def.isMultiple()) {
          values = property.getValues();
        } else {
          values = new Value[]{property.getValue()};
        }
      } else {
        values = new Value[0];
      }

      //
      List<V> list = listType.create(vt, values.length);
      for (int i = 0;i < values.length;i++) {
        Value value = values[i];
        V v = vt.get(value);
        list.set(i, v);
      }
      return list;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <V> void setPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, V propertyValue) {
    try {
      //
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);

      //
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
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
        ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
        if (vt != null) {
          jcrValue = vt.get(valueFactory, propertyValue);
        } else {
          jcrValue = ValueMapper.instance.get(valueFactory, propertyValue, null);
        }
      } else {
        jcrValue = null;
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

  <V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, ListType listType, List<V> objects) {
    if (objects == null) {
      throw new NullPointerException();
    }

    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
      Value[] values;
      int size = objects.size();
      values = new Value[size];
      for (int i = 0;i < size;i++) {
        V element = objects.get(i);
        values[i] = vt.get(valueFactory, element);
      }

      //
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
