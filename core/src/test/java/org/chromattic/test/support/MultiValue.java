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

package org.chromattic.test.support;

import junit.framework.AssertionFailedError;

import java.util.Arrays;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class MultiValue {

  public static MultiValue create(Object o) {
    if (o.getClass().isArray()) {
      return new Array(o);
    } else {
      return new List (((java.util.List<Object>)o).toArray());
    }
  }

  /** . */
  private final MultiValueType type;

  protected MultiValue(MultiValueType type) {
    this.type = type;
  }

  public final Object asNative() {
    int length = type.size(get());
    Object n = type.create(type.componentType(get()), length);
    for (int i = 0;i < length;i++) {
      Object o = type.get(get(), i);
      type.set(n, i, o);
    }
    return n;
  }

  public final Object asArray() {
    int length = size();
    Class<?> componentType = type.componentType(get());
    Object array = type.array(componentType, length);
    for (int i = 0;i < length;i++) {
      Object o = type.get(get(), i);
      MultiValueType.ARRAY.set(array, i, o);
    }
    return array;
  }

  public final Object getObject(int index) {
    Object o = type.get(get(), index);
    if (o instanceof InputStream) {
      try {
        InputStream in = (InputStream)o;
        byte[] buffer = new byte[512];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int l = in.read(buffer);l != -1;l = in.read(buffer)) {
          out.write(buffer, 0, l);
        }
        byte[] bytes = out.toByteArray();
        o = new ByteArrayInputStream(bytes);
        in.reset();
      }
      catch (IOException e) {
        AssertionFailedError err = new AssertionFailedError();
        err.initCause(e);
        throw err;
      }
    }
    return o;
  }

  public final void setObject(int index, Object object) {
    type.set(get(), index, object);
  }

  public final MultiValue sub(int... indices) {
    Class<?> componentType = type.componentType(get());
    Object sub = type.create(componentType, indices.length);
    MultiValue submv = create(sub);
    for (int i = 0;i < indices.length;i++) {
      Object object = getObject(indices[i]);
      submv.setObject(i, object);
    }
    return submv;
  }

  public int size() {
    return type.size(get());
  }

  protected abstract Object get();

  public static class Array extends MultiValue {

    /** . */
    final Object objects;

    public Array(Object objects) {
      super(MultiValueType.ARRAY);

      //
      this.objects = objects;
    }

    protected Object get() {
      return objects;
    }
  }

  public static class List extends MultiValue {

    /** . */
    final java.util.List<Object> list;

    public List(Object... objects) {
      super(MultiValueType.LIST);

      //
      this.list = Arrays.asList(objects);
    }

    protected Object get() {
      return list;
    }
  }
}
