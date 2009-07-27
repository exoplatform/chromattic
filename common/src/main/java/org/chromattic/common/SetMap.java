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
package org.chromattic.common;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SetMap<K, V> {

  /** . */
  private final Map<K, SetImpl> map = new HashMap<K, SetImpl>();

  public Set<V> peek(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    return map.get(key);
  }
  
  public Set<V> get(K key) {
    if (key == null) {
      throw new NullPointerException();
    }
    Set<V> set = map.get(key);
    if (set == null) {
      set = new SetImpl(key);
    }
    return set;
  }

  public Set<K> keySet() {
    return map.keySet();
  }
  
  private class SetImpl extends AbstractSet<V> {

    /** . */
    private final K key;
    
    /** . */
    private HashSet<V> set;

    private SetImpl(K key) {
      this.key = key;
    }

    @Override
    public boolean add(V e) {
      if (set == null) {
        set = new HashSet<V>();
        if (map.containsKey(key)) {
          throw new IllegalStateException();
        } else {
          map.put(key, this);
        }
      }
      boolean b = set.add(e);
      return b;
    }

    public Iterator<V> iterator() {
      return new Iterator<V>() {
        final Iterator<V> iterator = set.iterator();
        public boolean hasNext() {
          return iterator.hasNext();
        }
        public V next() {
          return iterator.next();
        }
        public void remove() {
          iterator.remove();
          if (set.size() == 0) {
            if (map.containsKey(key)) {
              map.remove(key);
            } else {
              throw new IllegalStateException();
            }
          }
        }
      };
    }

    public int size() {
      return set.size();
    }
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
