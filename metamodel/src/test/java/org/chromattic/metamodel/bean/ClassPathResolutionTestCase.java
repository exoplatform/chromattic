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

package org.chromattic.metamodel.bean;

import junit.framework.TestCase;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ClassPathResolutionTestCase extends TestCase {

  class A { }

  class B {
    A getRelated() { return null; }
  }

  private ClassTypeInfo cti;

  @Override
  protected void setUp() throws Exception {
    TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());
    cti = (ClassTypeInfo)domain.resolve(B.class);
  }

  public void testAcceptRelated() {
    BeanInfoBuilder info = new BeanInfoBuilder(new BeanFilter() {
      public boolean accept(ClassTypeInfo cti) {
        return cti.getName().equals(A.class.getName());
      }
    });
    Map<ClassTypeInfo, BeanInfo> beans = info.build(cti);
    assertEquals(2, beans.size());
    BeanInfo bBI = beans.get(cti);
    SingleValuedPropertyInfo<?> relatedPI = (SingleValuedPropertyInfo<?>)bBI.getProperty("related");
    BeanValueInfo relatedVI = (BeanValueInfo)relatedPI.getValue();
    BeanInfo aBI = relatedVI.getBean();
    assertEquals(A.class.getName(), aBI.getClassType().getName());
    assertTrue(beans.containsKey(aBI.getClassType()));
  }

  public void testRejectRelated() {
    BeanInfoBuilder info = new BeanInfoBuilder(new BeanFilter() {
      public boolean accept(ClassTypeInfo cti) {
        return false;
      }
    });
    Map<ClassTypeInfo, BeanInfo> a = info.build(cti);
    assertEquals(1, a.size());
    BeanInfo bBI = a.get(cti);
    SingleValuedPropertyInfo<?> relatedPI = (SingleValuedPropertyInfo<?>)bBI.getProperty("related");
    SimpleValueInfo relatedVI = (SimpleValueInfo)relatedPI.getValue();
    assertEquals(A.class.getName(), relatedVI.getDeclaredType().getName());
  }

  public void testAcceptObject() {
    BeanInfoBuilder info = new BeanInfoBuilder(new BeanFilter() {
      public boolean accept(ClassTypeInfo cti) {
        return cti.getName().equals(Object.class.getName());
      }
    });
    Map<ClassTypeInfo, BeanInfo> beans = info.build(cti);
    assertEquals(2, beans.size());
    BeanInfo bBI = beans.get(cti);
    BeanInfo objectBI = bBI.getParent();
    assertNotNull(objectBI);
    assertEquals(Object.class.getName(), objectBI.getClassType().getName());
  }
}
