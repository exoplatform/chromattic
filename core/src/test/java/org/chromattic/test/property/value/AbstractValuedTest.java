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
package org.chromattic.test.property.value;

import junit.framework.Assert;

import javax.jcr.ValueFactory;
import javax.jcr.Node;
import javax.jcr.Value;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

import org.chromattic.test.support.MultiValue;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractValuedTest extends Assert {

  /** . */
  protected final ValueFactory factory;

  /** . */
  protected final Object o;

  /** . */
  protected final Node node;

  /** . */
  protected final String propertyName;

  /** . */
  protected final String getterName;

  /** . */
  protected final String setterName;

  /** . */
  protected final int propertyType;

  /** . */
  protected final Method getter;

  /** . */
  protected final Method setter;

  /** . */
  protected final MultiValue values;

  /** . */
  protected final boolean primitive;

  protected AbstractValuedTest(
    ValueFactory factory,
    Object o,
    Node node,
    String propertyName,
    String getterName,
    String setterName,
    int propertyType,
    MultiValue values) throws Exception {
    this.getter = o.getClass().getDeclaredMethod(getterName);
    this.setter = o.getClass().getDeclaredMethod(setterName, getter.getReturnType());
    this.factory = factory;
    this.o = o;
    this.node = node;
    this.propertyName = propertyName;
    this.getterName = getterName;
    this.setterName = setterName;
    this.propertyType = propertyType;
    this.values = values;
    this.primitive = getter.getReturnType().isPrimitive();

  }

  private static final Map<Class<?>, Class<?>> primitiveToWrapper;

  static {
    Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
    map.put(boolean.class, Boolean.class);
    map.put(byte.class, Byte.class);
    map.put(short.class, Short.class);
    map.put(int.class, Integer.class);
    map.put(long.class, Long.class);
    map.put(float.class, Double.class);
    map.put(double.class, Double.class);
    primitiveToWrapper = map;
  }

  protected Value create(Object object) throws Exception {
    if (object instanceof Date) {
      Calendar c = Calendar.getInstance();
      c.setTime((Date)object);
      return factory.createValue(c);
    } else if (object instanceof InputStream) {
      return factory.createValue((InputStream)object);
    } else {
      return factory.createValue(object.toString(), propertyType);
    }
  }

  protected void safeValueEquals(Value v1, Value v2) throws Exception {
    if (v1 == null) {
      assertNull(v2);
    } else {
      assertNotNull(v2);
      assertEquals(v1.getType(), v2.getType());
      assertEquals(v1.getString(), v2.getString());
    }
  }

  protected void safeValueEquals(Object v1, Value v2) throws Exception {
    safeValueEquals(create(v1), v2);
  }

  protected void safeValueEquals(Object v1, Object v2) throws Exception {
    safeValueEquals(create(v1), create(v2));
  }

  protected void safeArrayEquals(MultiValue expectedObjects, Value[] values) throws Exception {
    if (expectedObjects == null) {
      assertNull(values);
    } else {
      assertNotNull(values);
      assertEquals(expectedObjects.size(), values.length);
      for (int i = 0;i < values.length;i++) {
        Value v1 = create(expectedObjects.getObject(i));
        Value v2 = values[i];
        safeValueEquals(v1, v2);
      }
    }
  }

  protected void safeArrayEquals(MultiValue expectedObjects, MultiValue objects) throws Exception {
    if (expectedObjects == null) {
      assertNull(objects);
    } else {
      assertNotNull(objects);
      assertEquals(expectedObjects.size(), objects.size());
      for (int i = 0;i < expectedObjects.size();i++) {
        Object o = objects.getObject(i);
        assertEquals(expectedObjects.getObject(i), o);
      }
    }
  }

  public abstract void run() throws Exception;

}
