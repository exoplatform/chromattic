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

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class AnyChildCollection extends AbstractCollection<Object> {

  /** . */
  private final ObjectContext parentCtx;

  /** . */
  private final Class<?> relatedClass;

  public AnyChildCollection(ObjectContext parentCtx, Class<?> relatedClass) {
    this.relatedClass = relatedClass;
    this.parentCtx = parentCtx;
  }

  @Override
  public boolean add(Object child) {
    parentCtx.addChild(child);
    return true;
  }

  public Iterator<Object> iterator() {
    return (Iterator<Object>)parentCtx.getChildren(relatedClass);
  }

  public int size() {
    int size = 0;
    Iterator<Object> iterator = iterator();
    while (iterator.hasNext()) {
      iterator.next();
      size++;
    }
    return size;
  }
}
