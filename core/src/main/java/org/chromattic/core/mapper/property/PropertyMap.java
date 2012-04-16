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

package org.chromattic.core.mapper.property;

import org.chromattic.common.collection.AbstractFilterIterator;
import org.chromattic.common.JCR;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.ArrayType;
import org.chromattic.core.EntityContext;
import org.chromattic.metamodel.bean.ValueKind;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PropertyMap extends AbstractMap<String, Object> {

  /** . */
  private static final ArrayType<List<Object>, Object> LIST_TYPE = ArrayType.list(Object.class);

  /** . */
  private final JCRPropertyDetypedPropertyMapper mapper;

  /** . */
  private final EntityContext ctx;

  /** . */
  private SetImpl set;

  PropertyMap(JCRPropertyDetypedPropertyMapper mapper, EntityContext ctx) {
    this.ctx = ctx;
    this.mapper = mapper;
    this.set = null;
  }

  public Set<Entry<String, Object>> entrySet() {
    if (set == null) {
      set = new SetImpl();
    }
    return set;
  }

  @Override
  public Object get(Object key) {
    String s = validateKey(key);
    if (s != null) {
      try {
        if (mapper.valueKind == ValueKind.SINGLE) {
          return ctx.getPropertyValue(s, null);
        } else {
          return ctx.getPropertyValues(s, null, LIST_TYPE);
        }
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    } else {
      return null;
    }
  }

  @Override
  public boolean containsKey(Object key) {
    String s = validateKey(key);
    if (s != null) {
      try {
        return ctx.hasProperty(s, null);
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    } else {
      return false;
    }
  }

  @Override
  public Object remove(Object key) {
    String s = validateKey(key);
    if (s != null) {
      return put(s, null);
    } else {
      return null;
    }
  }

  @Override
  public Object put(String key, Object value) {
    String s = validateKey(key);
    if (s != null) {
      return update(key, value);
    } else {
      throw new IllegalArgumentException("Invalid key " + key + " should being with the prefix " + mapper.namePrefix);
    }
  }

  private String validateKey(Object key) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
    if (key instanceof String) {
      String s = (String)key;
      if (mapper.namePrefix != null) {
        return mapper.namePrefix + s;
      } else {
        return s;
      }
    } else {
      throw new ClassCastException("Key must be instance of String instead of " + key.getClass().getName());
    }
  }

  private Object update(String key, Object value) {
    try {
      Object previous;
      if (mapper.valueKind == ValueKind.SINGLE) {
        previous = ctx.getPropertyValue(key, null);
        ctx.setPropertyValue(key, null, value);
      } else {
        List<Object> list = (List<Object>)value;
        previous = ctx.getPropertyValues(key, null, LIST_TYPE);
        ctx.setPropertyValues(key, null, LIST_TYPE, list);
      }
      return previous;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  private class SetImpl extends AbstractSet<Map.Entry<String, Object>> {

    public Iterator<Map.Entry<String, Object>> iterator() {

      try {
        Iterator<Property> i;
        if (mapper.namePattern == null) {
          i = JCR.adapt(ctx.getNode().getProperties());
        } else {
          i = JCR.adapt(ctx.getNode().getProperties(mapper.namePattern));
        }

        //
        return new AbstractFilterIterator<Entry<String, Object>, Property>(i) {

          @Override
          protected Entry<String, Object> adapt(Property internal) {

            try {

              // todo : that does not respect the encoding of property names but
              // that is not much important for now as it is an unsupported feature

              // todo : support for default values is not done because
              // we pass null as the SimpleValueInfo type parameter

              //
              final String key = internal.getName();

              //
              final String name = mapper.namePrefix != null ? key.substring(mapper.namePrefix.length()) : key;

              //
              if ("*".equals(internal.getDefinition().getName())) {
                switch (internal.getType()) {
                  case PropertyType.STRING:
                  case PropertyType.NAME:
                  case PropertyType.LONG:
                  case PropertyType.BOOLEAN:
                  {
                    return new Entry<String, Object>() {
                      public String getKey() {
                        return name;
                      }
                      public Object getValue() {
                        return get(key);
                      }
                      public Object setValue(Object value) {
                        throw new UnsupportedOperationException();
                      }
                    };
                  }
                }
              }

              //
              return null;
            }
            catch (RepositoryException e) {
              throw new UndeclaredRepositoryException(e);
            }
          }
        };
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    }

    public int size() {
      int count = 0;
      Iterator<Map.Entry<String, Object>> iterator = iterator();
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }
  }
}
