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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ListType<T> {

  public static ListType<Object> ARRAY = new ListType<Object>() {

    @Override
    public void set(Object objects, int index, Object element) {
      Array.set(objects, index, element);
    }

    @Override
    public Object create(Class<?> elementType, int size) {
      return Array.newInstance(elementType, size);
    }

    @Override
    public int size(Object objects) {
      return Array.getLength(objects);
    }

    @Override
    public Object get(Object objects, int index) {
      return Array.get(objects, index);
    }
  };

  public static ListType<List<Object>> LIST = new ListType<List<Object>>() {

    @Override
    public void set(List<Object> objects, int index, Object element) {
      objects.set(index, element);
    }

    @Override
    public List<Object> create(Class<?> elementType, int size) {
      ArrayList<Object> list = new ArrayList<Object>(size);
      for (int i = 0;i < size;i++) {
        list.add(null);
      }
      return list;
    }

    @Override
    public Object get(List<Object> objects, int index) {
      return objects.get(index);
    }

    @Override
    public int size(List<Object> objects) {
      return objects.size();
    }
  };

  public abstract T create(Class<?> elementType, int size);

  public abstract void set(T t, int index, Object element);

  public abstract int size(T t);

  public abstract Object get(T t, int index);

}
