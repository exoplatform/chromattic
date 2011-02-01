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
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.jcr.info.PrimaryTypeInfo;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.core.vt.ValueType;

import javax.jcr.InvalidItemStateException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentEntityContextState extends EntityContextState {

  /** . */
  private final DomainSession session;

  /** . */
  private final Node node;

  /** . */
  private final PrimaryTypeInfo typeInfo;

  /** . */
  private final boolean readAhead;

  /** . */
  private final boolean cache;

  /** . */
  private Map<String, Property> propertyCache;

  PersistentEntityContextState(Node node, DomainSession session) throws RepositoryException {

    PrimaryTypeInfo primaryTypeInfo = session.domain.nodeInfoManager.getPrimaryTypeInfo(node.getPrimaryNodeType());


    //
    this.session = session;
    this.propertyCache = null;
    this.node = node;
    this.typeInfo = primaryTypeInfo;
    this.readAhead = primaryTypeInfo.isReadAhead();
    this.cache = session.domain.propertyCacheEnabled;
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

  <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt) {
    try {
      //
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      Property property = null;
      if (cache) {
        if (readAhead) {
          if (propertyCache == null) {
            Map<String, Property> propertyCache = new HashMap<String, Property>();
            Iterator<Property> i = session.getSessionWrapper().getProperties(node);
            while (i.hasNext()) {
              Property p = i.next();
              String name = p.getName();
              propertyCache.put(name, p);
              if (name.equals(propertyName)) {
                property = p;
              }
            }
            this.propertyCache = propertyCache;
          } else {
            property = propertyCache.get(propertyName);
          }
        } else {
          if (propertyCache != null) {
            property = propertyCache.get(propertyName);
          }
          if (property == null) {
            property = session.getSessionWrapper().getProperty(node, propertyName);
            if (property != null) {
              if (propertyCache == null) {
                propertyCache = new HashMap<String, Property>();
                propertyCache.put(property.getName(), property);
              }
            }
          }
        }
      } else {
        property = session.getSessionWrapper().getProperty(node, propertyName);
      }

      //
      Value jcrValue;
      if (property != null) {
        if (def.isMultiple()) {
          Value[] values = new Value[0];
          try {
            values = property.getValues();
            if (values.length == 0) {
              jcrValue = null;
            } else {
              jcrValue = values[0];
            }
          }
          catch (InvalidItemStateException e) {
            // The property was deleted
            jcrValue = null;
          }
        } else {
          try {
            jcrValue = property.getValue();
          }
          catch (InvalidItemStateException e) {
            // The property was deleted
            jcrValue = null;
          }
        }
      } else {
        jcrValue = null;
      }

      //
      V value = null;
      if (jcrValue != null) {
        value = vt.get(jcrValue);
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
      Value jcrValue;
      if (propertyValue != null) {
        ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
        jcrValue = vt.get(valueFactory, propertyValue);
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
      Property property;
      if (def.isMultiple()) {
        if (jcrValue == null) {
          property = node.setProperty(propertyName, new Value[0]);
        } else {
          property = node.setProperty(propertyName, new Value[]{jcrValue});
        }
      } else {
        property = node.setProperty(propertyName, jcrValue);
      }

      //
      if (propertyCache != null) {
        propertyCache.put(propertyName, property);
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
