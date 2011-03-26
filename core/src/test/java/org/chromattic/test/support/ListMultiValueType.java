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

import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Array;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ListMultiValueType extends MultiValueType {

  public int size(Object nativeObj) {
    return ((List)nativeObj).size();
  }

  public Object get(Object nativeObj, int index) {
    return ((List)nativeObj).get(index);
  }

  public void set(Object nativeObj, int index, Object object) {
    ((List)nativeObj).set(index, object);
  }

  public Object create(Class<?> componentType, int size) {
    return Arrays.asList((Object[])Array.newInstance(componentType, size));
  }

  public Object array(Class<?> componentType, int size) {
    return java.lang.reflect.Array.newInstance(componentType, size);
  }

  public Class<?> componentType(Object nativeObj) {
    return ((List)nativeObj).toArray().getClass().getComponentType();
  }
}
