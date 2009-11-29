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
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DeltaList<E> {

  /** . */
  private Segment<E> head;

  /** . */
  private Segment<E> tail;

  /** . */
  final List<E> list;

  public DeltaList(List<E> list) {
    InPlaceSegment<E> head = new InPlaceSegment<E>(this);
    head.listIndex = 0;
    head.listSize = list.size();

    //
    InsertionSegment<E> tail = new InsertionSegment<E>();

    //
    head.addAfter(tail);

    //
    this.list = list;
    this.head = head;
    this.tail = tail;
  }

  public E get(int index) {
    return head.get(index);
  }

  public void add(int index, E e) {
    head.add(index, e);
  }

  public E remove(int index) {
    return head.remove(index);
  }

  public int size() {
    return head.size();
  }

  public Iterator<E> iterator() {
    return head.iterator();
  }
}
