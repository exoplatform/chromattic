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

package org.chromattic.metamodel.bean2;

import junit.framework.TestCase;
import org.chromattic.common.collection.Collections;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodSignature;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BuilderTestCase extends TestCase {

  /** . */
  protected final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  public void testClassInheritance() throws Exception {

    class A {}
    class B extends A {}
    class C extends B {}

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo c = (ClassTypeInfo) domain.resolve(C.class);
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(c, a));
    BeanInfo ai = beans.get(a);
    BeanInfo ci = beans.get(c);

    //
    assertEquals(2, beans.size());
    assertNotNull(ai);
    assertNotNull(ci);
    assertSame(null, ai.getParent());
    assertSame(ai, ci.getParent());
  }

  public void testReadOnlyProperty() throws Exception {

    class A {
      public String getA() { return null; }
    }
    class B extends A {
    }
    class C extends A {
      public String getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    ClassTypeInfo c = (ClassTypeInfo)domain.resolve(C.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b, c));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);
    BeanInfo ci = beans.get(c);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertEquals(domain.resolve(String.class), ap.getType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(String.class), bp.getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertEquals(domain.resolve(String.class), cp.getType());
    assertSame(ap, cp.getParent());
    assertNotNull(cp.getGetter());
    assertSame(ci.classType.getDeclaredMethod(new MethodSignature("getA")), cp.getGetter());
    assertNull(cp.getSetter());
  }

  public void testWriteOnlyProperty() throws Exception {

    class A {
      public void setA(String a) { }
    }
    class B extends A {
    }
    class C extends A {
      public void setA(String a) { }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    ClassTypeInfo c = (ClassTypeInfo)domain.resolve(C.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b, c));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);
    BeanInfo ci = beans.get(c);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertEquals(domain.resolve(String.class), ap.getType());
    assertNull(ap.getParent());
    assertNull(ap.getGetter());
    assertNotNull(ap.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(String.class), bp.getType());
    assertSame(ap, bp.getParent());
    assertNull(bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertEquals(domain.resolve(String.class), cp.getType());
    assertSame(ap, cp.getParent());
    assertNull(cp.getGetter());
    assertNotNull(cp.getSetter());
    assertSame(ci.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), cp.getSetter());
  }

  public void testReadWriteProperty() throws Exception {

    class A {
      public void setA(String a) { }
      public String getA() { return null; }
    }
    class B extends A {
    }
    class C extends A {
      public void setA(String a) { }
      public String getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    ClassTypeInfo c = (ClassTypeInfo)domain.resolve(C.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b, c));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);
    BeanInfo ci = beans.get(c);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertEquals(domain.resolve(String.class), ap.getType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNotNull(ap.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(String.class), bp.getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertEquals(domain.resolve(String.class), cp.getType());
    assertSame(ap, cp.getParent());
    assertNotNull(cp.getGetter());
    assertSame(ci.classType.getDeclaredMethod(new MethodSignature("getA")), cp.getGetter());
    assertNotNull(cp.getSetter());
    assertSame(ci.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), cp.getSetter());
  }

  public void testReadWritePropertyInheritsReadOnlyProperty() throws Exception {

    class A {
      public String getA() { return null; }
    }
    class B extends A {
      public void setA(String a) { }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    PropertyInfo ap = ai.getProperty("a");

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(String.class), bp.getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());
  }

  /*
   * The getter return type can covary.
   */
  public void testPropertyCovariantGetter() throws Exception {

    class A {
      public Object getA() { return null; }
    }
    class B extends A {
      public String getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo) domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    PropertyInfo ap = ai.getProperty("a");

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(String.class), bp.getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());
  }

  public void testGenericProperty() throws Exception {

    class A<T extends Number> {
      public T getA() { return null; }
    }
    class B extends A<Integer> {
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo) domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertEquals(domain.resolve(Number.class), ap.getClassType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(domain.resolve(Integer.class), bp.getType());
    assertEquals(domain.resolve(Integer.class), bp.getClassType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertEquals(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter()); // Should be SAME and not EQUALS
    assertNull(bp.getSetter());
  }

  /*
   * A setter with a different type is ignored.
   */
  public void testPropertySetterWithDifferentType() throws Exception {

    class A {
      public String getA() { return null; }
      public void setA(Integer a) { }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a));
    BeanInfo ai = beans.get(a);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertEquals(domain.resolve(String.class), ai.getProperty("a").getType());
    assertSame(null, ap.getParent());
    assertNotNull(ap.getGetter());
    assertNull(ap.getSetter());
  }
}