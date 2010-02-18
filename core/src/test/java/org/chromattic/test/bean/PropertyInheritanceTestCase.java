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

import org.chromattic.metamodel.bean.BeanInfoFactory;
import org.reflext.api.ClassTypeInfo;
import org.chromattic.metamodel.bean.BeanInfo;
import org.chromattic.metamodel.bean.AccessMode;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyInheritanceTestCase extends AbstractBeanTestCase {

  public abstract static class Foo5_1 { public abstract Exception getA(); }
  public abstract static class Foo5_2 extends Foo5_1 { public abstract RuntimeException getA(); }

  public void testFoo5() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo5_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", RuntimeException.class, AccessMode.READ_ONLY);
  }

  public abstract static class Foo6_1 { public abstract Exception getA(); }
  public abstract static class Foo6_2 extends Foo6_1 { }

  public void testFoo6() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo6_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", Exception.class, AccessMode.READ_ONLY);
  }

  public abstract static class Foo7_1 { public abstract Exception getA(); }
  public abstract static class Foo7_2 extends Foo7_1 {
    public abstract void setA(Exception a);
  }

  public void testFoo7() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo7_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", Exception.class, AccessMode.READ_WRITE);
  }

  public abstract static class Foo8_1 { public abstract Exception getA(); }
  public abstract static class Foo8_2 extends Foo8_1 {
    public abstract RuntimeException getA();
    public abstract void setA(RuntimeException a);
  }

  public void testFoo8() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo8_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", RuntimeException.class, AccessMode.READ_WRITE);
  }

  public abstract static class Foo9_1<T extends Exception> { public abstract T getA(); }
  public abstract static class Foo9_2 extends Foo9_1<RuntimeException> { public abstract RuntimeException getA(); }

  public void testFoo9() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo9_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", RuntimeException.class, AccessMode.READ_ONLY);
  }

  public abstract static class Foo10_1<T> { public abstract T getA(); }
  public abstract static class Foo10_2 extends Foo10_1<Exception> { }

  public void testFoo10() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo10_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", Exception.class, AccessMode.READ_ONLY);
  }

  public abstract static class Foo11_1<T extends Exception> { public abstract T getA(); }
  public abstract static class Foo11_2 extends Foo11_1<RuntimeException> { }

  public void testFoo11() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo11_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("a"), "a", RuntimeException.class, AccessMode.READ_ONLY);
  }

  public interface Foo12_1 { Exception getNumber(); }
  public abstract class Foo12_2 implements Foo12_1 {
    public abstract RuntimeException getNumber();
    public abstract void setNumber(RuntimeException number);
  }

  public void testFoo12() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(Foo12_2.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("number"), "number", RuntimeException.class, AccessMode.READ_WRITE);
  }

  public interface Navigation { }
  public interface Site { Navigation getNavigation(); }
  public class NavigationImpl implements Navigation { }
  public abstract class SiteImpl implements Site { public abstract NavigationImpl getNavigation(); }
  public interface Portal extends Site { }
  public abstract class PortalImpl extends SiteImpl implements Portal { }

  public void testNav() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.getType(PortalImpl.class);
    BeanInfo beanInfo = new BeanInfoFactory().build(typeInfo);
    assertEquals(1, beanInfo.getPropertyNames().size());
    assertProperty(beanInfo.getProperty("navigation"), "navigation", NavigationImpl.class, AccessMode.READ_ONLY);
  }
}
