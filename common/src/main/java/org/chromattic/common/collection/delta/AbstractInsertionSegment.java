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
package org.chromattic.common.collection.delta;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class AbstractInsertionSegment<E> extends Segment<E> {

  /** . */
  final LinkedList<E> insertions = new LinkedList<E>();

  @Override
  protected E localGet(int index) {
    return insertions.get(index);
  }

  @Override
  protected int localSize() {
    return insertions.size();
  }

  @Override
  protected boolean localCanAdd(int index) {
    return index <= insertions.size();
  }

  @Override
  protected void localAdd(int index, E element) {
    insertions.add(index, element);
  }

  @Override
  protected E localRemove(int index) {
    E removed = insertions.remove(index);
/*
    if (insertions.size() == 0) {
      Segment<E> previous = getPrevious();
      Segment<E> next = getNext();
      if (previous != null && next != null) {
        previous.setNext(next);
      }
    }
*/
    return removed;
  }

  @Override
  public Iterator<E> localIterator() {
    return insertions.iterator();
  }

  @Override
  protected void format(StringBuilder builder) {
    builder.append("{");
    int count = 0;
    for (E e : insertions) {
      if (count > 0) {
        builder.append(",");
      }
      builder.append(e);
      count++;
    }
    builder.append("}");
  }
}
