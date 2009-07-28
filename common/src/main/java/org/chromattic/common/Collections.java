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

import java.util.concurrent.ConcurrentMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Collections {

  public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
    V previous = map.putIfAbsent(key, value);
    if (previous == null) { return value; } else { return previous; }
  }

  public static <E> List<E> list(Iterator<E> i) {
    LinkedList<E> list = new LinkedList<E>();
    while (i.hasNext()) {
      list.add(i.next());
    }
    return list;
  }

  public static <E> HashSet<E> set(Iterator<E> i) {
    HashSet<E> set = new HashSet<E>();
    while (i.hasNext()) {
      set.add(i.next());
    }
    return set;
  }

  public static <E> HashSet<E> set(Iterable<E> i) {
    return set(i.iterator());
  }

  public static <E> HashSet<E> set(E... es) {
    HashSet<E> set = new HashSet<E>();
    for (E e : es) {
      set.add(e);
    }
    return set;
  }

}
