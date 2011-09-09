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
import org.chromattic.common.CloneableInputStream;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.metatype.EntityType;
import org.chromattic.metatype.ObjectType;
import org.chromattic.metatype.PropertyDescriptor;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.util.*;
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
  private final EntityType typeInfo;

  PersistentEntityContextState(Node node, DomainSession session) throws RepositoryException {
    this.session = session;
    this.propertyCache = session.domain.propertyCacheEnabled ? new HashMap<String, Object>() : null;
    this.node = node;
    this.typeInfo = (EntityType)session.getSessionWrapper().getSchema().getType(node.getPrimaryNodeType().getName());
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

  String getLocalName() {
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
  EntityType getTypeInfo() {
    return typeInfo;
  }

  <V> V getPropertyValue(ObjectType nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {
    try {
      //
      PropertyDescriptor desc = nodeTypeInfo.resolveProperty(propertyName);
      if (desc == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be loaded from node " + node.getPath() +
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
          if (desc.isMultiValued()) {
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

  @Override
  <L, V> L getPropertyValues(ObjectType nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType) {
    try {
      PropertyDescriptor desc = nodeTypeInfo.resolveProperty(propertyName);
      if (desc == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be from from node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      Value[] values;
      Property property = session.getSessionWrapper().getProperty(node, propertyName);
      if (property != null) {
        if (desc.isMultiValued()) {
          values = property.getValues();
        } else {
          values = new Value[]{property.getValue()};
        }
      } else {
        values = null;
      }

      // Try to determine a vt from the real value
      if (vt == null) {
        vt = (ValueDefinition<?, V>)ValueDefinition.get(desc.getValueType().getCode());
        if (vt == null) {
          if (values != null && values.length > 0) {
            vt = (ValueDefinition<?, V>)ValueDefinition.get(values[0].getType());
          }
        }
      }

      //
      L list;
      if (vt != null) {
        if (values != null) {
          list = arrayType.create(values.length);
          for (int i = 0;i < values.length;i++) {
            Value value = values[i];
            V v = vt.get(value);
            arrayType.set(list, i, v);
          }
        } else {
          List<V> defaultValue = vt.getDefaultValue();
          if (defaultValue != null) {
            if (desc.isMultiValued()) {
              list = arrayType.create(defaultValue.size());
              for (int i = 0;i < defaultValue.size();i++) {
                V v = defaultValue.get(i);
                arrayType.set(list, i, v);
              }
            } else {
              if (defaultValue.size() > 0) {
                list = arrayType.create(1);
                arrayType.set(list, 0, defaultValue.get(0));
              } else {
                list = arrayType.create(0);
              }
            }
          } else {
            list = null;
          }
        }
      } else {
        list = arrayType.create(0);
      }

      //
      return list;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <V> void setPropertyValue(ObjectType nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, V propertyValue) {
    try {
      //
      PropertyDescriptor desc = nodeTypeInfo.resolveProperty(propertyName);

      //
      if (desc == null) {
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
          vt = (ValueDefinition<?, V>)ValueDefinition.get(desc.getValueType().getCode());

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
        int expectedType = desc.getValueType().getCode();

        //
        ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
        jcrValue = vt.get(valueFactory, expectedType, propertyValue);
      } else {
        jcrValue = null;
      }

      //
      if (desc.isMultiValued()) {
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

  @Override
  <L, V> void setPropertyValues(ObjectType nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType, L propertyValues) {
    try {
      PropertyDescriptor desc = nodeTypeInfo.resolveProperty(propertyName);
      if (desc == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      Value[] jcrValues;
      if (propertyValues != null) {
        if (arrayType.size(propertyValues) == 0) {
          jcrValues = new Value[0];
        } else {

          // Determine vt if null
          if (vt == null) {

            // We try first the definition type
            vt = (ValueDefinition<?, V>)ValueDefinition.get(desc.getValueType().getCode());

            //
            if (vt == null) {
              Object propertyValue = arrayType.get(propertyValues, 0);
              vt = (ValueDefinition<?, V>)ValueDefinition.get(propertyValue);
              if (vt == null) {
                throw new TypeConversionException("Cannot convert object " + propertyValue + " no converter found");
              }
            }
          }

          //
          ValueFactory valueFactory = session.sessionWrapper.getSession().getValueFactory();
          int size = arrayType.size(propertyValues);
          jcrValues = new Value[size];
          for (int i = 0;i < size;i++) {
            V element = arrayType.get(propertyValues, i);
            Value jcrValue = vt.get(valueFactory, desc.getValueType().getCode(), element);
            jcrValues[i] = jcrValue;
          }
        }
      } else {
        jcrValues = null;
      }

      //
      if (jcrValues != null) {
        if (desc.isMultiValued()) {
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
        if (desc.isMultiValued()) {
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
