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

import org.chromattic.core.ObjectContext;
import org.chromattic.core.DomainSession;
import org.chromattic.api.Status;

import java.util.Iterator;
import java.util.AbstractList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class AnyChildList<E> extends AbstractList<E> {

  /** . */
  private final ObjectContext parentCtx;

  /** . */
  private final Class<E> relatedClass;

  public AnyChildList(ObjectContext parentCtx, Class<E> relatedClass) {
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
    DomainSession session = parentCtx.getSession();

    // Get the added context
    ObjectContext addedCtx = session.unwrap(addedElement);

    //
    switch (addedCtx.getStatus()) {
      case TRANSIENT:
        parentCtx.addChild(addedCtx);
        break;
      case PERSISTENT:
        Object insertedParent = addedCtx.getParent();
        ObjectContext addedParentCtx = session.unwrap(insertedParent);

        //
        if (addedParentCtx != parentCtx) {
          throw new UnsupportedOperationException("Not yet supported but that should be a node move operation");
        }
        break;
      default:
        throw new IllegalArgumentException("Cannot insert element with status " + addedCtx.getStatus());
    }

    //
    if (nextElement == null) {
      parentCtx.orderBefore(addedCtx, null);
    } else {
      ObjectContext nextCtx = session.unwrap(nextElement);
      parentCtx.orderBefore(addedCtx, nextCtx);
    }
  }

  @Override
  public E set(int index, E addedElement) {
    if (addedElement == null) {
      throw new NullPointerException("No null element can be inserted");
    }

    // Get the removed element
    E removedElement = get(index);

    // Get the session
    DomainSession session = parentCtx.getSession();

    // Unwrap the removed element
    ObjectContext removedCtx = session.unwrap(removedElement);

    // Unwrap the added element
    ObjectContext addedCtx = session.unwrap(addedElement);

    //
    switch (addedCtx.getStatus()) {
      case TRANSIENT:
        parentCtx.addChild(addedCtx);
        break;
      case PERSISTENT:
        Object insertedParent = addedCtx.getParent();
        ObjectContext addedParentCtx = session.unwrap(insertedParent);

        //
        if (addedParentCtx != parentCtx) {
          throw new UnsupportedOperationException("Not yet supported but that should be a node move operation");
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
    DomainSession session = parentCtx.getSession();

    // Unwrap the removed element
    ObjectContext removedCtx = session.unwrap(removedElement);

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