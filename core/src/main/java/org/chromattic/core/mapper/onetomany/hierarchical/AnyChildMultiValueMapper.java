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
import org.chromattic.metamodel.bean.ValueKind;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AnyChildMultiValueMapper<K extends ValueKind.Multi> {

  public abstract <E> Object createValue(EntityContext parentCtx, String prefix, Class<E> relatedClass);

  public static class Map extends AnyChildMultiValueMapper<ValueKind.Map> {
    public <E> Object createValue(EntityContext parentCtx, String prefix, Class<E> relatedClass) {
      return new AnyChildMap<E>(parentCtx, prefix, relatedClass);
    }
  }

  public static class Collection extends AnyChildMultiValueMapper<ValueKind.Collection> {
    public <E> Object createValue(EntityContext parentCtx, String prefix, Class<E> relatedClass) {
      return new AnyChildCollection<E>(parentCtx, prefix, relatedClass);
    }
  }

  public static class List extends AnyChildMultiValueMapper<ValueKind.Array> {
    public <E> Object createValue(EntityContext parentCtx, String prefix, Class<E> relatedClass) {
      return new AnyChildList<E>(parentCtx, prefix, relatedClass);
    }
  }
}
