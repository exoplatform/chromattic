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
package org.chromattic.test.bean;

import org.chromattic.bean.BeanInfo;
import org.reflext.api.ClassTypeInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyAnnotationTestCase extends AbstractBeanTestCase {

  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Ann1 {
    String value();
  }

  public static class Foo1 {

    @Ann1("Ann1_Foo1")
    public String getA() {
      throw new UnsupportedOperationException();
    }

    public void setA(String a) {
      throw new UnsupportedOperationException();
    }
  }

  public static class Foo1_1 extends Foo1 { }

  public void testFoo1() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo1.class);
    BeanInfo beanInfo = new BeanInfo(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertAnnotation(beanInfo.getProperty("a"), Ann1.class, Collections.singletonMap("value", (Object)"Ann1_Foo1"));
  }

  public void testFoo1_1() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo1_1.class);
    BeanInfo beanInfo = new BeanInfo(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertAnnotation(beanInfo.getProperty("a"), Ann1.class, Collections.singletonMap("value", (Object)"Ann1_Foo1"));
  }

  public static class Foo2 {

    public String getA() {
      throw new UnsupportedOperationException();
    }

    @Ann1("Ann1_Foo1")
    public void setA(String a) {
      throw new UnsupportedOperationException();
    }
  }

  public void testFoo2() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo2.class);
    BeanInfo beanInfo = new BeanInfo(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertAnnotation(beanInfo.getProperty("a"), Ann1.class, Collections.singletonMap("value", (Object)"Ann1_Foo1"));
  }

  public static class Foo3 {

    @Ann1("Ann1_Foo1")
    public String getA() {
      throw new UnsupportedOperationException();
    }

    @Ann1("Ann1_Foo1")
    public void setA(String a) {
      throw new UnsupportedOperationException();
    }
  }

  public void testFoo3() {
/*
    try {
      ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo3.class);
      fail();
    }
    catch (IllegalStateException e) {
    }
*/
  }

  public abstract static class Foo4 {

    @Ann1("Ann1_Foo1")
    public abstract String getA();

    public abstract void setA(String a);
  }

  public void testFoo4() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo4.class);
    BeanInfo beanInfo = new BeanInfo(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertAnnotation(beanInfo.getProperty("a"), Ann1.class, Collections.singletonMap("value", (Object)"Ann1_Foo1"));
  }
}
