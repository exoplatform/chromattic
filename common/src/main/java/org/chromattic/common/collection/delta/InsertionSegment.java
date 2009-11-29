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

import java.util.LinkedList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class InsertionSegment<E> extends Segment<E> {

  /** . */
  final LinkedList<E> insertions = new LinkedList<E>();

  @Override
  public E get(int index) {
    if (index < insertions.size()) {
      return insertions.get(index);
    } else {
      return super.get(index - insertions.size());
    }
  }

  @Override
  public void add(int index, E e) {
    if  (index <= insertions.size()) {
      insertions.add(index, e);
    } else {
      super.add(index - insertions.size(), e);
    }
  }

  @Override
  public E remove(int index) {
    if (index < insertions.size()) {
      return insertions.remove(index);
    } else {
      return super.remove(index - insertions.size());
    }
  }

  @Override
  public int size() {
    return insertions.size() + super.size();
  }
}
