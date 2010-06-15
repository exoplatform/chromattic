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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BuilderTestCase extends TestCase {

  /** . */
  protected TypeResolver<Type> domain;

  @Override
  protected void setUp() throws Exception {
    domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());
  }

  @Override
  protected void tearDown() throws Exception {
    domain = null;
  }

  public void testDeadLock() throws Exception {
    class A {
      class B {
        public A getA() {
          return null;
        }
      }
      public B getB() { return null; }
    }

    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo) domain.resolve(A.B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
   }

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
    assertSame(domain.resolve(String.class), ap.getValue().getType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(String.class), bp.getValue().getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertSame(domain.resolve(String.class), cp.getValue().getType());
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
    assertSame(domain.resolve(String.class), ap.getValue().getType());
    assertNull(ap.getParent());
    assertNull(ap.getGetter());
    assertNotNull(ap.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(String.class), bp.getValue().getType());
    assertSame(ap, bp.getParent());
    assertNull(bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertSame(domain.resolve(String.class), cp.getValue().getType());
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
    assertSame(domain.resolve(String.class), ap.getValue().getType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNotNull(ap.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(String.class), bp.getValue().getType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());

    //
    PropertyInfo cp = ci.getProperty("a");
    assertNotNull(cp);
    assertSame(domain.resolve(String.class), cp.getValue().getType());
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
    assertSame(domain.resolve(String.class), bp.getValue().getType());
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
    assertSame(domain.resolve(String.class), bp.getValue().getType());
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
    assertSame(domain.resolve(Number.class), ap.getValue().getClassType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
//    assertSame(domain.resolve(Integer.class), bp.getType());
    assertSame(domain.resolve(Integer.class), bp.getValue().getClassType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter()); 
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
    assertSame(domain.resolve(String.class), ai.getProperty("a").getValue().getType());
    assertSame(null, ap.getParent());
    assertNotNull(ap.getGetter());
    assertNull(ap.getSetter());
  }

  public void testBeanProperty() throws Exception {

    class A {
    }
    class B {
      public A getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo) domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(A.class), bp.getValue().getType());
    assertSame(domain.resolve(A.class), bp.getValue().getClassType());
    assertTrue(bp.getValue() instanceof BeanValueInfo);
    assertEquals(ai, ((BeanValueInfo)bp.getValue()).getBean());
    assertSame(null, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());
  }

  public void testSelfBeanProperty() throws Exception {

    class A {
      public A getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a));
    BeanInfo ai = beans.get(a);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertSame(domain.resolve(A.class), ap.getValue().getType());
    assertSame(domain.resolve(A.class), ap.getValue().getClassType());
    assertTrue(ap.getValue() instanceof BeanValueInfo);
    assertEquals(ai, ((BeanValueInfo)ap.getValue()).getBean());
    assertSame(null, ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());
  }

  public void testGenericSelfBeanProperty() throws Exception {

    class A<X> {
      public X getA() { return null; }
    }
    class B extends A<B> {
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo o = (ClassTypeInfo) domain.resolve(Object.class);
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo) domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(o, a, b));
    BeanInfo oi = beans.get(o);
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    // assertSame(o, ap.getType()); <X>
    assertSame(o, ap.getValue().getClassType());
    assertTrue(ap.getValue() instanceof BeanValueInfo);
    assertEquals(oi, ((BeanValueInfo)ap.getValue()).getBean());
    assertSame(null, ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
//    assertSame(domain.resolve(B.class), bp.getType());
    assertSame(domain.resolve(B.class), bp.getValue().getClassType());
    assertTrue(bp.getValue() instanceof BeanValueInfo);
    assertEquals(bi, ((BeanValueInfo)bp.getValue()).getBean());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());
  }

  public void testCollectionBeanProperty() throws Exception {

    class A {
    }
    class B extends A {
      public Collection<A> getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a, b));
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);

    //
    MultiValuedPropertyInfo bp = (MultiValuedPropertyInfo)bi.getProperty("a");
    assertNotNull(bp);
    assertEquals(MultiValueKind.COLLECTION, bp.getKind());
    // assertSame(o, ap.getType()); <X>
//    assertSame(o, ap.getValue().getClassType());
    assertTrue(bp.getValue() instanceof BeanValueInfo);
    assertEquals(ai, ((BeanValueInfo)bp.getValue()).getBean());
    assertSame(null, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNull(bp.getSetter());
  }

  public void testVariableResolveToBeanCollectionProperty() throws Exception {

    class A {
    }
    class B<X> {
      public Collection<X> getA() { return null; }
    }
    class C extends B<A> {
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo o = (ClassTypeInfo)domain.resolve(Object.class);
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    ClassTypeInfo b = (ClassTypeInfo)domain.resolve(B.class);
    ClassTypeInfo c = (ClassTypeInfo)domain.resolve(C.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(o, a, b, c));
    BeanInfo oi = beans.get(o);
    BeanInfo ai = beans.get(a);
    BeanInfo bi = beans.get(b);
    BeanInfo ci = beans.get(c);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ci.getProperty("a");
    assertNotNull(cp);
    assertEquals(MultiValueKind.COLLECTION, cp.getKind());
    // assertSame(o, ap.getType()); <X>
//    assertSame(o, ap.getValue().getClassType());
    assertTrue(cp.getValue() instanceof BeanValueInfo);
    assertEquals(ai, ((BeanValueInfo)cp.getValue()).getBean());
//    assertSame(null, cp.getParent());
//    assertNotNull(bp.getGetter());
//    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
//    assertNull(bp.getSetter());
  }

  public void testSimplePropertyList() throws Exception {
    
    class A {
      public List<String> getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a));
    BeanInfo ai = beans.get(a);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.LIST, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
    assertEquals(domain.resolve(String.class), value.getType());
  }

  public void testSimplePropertyArray() throws Exception {

    class A {
      public String[] getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a));
    BeanInfo ai = beans.get(a);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.ARRAY, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
    assertEquals(domain.resolve(String.class), value.getType());
  }

  public void testPropertyMap() throws Exception {

    class A {
      public Map<String, ?> getA() { return null; }
    }

    //
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo)domain.resolve(A.class);
    Map<ClassTypeInfo, BeanInfo> beans = builder.build(Collections.set(a));
    BeanInfo ai = beans.get(a);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.MAP, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
    assertEquals(domain.resolve(Object.class), value.getClassType());
  }
}