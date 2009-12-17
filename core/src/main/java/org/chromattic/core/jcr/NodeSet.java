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
package org.chromattic.core.jcr;

import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeSet extends AbstractSet<Node> {

  /** . */
  private HashMap<String, Node> map = null;

  public boolean contains(Object o) {
    if (map != null && o instanceof Node) {
      try {
        return map.containsKey(((Node)o).getUUID());
      }
      catch (RepositoryException e) {
        throw new UndeclaredRepositoryException(e);
      }
    }
    return false;
  }

  public Iterator<Node> iterator() {
    return map != null ? map.values().iterator() : Collections.<Node>emptyList().iterator();
  }

  public Object[] toArray() {
    return map.values().toArray();
  }

  public <T> T[] toArray(T[] a) {
    return map.values().toArray(a);
  }

  public boolean add(Node node) {
    if (map == null) {
      map = new HashMap<String, Node>();
    }
    try {
      return map.put(node.getUUID(), node) != null;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  @Override
  public int size() {
    return map.size();
  }

  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  public boolean addAll(Collection<? extends Node> c) {
    boolean changed = false;
    for (Object o : c) {
      if (o instanceof Node) {
        changed |= add((Node)o);
      }
    }
    return changed;
  }

  public boolean retainAll(Collection<?> c) {
    if (map == null) {
      return false;
    }
    Set<String> keys = keys(c);
    return map.keySet().retainAll(keys);
  }

  public boolean removeAll(Collection<?> c) {
    if (map == null) {
      return false;
    }
    Set<String> keys = keys(c);
    return map.keySet().removeAll(keys);
  }

  private Set<String> keys(Collection<?> c) {
    Set<String> keys = new HashSet<String>();
    for (Object o : c) {
      if (o instanceof Node) {
        try {
          keys.add(((Node)o).getUUID());
        }
        catch (RepositoryException e) {
          throw new UndeclaredRepositoryException(e);
        }
      }
    }
    return keys;
  }
}
