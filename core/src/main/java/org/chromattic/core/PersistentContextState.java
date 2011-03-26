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
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.common.JCR;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.Property;
import javax.jcr.ValueFactory;
import javax.jcr.PropertyType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentContextState extends ContextState {

  /** . */
  private final TypeMapper mapper;

  /** . */
  private final String externalName;

  /** . */
  private final String path;

  /** . */
  private final String id;

  /** . */
  private final Node node;

  /** . */
  private final DomainSession session;

  PersistentContextState(TypeMapper mapper, Node node, DomainSession session) throws RepositoryException {
    super(node.getPrimaryNodeType());

    //
    this.mapper = mapper;
    this.externalName = mapper.decodeName(node.getName());
    this.id = node.getUUID();
    this.path = node.getPath();
    this.node = node;
    this.session = session;
  }

  String getId() {
    return id;
  }

  String getPath() {
    return path;
  }

  String getName() {
    return externalName;
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
      Value value;
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
      if (value != null) {
        SimpleType st = type != null ? type.getSimpleType() : null;
        return ValueMapper.instance.get(value, st);
      } else {
        if (type != null && type.isPrimitive()) {
          throw new IllegalStateException("Cannot convert null to primitive type " + type.getSimpleType());
        }
        return null;
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  <T> T getPropertyValues(String propertyName, SimpleValueInfo simpleType, ListType<T> listType) {
    try {
      Value[] values;
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

  void setPropertyValue(String propertyName, SimpleValueInfo type, Object o) {
    try {
      Value value;
      if (o != null) {
        ValueFactory valueFactory = session.getJCRSession().getValueFactory();
        SimpleType st = type != null ? type.getSimpleType() : null;
        value = ValueMapper.instance.get(valueFactory, o, st);
      } else {
        value = null;
      }

      //
      PropertyDefinition def = JCR.findPropertyDefinition(node, propertyName);

      //
      if (def == null) {
        throw new NoSuchPropertyException("Property " + propertyName + " cannot be set on node " + node.getPath() + "  with type " + node.getPrimaryNodeType().getName());
      }

      //
      if (value != null) {
        int neededType = def.getRequiredType();
        if (neededType != PropertyType.UNDEFINED) {
          if (neededType != value.getType()) {
            throw new ClassCastException();
          }
        }
      }

      //
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
      PropertyDefinition def = JCR.getPropertyDefinition(node, propertyName);
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
