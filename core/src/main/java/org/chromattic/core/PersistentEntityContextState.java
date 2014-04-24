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
import org.chromattic.api.NoSuchPropertyException;
import org.chromattic.api.Status;
import org.chromattic.api.TypeConversionException;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.core.jcr.type.NodeTypeInfo;
import org.chromattic.core.jcr.type.PrimaryTypeInfo;
import org.chromattic.core.jcr.type.PropertyDefinitionInfo;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.core.vt2.ValueDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentEntityContextState extends EntityContextState {

  private static final List<Object> NULL_VALUE = new ArrayList<Object>(1);
  
  /** . */
  private final DomainSession session;

  /** . */
  private final Map<String, List<Object>> propertyCache;

  /** . */
  private final Node node;

  /** . */
  private final PrimaryTypeInfo typeInfo;

  /** . */
  private final ObjectMapper<?> mapper;

  /** . */
  private boolean initialized;

  PersistentEntityContextState(ObjectMapper<?> mapper, Node node, DomainSession session) throws RepositoryException {
    this.session = session;
    this.propertyCache = session.domain.propertyCacheEnabled ? new HashMap<String, List<Object>>() : null;
    this.node = node;
    this.typeInfo = session.domain.nodeInfoManager.getPrimaryTypeInfo(node.getPrimaryNodeType());
    this.mapper = mapper;
    this.initialized = false;
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
  PrimaryTypeInfo getTypeInfo() {
    return typeInfo;
  }

  <V> boolean hasProperty(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {

    //
    checkInitialized();

    //
    Object value = null;
    if (propertyCache != null) {
      value = propertyCache.get(propertyName);
    }

    //
    if (value != null) {
      return value != NULL_VALUE;
    } else {
      try {
        boolean result = session.getSessionWrapper().hasProperty(node, propertyName);
        if (propertyCache != null && !result) {
           propertyCache.put(propertyName, NULL_VALUE);
        }
        return result;
      } catch (RepositoryException e) {
        return false;
      }
    }
  }

  <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {

    //
    checkInitialized();

    //
    try {
      //
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be loaded from node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      V value = null;

      //
      if (propertyCache != null) {
        // That must be ok
        List<Object> l = propertyCache.get(propertyName);
        if (l == NULL_VALUE) {
          if (vt != null) {
            value = vt.getDefaultValue();
            if (value == null && vt.isPrimitive()) {
              throw new NullPointerException("Cannot convert null to primitive type " + vt);
            }
          } else {
            value = null;
          }
        } else if (l != null) {
          value = l.isEmpty() ? null : (V)l.get(0);
        }
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
          value = vt.getDefaultValue();
          if (value == null && vt.isPrimitive()) {
            throw new NullPointerException("Cannot convert null to primitive type " + vt);
          }
        }
        if (propertyCache != null) {
          propertyCache.put(propertyName, NULL_VALUE);
        }
      } else {
        if (propertyCache != null) {
          if (value instanceof InputStream) {
            value = (V)((CloneableInputStream)value).clone();
          } else if (value instanceof Date) {
            value = (V)((Date)value).clone();
          }
          propertyCache.put(propertyName, Collections.<Object>singletonList(value));
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
  <L, V> L getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType) {

    //
    checkInitialized();

    //
    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be from from node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      L list = null;
      if (propertyCache != null) {
        // That must be ok
        List<Object> l = propertyCache.get(propertyName);
        if (l == NULL_VALUE) {
          if (vt != null) {
            list = getDefaultValues(def, vt, arrayType);
          } else {
            list = arrayType.create(0);
          }
        } else if (l != null) {
          int size = l.size();
          list = arrayType.create(size);
          for (int i = 0 ;i < size;i++) {
            V v = (V)l.get(i);
            arrayType.set(list, i, v);
          }
        }
      }

      //
      if (list == null) {

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

        // Try to determine a vt from the real value
        if (vt == null) {
          vt = (ValueDefinition<?, V>)ValueDefinition.get(def.getType());
          if (vt == null) {
            if (values != null && values.length > 0) {
              vt = (ValueDefinition<?, V>)ValueDefinition.get(values[0].getType());
            }
          }
        }

        //
        if (vt != null) {
          if (values != null) {
            list = arrayType.create(values.length);
            for (int i = 0;i < values.length;i++) {
              Value value = values[i];
              V v = vt.get(value);
              arrayType.set(list, i, v);
            }
            if (propertyCache != null) {
              propertyCache.put(propertyName, toList(arrayType, list));
            }
          } else {
            list = getDefaultValues(def, vt, arrayType);
            if (propertyCache != null) {
              propertyCache.put(propertyName, NULL_VALUE);
            }
          }
        } else {
          list = arrayType.create(0);
          if (propertyCache != null) {
            propertyCache.put(propertyName, NULL_VALUE);
          }
        }
      }

      //
      return list;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  private static <L, V> L getDefaultValues(PropertyDefinitionInfo def, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType) {
    L list;
    List<V> defaultValue = vt.getDefaultValueList();
    if (defaultValue != null) {
      if (def.isMultiple()) {
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
    return list;
  }
  
  private static <L> List<Object> toList(ArrayType<L, ?> arrayType, L list) {
    int size = arrayType.size(list);
    List<Object> result = new ArrayList<Object>(size);
    for (int i = 0; i < size; i++) {
      result.add(arrayType.get(list, i));
    }
    return result;
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
          propertyCache.put(propertyName, Collections.<Object>singletonList(propertyValue));
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
  <L, V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType, L propertyValues) {
    try {
      PropertyDefinitionInfo def = nodeTypeInfo.findPropertyDefinition(propertyName);
      if (def == null) {
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
            vt = (ValueDefinition<?, V>)ValueDefinition.get(def.getType());

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
            Value jcrValue = vt.get(valueFactory, def.getType(), element);
            jcrValues[i] = jcrValue;
          }
        }
      } else {
        jcrValues = null;
      }

      //
      if (jcrValues != null) {
        if (def.isMultiple()) {
          node.setProperty(propertyName, jcrValues);
          if (propertyCache != null) {
             propertyCache.put(propertyName, toList(arrayType, propertyValues));
          }
        } else {
          if (jcrValues.length > 1) {
            throw new IllegalArgumentException("Cannot update with an array of length greater than 1");
          } else if (jcrValues.length == 1) {
            node.setProperty(propertyName, jcrValues[0]);
            if (propertyCache != null) {
              propertyCache.put(propertyName, Collections.<Object>singletonList(arrayType.get(propertyValues, 0)));
            }
          } else {
            node.setProperty(propertyName, (Value)null);
            if (propertyCache != null) {
              propertyCache.remove(propertyName);
            }
          }
        }
      } else {
        if (def.isMultiple()) {
          node.setProperty(propertyName, (Value[])null);
        } else {
          node.setProperty(propertyName, (Value)null);
        }
        if (propertyCache != null) {
           propertyCache.remove(propertyName);
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

  /**
   * Checks if the entity has been initialized if not it will load all the properties at the same time
   */
  @SuppressWarnings("rawtypes")
  private void checkInitialized() {
    if (!initialized) {
      initialized = true;
      if (getStatus() == Status.PERSISTENT) {
        Domain domain = session.domain;
        if (domain.propertyLoadGroupEnabled) {
          Map<String, ValueDefinition<?, ?>> properties = mapper.getProperties();
          if (properties.size() > 1) {
            // We load the properties if and only if we have at least 2 properties to load
            loadProperties();
          }
        }
      }
    }
  }

  private void loadProperties() {
     try {

       // Pre-load the second level cache anyway
       Iterator<Property> properties = session.getSessionWrapper().getProperties(node, mapper.getPropertiesPattern());

         // We store the result in the first level cache if property caching is enabled
       if (propertyCache != null) {
         int foundCount = 0;
         while (properties.hasNext()) {
           Property property = properties.next();
           String propertyName = property.getName();
           PropertyDefinitionInfo def = typeInfo.findPropertyDefinition(propertyName);
           if (def == null) {
             throw new NoSuchPropertyException("Property " + propertyName + " cannot be loaded from node " + node.getPath() +
                 "  with type " + node.getPrimaryNodeType().getName());
           }

           //
           Value[] values;
           if (def.isMultiple()) {
             values = property.getValues();
           } else {
             values = new Value[]{property.getValue()};
           }

           //
           ValueDefinition<?, ?> vt = mapper.getProperties().get(propertyName);
           if (vt == null) {
             // Try to determine a vt from the real value
             vt = ValueDefinition.get(def.getType());
             if (vt == null) {
               if (values != null && values.length > 0) {
                 vt = ValueDefinition.get(values[0].getType());
               }
             }
           }

           //
           List<Object> l = new ArrayList<Object>(values.length);
           for (Value value : values) {
             l.add(vt.get(value));
           }

           //
           propertyCache.put(propertyName, l);
           foundCount++;
         }

         // Black list not found properties
         if (foundCount < mapper.getProperties().size()) {
           for (String name : mapper.getProperties().keySet()) {
             if (propertyCache.get(name) == null) {
               propertyCache.put(name, NULL_VALUE);
             }
           }
         }
       }
     }
     catch (RepositoryException e) {
       throw new UndeclaredRepositoryException(e);
     }
   }
 }
