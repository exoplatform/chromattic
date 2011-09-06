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

import org.chromattic.common.collection.Collections;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Safe {

  /**
   * Returns true if one of the following conditions is satisfied:
   * <ul>
   * <li>o1 and o2 are both null</li>
   * <li>o1 and o2 are not null and the invocation of the <code>equals</code> method on o1 with o2 as argument returns true</li>
   * </ul>
   *
   * @param o1 the first object
   * @param o2 the second object
   * @return the safe equals value
   */
  public static boolean equals(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    } else if (o2 == null) {
      return false;
    } else {
      return o1.equals(o2);
    }
  }

  /**
   * Returns the object's hash code if the object is not null otherwise return zero.
   *
   * @param o the object to get the hashcode from
   * @return the safe hash code value
   */
  public static int hashCode(Object o) {
    return o == null ? 0 : o.hashCode();
  }

  /**
   * Wrap the specified map with {@link java.util.Collections#unmodifiableMap(java.util.Map)} when it is not null,
   * otherwise returns {@link java.util.Collections#emptyMap()}.
   *
   * @param map the map
   * @param <K> the key generic type
   * @param <V> the value generic type
   * @return an unmodifiable map
   */
  public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
    return map == null ? java.util.Collections.<K, V>emptyMap() : java.util.Collections.unmodifiableMap(map);
  }
}
