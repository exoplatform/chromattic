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
package org.chromattic.common.collection;

import java.util.AbstractSequentialList;
import java.util.ListIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BufferingList<E> extends AbstractSequentialList<E> {

  /** . */
  private final ListModel<E> model;

  public BufferingList(ListModel<E> model) {
    this.model = model;
  }

  public ListIterator<E> listIterator(int index) {
    BufferingListIterator<E> iterator = new BufferingListIterator<E>(model);
    while (index-- > 0) {
      iterator.next();
    }
    return iterator;
  }

  public int size() {
    return model.size();
  }
}
