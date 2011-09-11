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
import org.chromattic.spi.type.SimpleTypeProvider;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
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
      PropertyDescriptor<?> desc = nodeTypeInfo.resolveProperty(propertyName);
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
        SimpleTypeProvider stp;
        if (vt != null) {
          stp = vt.getValueType();
        } else {
          stp = null;
        }

        //
        value = (V)session.sessionWrapper.getPropertyValue(node, desc, stp, propertyName);

        //
        if (value != null) {
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
      PropertyDescriptor<?> desc = nodeTypeInfo.resolveProperty(propertyName);
      if (desc == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be from from node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      SimpleTypeProvider stp;
      if (vt != null) {
        stp = vt.getValueType();
      } else {
        stp = null;
      }

      //
      L value = (L)session.sessionWrapper.getPropertyValues(node, arrayType, desc, stp, propertyName);

      //
      if (value == null) {
        if (vt != null) {
          List<V> defaultValue = vt.getDefaultValue();
          if (defaultValue != null) {
            if (desc.isMultiValued()) {
              value = arrayType.create(defaultValue.size());
              for (int i = 0;i < defaultValue.size();i++) {
                V v = defaultValue.get(i);
                arrayType.set(value, i, v);
              }
            } else {
              if (defaultValue.size() > 0) {
                value = arrayType.create(1);
                arrayType.set(value, 0, defaultValue.get(0));
              } else {
                value = arrayType.create(0);
              }
            }
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

  <V> void setPropertyValue(ObjectType nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, V propertyValue) {
    try {
      //
      PropertyDescriptor<?> desc = nodeTypeInfo.resolveProperty(propertyName);

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
        session.sessionWrapper.setPropertyValue(node, (PropertyDescriptor)desc, vt.getValueType(), propertyName, propertyValue);
      } else {
        session.sessionWrapper.setPropertyValue(node, desc, null, propertyName, null);
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
      PropertyDescriptor<?> desc = nodeTypeInfo.resolveProperty(propertyName);
      if (desc == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() +
          "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      SimpleTypeProvider stp;
      if (vt != null) {
        stp = vt.getValueType();
      } else {
        stp = null;
      }

      //
      session.sessionWrapper.setPropertyValues(node, arrayType, desc, stp,propertyName, propertyValues);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public String toString() {
    return "ObjectStatus[path=" + getPath() + ",status=" + Status.PERSISTENT + "]";
  }

}
