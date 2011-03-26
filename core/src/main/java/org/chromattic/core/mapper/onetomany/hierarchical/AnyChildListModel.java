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

import org.chromattic.common.ListModel;
import org.chromattic.common.ElementInsertion;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.DomainSession;
import org.chromattic.api.Status;

import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnyChildListModel<E> implements ListModel<E> {

  /** . */
  private final ObjectContext parentCtx;

  /** . */
  private final Class<E> relatedClass;

  public AnyChildListModel(ObjectContext parentCtx, Class<E> relatedClass) {
    this.parentCtx = parentCtx;
    this.relatedClass = relatedClass;
  }

  public Iterator<E> iterator() {
    return parentCtx.getChildren(relatedClass);
  }

  public void set(int index, E removedElement, E addedElement) {
    if (addedElement == null) {
      throw new NullPointerException("No null element can be inserted");
    }

    //
    DomainSession session = parentCtx.getSession();

    //
    ObjectContext ctx = session.unwrap(addedElement);

    //
    ObjectContext removedCtx = session.unwrap(removedElement);

    // Add as child
    parentCtx.addChild(ctx);

    // Order before ctx
    parentCtx.orderBefore(ctx, removedCtx);

    // Remove ctx
    session.remove(removedCtx);
  }

  public void add(ElementInsertion<E> insertion) {
    if (insertion.getElement() == null) {
      throw new NullPointerException("No null element can be inserted");
    }

    //
    DomainSession session = parentCtx.getSession();

    //
    ObjectContext ctx = session.unwrap(insertion.getElement());
    Status status = ctx.getStatus();

    //
    if (status == Status.TRANSIENT) {
      parentCtx.addChild(ctx);
    } else if (status == Status.PERSISTENT) {
      throw new UnsupportedOperationException("Not yet supported but that should be a node move operation");
/*
      Object insertedParent = ctx.getParent();
      ObjectContext insertedParentCtx = session.unwrap(insertedParent);

      //
      if (insertedParentCtx != parentCtx) {
        throw new UnsupportedOperationException("Not yet supported but that should be a node move operation");
      }

      //
      if (insertion instanceof ElementInsertion.Singleton) {
        throw new AssertionError("impossible");
      }
*/
    } else {
      throw new IllegalArgumentException();
    }

    //
    if (insertion instanceof ElementInsertion.Singleton) {
      // Nothing to do
    } else if (insertion instanceof ElementInsertion.First) {
      ElementInsertion.First first = (ElementInsertion.First)insertion;
      ObjectContext nextCtx = session.unwrap(first.getNext());
      parentCtx.orderBefore(ctx, nextCtx);
    } else if (insertion instanceof ElementInsertion.Middle) {
      ElementInsertion.Middle middle = (ElementInsertion.Middle)insertion;
      ObjectContext nextCtx = session.unwrap(middle.getNext());
      parentCtx.orderBefore(ctx, nextCtx);
    } else {
      ElementInsertion.Last last = (ElementInsertion.Last)insertion;
      ObjectContext previousCtx = session.unwrap(last.getPrevious());
      parentCtx.orderBefore(previousCtx, ctx);
    }
  }

  public void remove(int index, E removedElement) {
    DomainSession session = parentCtx.getSession();
    session.remove(removedElement);
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
