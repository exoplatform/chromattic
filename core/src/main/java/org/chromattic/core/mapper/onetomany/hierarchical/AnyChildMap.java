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

package org.chromattic.core.mapper.onetomany.hierarchical;

import org.chromattic.core.EntityContext;

import java.util.AbstractMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnyChildMap<E> extends AbstractMap<String, E> {

  /** . */
  final EntityContext parentCtx;

  /** . */
  final String prefix;

  /** . */
  final Class<E> relatedClass;

  /** . */
  private final AnyChildEntrySet<E> entries;

  public AnyChildMap(
    EntityContext parentCtx,
    String prefix,
    Class<E> relatedClass) {
    this.relatedClass = relatedClass;
    this.prefix = prefix;
    this.entries = new AnyChildEntrySet<E>(this);
    this.parentCtx = parentCtx;
  }

  @Override
  public E get(Object key) {
    if (key instanceof String) {
      String name = (String)key;
      EntityContext childCtx = parentCtx.getChild(prefix, name);
      if (childCtx != null) {
        Object child = childCtx.getObject();
        if (relatedClass.isInstance(child)) {
          return relatedClass.cast(child);
        }
      }
    }
    return null;
  }

  @Override
  public E remove(Object key) {
    if (key instanceof String) {
      return put((String)key, null);
    } else {
      return null;
    }
  }

  @Override
  public E put(String key, E value) {
    EntityContext childCtx = parentCtx.getChild(prefix, key);

    //
    if (value == null) {
      if (childCtx != null) {
        parentCtx.getSession().remove(childCtx);
      }
    } else if (relatedClass.isInstance(value)) {
      EntityContext valueCtx = parentCtx.getSession().unwrapEntity(value);
      parentCtx.addChild(prefix, key, valueCtx);
    } else {
      throw new ClassCastException("Cannot put " + value + " with in map containing values of type " + relatedClass);
    }

    //
    if (childCtx != null) {
      Object child = childCtx.getObject();
      if (relatedClass.isInstance(child)) {
        return relatedClass.cast(child);
      }
    }

    // julien todo : unit test that
    return null;
  }

  public Set<Entry<String, E>> entrySet() {
    return entries;
  }
}
