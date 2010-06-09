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

import org.chromattic.api.*;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.jcr.info.PrimaryTypeInfo;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.core.vt2.ValueDefinition;

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
    this.propertyCache = session.domain.propertyCacheEnabled ? new HashMap<String, Object>() : null;
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

  <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {
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

          // We use the type from the real value itself when no one was provided
          if (vt == null) {
            vt = (ValueDefinition<?, V>)ValueDefinition.get(jcrValue.getType());
          }

          //
          value = vt.get(jcrValue);

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
            throw new NullPointerException("Cannot convert null to primitive type " + vt);
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

  <V> List<V> getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ListType listType) {
    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      if (vt == null) {
        throw new UnsupportedOperationException("Not supported at the moment");
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
        values = null;
      }

      //
      List<V> list;
      if (values != null) {
        list = listType.create(vt, values.length);
        for (int i = 0;i < values.length;i++) {
          Value value = values[i];
          V v = vt.get(value);
          list.set(i, v);
        }
      } else {
        List<V> defaultValue = vt.getDefaultValue();
        if (defaultValue != null) {
          if (def.isMultiple()) {
            list = listType.create(vt, defaultValue.size());
            for (int i = 0;i < defaultValue.size();i++) {
              V v = defaultValue.get(i);
              list.set(i, v);
            }
          } else {
            if (defaultValue.size() > 0) {
              list = listType.create(vt, 1);
              list.set(0, defaultValue.get(0));
            } else {
              list = listType.create(vt, 0);
            }
          }
        } else {
          list = null;
        }
      }

      //
      return list;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <V> void setPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, V propertyValue) {
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

        //
        if (vt == null) {

          // We try first the definition type
          vt = (ValueDefinition<?, V>)ValueDefinition.get(def.getType());

          // We had a undefined type so we are going to use a type based on the provided value
          if (vt == null) {
            vt = (ValueDefinition<?, V>)ValueDefinition.get(propertyValue);
          }

          //
          if (vt == null) {
            throw new TypeConversionException("Cannot convert object " + propertyValue + " no converter found");
          }
        }

        //
        int expectedType = def.getType();

        //
        ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
        jcrValue = vt.get(valueFactory, expectedType, propertyValue);
      } else {
        jcrValue = null;
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

  <V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ListType listType, List<V> propertyValues) {
    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      if (vt == null) {
        throw new UnsupportedOperationException("Not supported at the moment");
      }

      //
      Value[] jcrValues;
      if (propertyValues != null) {
        ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
        int size = propertyValues.size();
        jcrValues = new Value[size];
        for (int i = 0;i < size;i++) {
          V element = propertyValues.get(i);
          Value jcrValue = vt.get(valueFactory, def.getType(), element);
          jcrValues[i] = jcrValue;
        }
      } else {
        jcrValues = null;
      }

      //
      if (jcrValues != null) {
        if (def.isMultiple()) {
          node.setProperty(propertyName, jcrValues);
        } else {
          if (jcrValues.length > 1) {
            throw new IllegalArgumentException("Cannot update with an array of length greater than 1");
          } else if (jcrValues.length == 1) {
            node.setProperty(propertyName, jcrValues[0]);
          } else {
            node.setProperty(propertyName, (Value)null);
          }
        }
      } else {
        if (def.isMultiple()) {
          node.setProperty(propertyName, (Value[])null);
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
