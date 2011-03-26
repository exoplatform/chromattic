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
package org.chromattic.core;

import org.chromattic.common.collection.WrappedArrayList;
import org.chromattic.core.bean.SimpleType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ListType {

  public abstract <E> List<E> create(SimpleType<E> elementType, int size);

  public abstract <E> Object unwrap(SimpleType<E> elementType, List<E> list);

  public abstract <E> List<E> wrap(SimpleType<E> elementType, Object array);

  public static final ListType ARRAY = new ListType() {

    @Override
    public <E> List<E> create(SimpleType<E> elementType, int size) {
      return WrappedArrayList.create(
        elementType.getObjectType(),
        elementType.getRealType(),
        size);
    }

    @Override
    public <E> List<E> wrap(SimpleType<E> elementType, Object array) {
      return WrappedArrayList.wrap(elementType.getObjectType(), array);
    }

    @Override
    public <E> Object unwrap(SimpleType<E> elementType, List<E> list) {
      return ((WrappedArrayList)list).getArray();
    }
  };

  public static final ListType LIST = new ListType() {

    @Override
    public <E> List<E> create(SimpleType<E> elementType, int size) {
      ArrayList<E> list = new ArrayList<E>(size);
      for (int i = 0;i < size;i++) {
        list.add(null);
      }
      return list;
    }

    @Override
    public <E> List<E> wrap(SimpleType<E> elementType, Object array) {
      return (List<E>)array;
    }

    @Override
    public <E> Object unwrap(SimpleType<E> elementType, List<E> list) {
      return list;
    }
  };
}
