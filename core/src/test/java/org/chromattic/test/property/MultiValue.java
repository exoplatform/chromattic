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
package org.chromattic.test.property;

import java.util.Arrays;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class MultiValue {

  public abstract Object objects();

  public abstract Object getObject(int index);

  public abstract void setObject(int index, Object object);

  public abstract int size();

  protected abstract MultiValue create(int size);

  public static MultiValue create(Object o) {
    if (o.getClass().isArray()) {
      return new Array(o);
    } else {
      return new List(((java.util.List<Object>)o).toArray());
    }
  }

  protected MultiValue array(int... indices) {
    MultiValue array = create(indices.length);
    for (int i = 0;i < indices.length;i++) {
      Object object = getObject(indices[i]);
      array.setObject(i, object);
    }
    return array;
  }


  public static class Array extends MultiValue {

    /** . */
    final Object objects;

    public Array(Object objects) {
      this.objects = objects;
    }

    public Object objects() {
      return objects;
    }

    public Object getObject(int index) {
      return java.lang.reflect.Array.get(objects, index);
    }

    public void setObject(int index, Object object) {
      java.lang.reflect.Array.set(objects, index, object);
    }

    public int size() {
      return java.lang.reflect.Array.getLength(objects);
    }

    protected MultiValue create(int size) {
      return new Array(java.lang.reflect.Array.newInstance(objects.getClass().getComponentType(), size));
    }
  }

  public static class List extends MultiValue {

    /** . */
    final java.util.List<Object> list;

    /** . */
    final Object objects;

    public List(Object... objects) {
      this.objects = objects;
      this.list = Arrays.asList(objects);
    }

    public Object objects() {
      return list;
    }

    public Object getObject(int index) {
      return java.lang.reflect.Array.get(objects, index);
    }

    public void setObject(int index, Object object) {
      java.lang.reflect.Array.set(objects, index, object);
    }

    public int size() {
      return java.lang.reflect.Array.getLength(objects);
    }

    protected MultiValue create(int size) {
      return new List((Object[])java.lang.reflect.Array.newInstance(objects.getClass().getComponentType(), size));
    }
  }
}
