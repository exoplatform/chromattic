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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ArrayMultiValueType extends MultiValueType {

  public int size(Object nativeObj) {
    return java.lang.reflect.Array.getLength(nativeObj);
  }

  public Object get(Object nativeObj, int index) {
    return java.lang.reflect.Array.get(nativeObj, index);
  }

  public void set(Object nativeObj, int index, Object object) {
    java.lang.reflect.Array.set(nativeObj, index, object);
  }

  public Object create(Class<?> componentType, int size) {
    return java.lang.reflect.Array.newInstance(componentType, size);
  }

  public Object array(Class<?> componentType, int size) {
    return java.lang.reflect.Array.newInstance(componentType, size);
  }

  public Class<?> componentType(Object nativeObj) {
    return nativeObj.getClass().getComponentType();
  }
}
