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

import org.chromattic.common.collection.Collections;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodSignature;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticBeanTestCase extends BeanTestCase {

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

  @Override
  protected Collection<PropertyMetaData> buildMetaData(Class<?> beanClass) throws Exception {
    BeanInfoBuilder builder = new BeanInfoBuilder();
    ClassTypeInfo a = (ClassTypeInfo) domain.resolve(beanClass);
    BeanInfo bean = builder.build(Collections.set(a)).get(a);
    List<PropertyMetaData> res = new ArrayList<PropertyMetaData>();
    for (PropertyInfo pi : bean.getProperties().values()) {
      res.add(new PropertyMetaData(pi));
    }
    return res;
  }

  private Map<Class<?>, BeanInfo> buildInfo(Class<?>... classes)  throws Exception {
    BeanInfoBuilder builder = new BeanInfoBuilder();
    Set<ClassTypeInfo> classTypes = new HashSet<ClassTypeInfo>();
    for (Class<?> clazz : classes) {
      classTypes.add((ClassTypeInfo)domain.resolve(clazz));
    }
    Map<ClassTypeInfo, BeanInfo> classTypeToBean = builder.build(classTypes);
    Map<Class<?>, BeanInfo> classToBean = new HashMap<Class<?>, BeanInfo>();
    for (Map.Entry<ClassTypeInfo, BeanInfo> entry : classTypeToBean.entrySet()) {
      classToBean.put((Class<?>)entry.getKey().unwrap(), entry.getValue());
    }
    return classToBean;
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
    assertProperties(A.B.class, new PropertyMetaData("a", A.class, AccessMode.READ_ONLY));
   }

  public void testClassInheritance() throws Exception {

    class A {}
    class B extends A {}
    class C extends B {}

    //
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class, C.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo ci = beans.get(C.class);

    //
    assertEquals(2, beans.size());
    assertNotNull(ai);
    assertNotNull(ci);
    assertSame(null, ai.getParent());
    assertSame(ai, ci.getParent());
  }

/*
  public void testReadWritePropertyInheritsWriteOnlyPropertyBilto() throws Exception {

    class A {
      public void setA(String a) { }
      public void setA(Integer a) { }
    }
    class B extends A {
      public String getA() { return null; }
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
    assertSame(domain.resolve(String.class), bp.getValue().getDeclaredType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(bi.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter());
    assertNotNull(bp.getSetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("setA", String.class)), bp.getSetter());
  }
*/

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
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class, B.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);

    //
    PropertyInfo ap = ai.getProperty("a");

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(String.class), bp.getValue().getDeclaredType());
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
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class, B.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
//    assertSame(domain.resolve(Number.class), ap.getValue().getEffectiveType());
    assertNull(ap.getParent());
    assertNotNull(ap.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), ap.getGetter());
    assertNull(ap.getSetter());

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
//    assertSame(domain.resolve(Integer.class), bp.getType());
    assertSame(domain.resolve(Integer.class), bp.getValue().getEffectiveType());
    assertSame(ap, bp.getParent());
    assertNotNull(bp.getGetter());
    assertSame(ai.classType.getDeclaredMethod(new MethodSignature("getA")), bp.getGetter()); 
    assertNull(bp.getSetter());
  }

  /*
   * When a setter is declared with a subclass of the getter, the property is read only.
   */
  public void testPropertySetterWithPropertyGetterTypeSubclass() throws Exception {
    class A {
      public Number getA() { return null; }
      public void setA(Integer a) { }
    }
    assertProperties(A.class, new PropertyMetaData("a", Number.class, AccessMode.READ_ONLY));
  }

  public void testBeanProperty() throws Exception {

    class A {
    }
    class B {
      public A getA() { return null; }
    }

    //
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class, B.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);

    //
    PropertyInfo bp = bi.getProperty("a");
    assertNotNull(bp);
    assertSame(domain.resolve(A.class), bp.getValue().getDeclaredType());
    assertSame(domain.resolve(A.class), bp.getValue().getEffectiveType());
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
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class);
    BeanInfo ai = beans.get(A.class);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    assertSame(domain.resolve(A.class), ap.getValue().getDeclaredType());
    assertSame(domain.resolve(A.class), ap.getValue().getEffectiveType());
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
    Map<Class<?>, BeanInfo> beans = buildInfo(Object.class, A.class, B.class);
    BeanInfo oi = beans.get(Object.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);

    //
    PropertyInfo ap = ai.getProperty("a");
    assertNotNull(ap);
    // assertSame(o, ap.getType()); <X>
    assertSame(Object.class, ap.getValue().getEffectiveType().unwrap());
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
    assertSame(domain.resolve(B.class), bp.getValue().getEffectiveType());
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
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class, B.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);

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
    Map<Class<?>, BeanInfo> beans = buildInfo(Object.class, A.class, B.class, C.class);
    BeanInfo oi = beans.get(Object.class);
    BeanInfo ai = beans.get(A.class);
    BeanInfo bi = beans.get(B.class);
    BeanInfo ci = beans.get(C.class);

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
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class);
    BeanInfo ai = beans.get(A.class);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.LIST, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
    assertEquals(domain.resolve(String.class), value.getDeclaredType());
  }

  public void testSimplePropertyArray() throws Exception {

    class A {
      public String[] getA() { return null; }
    }

    //
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class);
    BeanInfo ai = beans.get(A.class);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.ARRAY, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
    assertEquals(domain.resolve(String.class), value.getDeclaredType());
  }

  public void testPropertyMap() throws Exception {

    class A {
      public Map<String, ?> getA() { return null; }
    }

    //
    Map<Class<?>, BeanInfo> beans = buildInfo(A.class);
    BeanInfo ai = beans.get(A.class);

    //
    MultiValuedPropertyInfo cp = (MultiValuedPropertyInfo)ai.getProperty("a");
    assertEquals(MultiValueKind.MAP, cp.getKind());
    SimpleValueInfo value = (SimpleValueInfo)cp.getValue();
//    assertEquals(domain.resolve(Object.class), value.getEffectiveType());
  }
}