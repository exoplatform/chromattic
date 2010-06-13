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

import junit.framework.TestCase;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

/**
 * This test case checks various assertions on JavaBean as defined by java.beans.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JavaBeanTestCase extends TestCase {

  public void testReadOnlyProperty() throws Exception {
    class A {
      public String getA() { return null; }
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNull(pd[0].getWriteMethod());
  }

  public void testWriteOnlyProperty() throws Exception {
    class A {
      public void setA(String a) {}
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
  }

  public void testReadWriteProperty() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
  }

  /*
   * The getter is inherited.
   */
  public void testGetterInheritance() throws Exception {
    class A {
      public String getA() { return null; }
    }
    class B extends A {
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNull(pd[0].getWriteMethod());
  }

  /*
   * The setter is inherited.
   */
  public void testSetterInheritance() throws Exception {
    class A {
      public void setA(String a) {}
    }
    class B extends A {
    }
    BeanInfo info  = Introspector.getBeanInfo(B.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
  }

  /*
   * The getter and setter are inherited.
   */
  public void testGetterSetterInheritance() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    class B extends A {
    }
    BeanInfo info  = Introspector.getBeanInfo(B.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
  }

  /*
   * The getter primes over any setter with the same name, which makes sense as setter signature can be
    * overloaded.
   */
  public void testGetterAndSetterWithDifferentType() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(Object a) {}
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNull(pd[0].getWriteMethod());
  }

  /*
   * The property becomes read/write as it inherits the getter from its super class.
   */
  public void testReadWriteSubclass() throws Exception {
    class A {
      public String getA() { return null; }
    }
    class B extends A {
      public void setA(String a) {}
    }
    BeanInfo info  = Introspector.getBeanInfo(B.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(String.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
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
    BeanInfo info  = Introspector.getBeanInfo(B.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(Object.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNull(pd[0].getWriteMethod());
  }

  /*
   * At the moment which property will be chosen is not determined by any formal or logicial statement.
   */
  public void testSetterOverloading() throws Exception {
    class A {
      public void setA(String a) {}
      public void setA(Integer a) {} 
    }
    BeanInfo info  = Introspector.getBeanInfo(A.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertNull(pd[0].getReadMethod());
    assertNotNull(pd[0].getWriteMethod());
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
    BeanInfo info  = Introspector.getBeanInfo(B.class, Object.class);
    PropertyDescriptor[] pd = info.getPropertyDescriptors();
    assertEquals(1, pd.length);
    assertEquals("a", pd[0].getName());
    assertEquals(Number.class, pd[0].getPropertyType());
    assertNotNull(pd[0].getReadMethod());
    assertNull(pd[0].getWriteMethod());
  }
}
