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

import org.chromattic.core.ChromatticSessionImpl;
import org.chromattic.core.EntityContext;

import java.util.Iterator;
import java.util.AbstractList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class AnyChildList<E> extends AbstractList<E> {

  /** . */
  private final EntityContext parentCtx;

  /** . */
  private final Class<E> relatedClass;

  public AnyChildList(EntityContext parentCtx, Class<E> relatedClass) {
    this.relatedClass = relatedClass;
    this.parentCtx = parentCtx;
  }

  @Override
  public void add(int index, E addedElement) {
    if (index < 0) {
      throw new IndexOutOfBoundsException();
    }
    if (addedElement == null) {
      throw new NullPointerException("No null element can be inserted");
    }
    if (!relatedClass.isInstance(addedElement)) {
      throw new ClassCastException("Cannot cast object with class " + addedElement.getClass().getName() + " as child expected class " + relatedClass.getName());
    }

    // Get the element that will be the next element of the inserted element
    E nextElement;
    Iterator<E> iterator = iterator();
    while (true) {
      if (index == 0) {
        if (iterator.hasNext()) {
          nextElement = iterator.next();
        } else {
          nextElement = null;
        }
        break;
      } else {
        if (iterator.hasNext()) {
          iterator.next();
          index--;
        } else {
          throw new IndexOutOfBoundsException();
        }
      }
    }

    // Get the session
    ChromatticSessionImpl session = parentCtx.getSession();

    // Get the added context
    EntityContext addedCtx = session.unwrapEntity(addedElement);

    //
    switch (addedCtx.getStatus()) {
      case TRANSIENT:
        parentCtx.addChild(addedCtx);
        break;
      case PERSISTENT:
        Object insertedParent = addedCtx.getParent();
        EntityContext addedParentCtx = session.unwrapEntity(insertedParent);

        // It's a move
        if (addedParentCtx != parentCtx) {
          parentCtx.addChild(addedCtx);
        }
        break;
      default:
        throw new IllegalArgumentException("Cannot insert element with status " + addedCtx.getStatus());
    }

    //
    if (nextElement == null) {
      parentCtx.orderBefore(addedCtx, null);
    } else {
      EntityContext nextCtx = session.unwrapEntity(nextElement);
      parentCtx.orderBefore(addedCtx, nextCtx);
    }
  }

  @Override
  public E set(int index, E addedElement) {
    if (addedElement == null) {
      throw new NullPointerException("No null element can be inserted");
    }
    if (!relatedClass.isInstance(addedElement)) {
      throw new ClassCastException("Cannot cast object with class " + addedElement.getClass().getName() + " as child expected class " + relatedClass.getName());
    }

    // Get the removed element
    E removedElement = get(index);

    // Get the session
    ChromatticSessionImpl session = parentCtx.getSession();

    // Unwrap the removed element
    EntityContext removedCtx = session.unwrapEntity(removedElement);

    // Unwrap the added element
    EntityContext addedCtx = session.unwrapEntity(addedElement);

    //
    switch (addedCtx.getStatus()) {
      case TRANSIENT:
        parentCtx.addChild(addedCtx);
        break;
      case PERSISTENT:
        Object insertedParent = addedCtx.getParent();
        EntityContext addedParentCtx = session.unwrapEntity(insertedParent);

        // It's a move
        if (addedParentCtx != parentCtx) {
          parentCtx.addChild(addedCtx);
        }
        break;
      default:
        throw new IllegalArgumentException("Cannot insert element with status " + addedCtx.getStatus());
    }

    // Order before the removed element
    parentCtx.orderBefore(addedCtx, removedCtx);

    // Remove the element
    session.remove(removedCtx);

    //
    return removedElement;
  }

  @Override
  public E remove(int index) {

    // Get the removed element
    E removedElement = get(index);

    // Get the session
    ChromatticSessionImpl session = parentCtx.getSession();

    // Unwrap the removed element
    EntityContext removedCtx = session.unwrapEntity(removedElement);

    // Remove the element
    session.remove(removedCtx);

    //
    return removedElement;
  }

  public E get(int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException();
    }
    Iterator<E> iterator = iterator();
    while (true) {
      if (iterator.hasNext()) {
        E o = iterator.next();
        if (index == 0) {
          return o;
        } else {
          index--;
        }
      } else {
        throw new IndexOutOfBoundsException();
      }
    }
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
}