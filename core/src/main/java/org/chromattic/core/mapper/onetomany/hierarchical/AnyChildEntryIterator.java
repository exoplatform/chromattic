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

import org.chromattic.common.AbstractFilterIterator;
import org.chromattic.core.ObjectContext;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnyChildEntryIterator extends AbstractFilterIterator<Map.Entry<String, Object>, Object> {

  /** . */
  private final ObjectContext parentCtx;

  public AnyChildEntryIterator(Iterator<Object> objectIterator, ObjectContext parentCtx) throws NullPointerException {
    super(objectIterator);

    //
    this.parentCtx = parentCtx;
  }

  protected Map.Entry<String, Object> adapt(final Object internal) {
    final String name = parentCtx.getSession().getName(internal);
    return new Map.Entry<String, Object>() {
      public String getKey() {
        return name;
      }
      public Object getValue() {
        return internal;
      }
      public Object setValue(Object value) {
        throw new UnsupportedOperationException();
      }
    };
  }
}
