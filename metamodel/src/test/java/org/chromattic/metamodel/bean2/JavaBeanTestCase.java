/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.bean2;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This test case checks various assertions on JavaBean as defined by java.beans.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JavaBeanTestCase extends BeanTestCase {

  @Override
  protected Collection<PropertyMetaData> buildMetaData(Class<?> beanClass) throws Exception {
    java.beans.BeanInfo info  = Introspector.getBeanInfo(beanClass, Object.class);
    List<PropertyMetaData> res = new ArrayList<PropertyMetaData>();
    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
      res.add(new PropertyMetaData(pd));
    }
    return res;
  }

  /*
   * Surprisingly the property type is Object and not String.
   */
  public void testCovariantReturnTypeSubclass() throws Exception {
    class A {
      public Object getA() { return null; }
    }
    class B extends A {
      public String getA() { return null; }
    }
    assertProperties(B.class, new PropertyMetaData("a", Object.class, AccessMode.READ_ONLY));
  }

  /*
   * The upper bound is used.
   */
  public void testGeneric() throws Exception {
    class A<T extends Number> {
      public T getA() { return null; }
    }
    class B extends A<Integer> {
    }
    assertProperties(A.class, new PropertyMetaData("a", Number.class, AccessMode.READ_ONLY));
    assertProperties(B.class, new PropertyMetaData("a", Number.class, AccessMode.READ_ONLY));
  }
}
