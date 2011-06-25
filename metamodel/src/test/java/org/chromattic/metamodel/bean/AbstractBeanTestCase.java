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

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractBeanTestCase extends TestCase {

  /** . */
  protected final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  protected final BeanInfo beanInfo(ClassTypeInfo typeInfo) {
    return new BeanInfoBuilder(new SimpleTypeResolver()).build(typeInfo).get(typeInfo);
  }

  protected final void assertProperty(PropertyInfo<?, ?> property, String expectedName, Class<?> expectedType, AccessMode accessMode) {
    assertNotNull(property);
    assertEquals(expectedName, property.getName());
    ValueInfo value = property.getValue();
    if (property.getValueKind() == ValueKind.SINGLE) {
      assertEquals(expectedType.getName(), value.getEffectiveType().getName());
      switch (accessMode) {
        case READ_ONLY:
          assertNotNull(property.getGetter());
          assertNull(property.getSetter());
          break;
        case WRITE_ONLY:
          assertNull(property.getGetter());
          assertNotNull(property.getSetter());
          break;
        case READ_WRITE:
          assertNotNull(property.getGetter());
          assertNotNull(property.getSetter());
          break;
      }
    } else {
      fail("todo");
    }
  }

  protected final <A extends Annotation> void assertAnnotation(
    PropertyInfo<?, ?> property,
    Class<A> annotationClass,
    Map<String, Object> expectedAnnotation) {
    A ann1 = property.getAnnotation(annotationClass);
    assertNotNull(ann1);
    Map<String, Object> values = new HashMap<String, Object>();
    for (Method m : ann1.getClass().getDeclaredMethods()) {
      if (m.getName().equals("equals") && m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Object.class) {
        continue;
      }
      if (m.getName().equals("hashCode") && m.getParameterTypes().length == 0) {
        continue;
      }
      if (m.getName().equals("toString") && m.getParameterTypes().length == 0) {
        continue;
      }
      if (m.getName().equals("annotationType") && m.getParameterTypes().length == 0) {
        continue;
      }
      try {
        Object value = m.invoke(ann1);
        values.put(m.getName(), value);
      }
      catch (Exception e) {
        AssertionFailedError afe = new AssertionFailedError("Could not invoke annotation value " + m);
        afe.initCause(e);
        throw afe;
      }
    }
    assertEquals(expectedAnnotation, values);
  }
}
