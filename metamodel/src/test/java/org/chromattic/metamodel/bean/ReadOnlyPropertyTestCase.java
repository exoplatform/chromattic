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

package org.chromattic.metamodel.bean;

import org.reflext.api.ClassTypeInfo;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReadOnlyPropertyTestCase extends AbstractBeanTestCase {

  public class A {
    public String getA() { throw new UnsupportedOperationException(); }
  }

  public void testA() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(A.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(Collections.singleton("a"), beanInfo.getPropertyNames());
    assertProperty(beanInfo.getProperty("a"), "a", String.class, AccessMode.READ_ONLY);
  }

  public class B<X> {
    public X getA() { throw new UnsupportedOperationException(); }
  }

  public void testB() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(B.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(new HashSet<String>(), beanInfo.getPropertyNames());
  }

  public class C extends B<String> {
  }

  public void testC() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(C.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(Collections.singleton("a"), beanInfo.getPropertyNames());
    assertProperty(beanInfo.getProperty("a"), "a", String.class, AccessMode.READ_ONLY);
  }
}
