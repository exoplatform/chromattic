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

import org.chromattic.common.JCR;
import org.chromattic.common.TypeAdapter;
import org.chromattic.common.AdaptingIterator;
import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.AbstractMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PropertyMap extends AbstractMap<String, Object> {

  /** . */
  private final ObjectContext ctx;

  /** . */
  private final SetImpl set = new SetImpl();

  public PropertyMap(ObjectContext ctx) {
    this.ctx = ctx;
  }

  public Set<Entry<String, Object>> entrySet() {
    return set;
  }

  @Override
  public Object remove(Object key) {
    if (key instanceof String) {
      return put((String)key, null);
    } else {
      return null;
    }
  }

  @Override
  public Object put(String key, Object value) {
    Object previous = ctx.getPropertyValue(key, null);
    ctx.setPropertyValue(key, null, value);
    return previous;
  }

  private class SetImpl extends AbstractSet<Map.Entry<String, Object>> {

    public Iterator<Map.Entry<String, Object>> iterator() {

      try {
        Iterator<Property> i = JCR.adapt(ctx.state.getNode().getProperties());

        TypeAdapter<Entry<String, Object>, Property> adapter = new TypeAdapter<Map.Entry<String, Object>, Property>() {
          public Map.Entry<String, Object> adapt(Property property) {
            try {
              Object o;
              switch (property.getType()) {
                case PropertyType.STRING:
                case PropertyType.NAME:
                  o = property.getString();
                  break;
                case PropertyType.LONG:
                  o = (int)property.getLong();
                  break;
                case PropertyType.BOOLEAN:
                  o = property.getBoolean();
                  break;
                default:
                  throw new UnsupportedOperationException("Should implement handling of property type " + property.getType());
              }
              final String key = property.getName();
              final Object value = o;
              return new Entry<String, Object>() {
                public String getKey() {
                  return key;
                }
                public Object getValue() {
                  return value;
                }
                public Object setValue(Object value) {
                  return null;
                }
              };
            }
            catch (RepositoryException e) {
              throw new UndeclaredRepositoryException(e);
            }
          }
        };

        //
        return new AdaptingIterator<Map.Entry<String, Object>, Property>(i, adapter);
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    }

    public int size() {
      try {
        return (int)ctx.state.getNode().getProperties().getSize();
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    }
  }

}
