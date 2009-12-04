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
package org.chromattic.test.support;

import junit.framework.Assert;
import org.chromattic.common.Safe;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyChangedEvent extends StateChangeEvent {

  /** . */
  private final String id;

  /** . */
  private final Object object;

  /** . */
  private final String name;

  /** . */
  private final Object value;

  public PropertyChangedEvent(String id, Object object, String name, Object value) {
    this.id = id;
    this.object = object;
    this.name = name;
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public Object getObject() {
    return object;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof PropertyChangedEvent) {
      PropertyChangedEvent that = (PropertyChangedEvent)obj;
      if (value instanceof InputStream && that.value instanceof InputStream) {
        InputStream s1 = (InputStream)value;
        InputStream s2 = (InputStream)that.value;
        while (true) {
          try {
            int i1 = s1.read();
            int i2 = s2.read();
            if (i1 != i2) {
              return false;
            }
            if (i1 == -1) {
              break;
            }
          }
          catch (IOException e) {
            return false;
          }
        }
      } else {
        if (!Safe.equals(value, that.value)) {
          return false;
        }
      }
      return Safe.equals(id, that.id) && Safe.equals(object, that.object) && Safe.equals(name, that.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    // We don't use value because input stream would not provide a correct hash code
    return Safe.hashCode(id) + Safe.hashCode(object) + Safe.hashCode(name);
  }
}
