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
import org.chromattic.core.ThrowableFactory;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class AnyChildCollection<E> extends AbstractCollection<E> {

  /** . */
  private final EntityContext parentCtx;

  /** . */
  private final String prefix;

  /** . */
  private final Class<E> relatedClass;

  public AnyChildCollection(EntityContext parentCtx, String prefix, Class<E> relatedClass) {
    this.relatedClass = relatedClass;
    this.prefix = prefix;
    this.parentCtx = parentCtx;
  }

  @Override
  public boolean add(Object child) {
    if (child == null) {
      throw new NullPointerException();
    }
    if (!relatedClass.isInstance(child)) {
      throw new ClassCastException("Cannot cast object with class " + child.getClass().getName() + " as child expected class " + relatedClass.getName());
    }

    //
    EntityContext childCtx = parentCtx.getSession().unwrapEntity(child);

    //
    parentCtx.addChild(ThrowableFactory.ISE, ThrowableFactory.IAE, prefix, childCtx);

    //
    return true;
  }

  public Iterator<E> iterator() {
    return parentCtx.getChildren(relatedClass);
  }

  public int size() {
    int size = 0;
    Iterator<E> iterator = iterator();
    while (iterator.hasNext()) {
      iterator.next();
      size++;
    }
    return size;
  }

  @Override
  public boolean contains(Object child) {
    if (child == null) {
      throw new NullPointerException();
    }
    if (!relatedClass.isInstance(child)) {
      throw new ClassCastException("Cannot cast object with class " + child.getClass().getName() + " as child expected class " + relatedClass.getName());
    }

    //
    EntityContext childCtx = parentCtx.getSession().unwrapEntity(child);
    return parentCtx.hasChild(prefix, childCtx.getLocalName());
  }

  @Override
  public boolean remove(Object child) {
    if (child == null) {
      throw new NullPointerException();
    }
    if (!relatedClass.isInstance(child)) {
      throw new ClassCastException("Cannot cast object with class " + child.getClass().getName() + " as child expected class " + relatedClass.getName());
    }

    //
    EntityContext childCtx = parentCtx.getSession().unwrapEntity(child);
    childCtx.remove();
    return true;
  }

  @Override
  public boolean isEmpty() {
    return !parentCtx.hasChildren();
  }  
}
