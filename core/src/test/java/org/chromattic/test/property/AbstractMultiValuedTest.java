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
package org.chromattic.test.property;

import junit.framework.Assert;

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractMultiValuedTest extends Assert {

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
  protected final MultiValue _objects;

  /** . */
  protected final Method getter;

  /** . */
  protected final Method setter;

  protected AbstractMultiValuedTest(
    ValueFactory factory,
    Object o,
    Node node,
    String propertyName,
    String getterName,
    String setterName,
    int propertyType,
    MultiValue objects) throws Exception {
    this.getter = o.getClass().getDeclaredMethod(getterName);
    this.setter = o.getClass().getDeclaredMethod(setterName, getter.getReturnType());
    this.factory = factory;
    this.o = o;
    this.node = node;
    this.propertyName = propertyName;
    this.getterName = getterName;
    this.setterName = setterName;
    this.propertyType = propertyType;
    this._objects = objects;
  }

  protected Value create(Object object) throws Exception {
    return factory.createValue(object.toString(), propertyType);
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

  protected void safeArrayEquals(MultiValue expectedObjects, Value[] values) throws Exception {
    assertEquals(expectedObjects.size(), values.length);
    for (int i = 0;i < values.length;i++) {
      Value v1 = create(expectedObjects.getObject(i));
      Value v2 = values[i];
      safeValueEquals(v1, v2);
    }
  }

  protected void safeArrayEquals(MultiValue expectedObjects, MultiValue objects) throws Exception {
    assertEquals(expectedObjects.size(), objects.size());
    for (int i = 0;i < expectedObjects.size();i++) {
      Object o = objects.getObject(i);
      assertEquals(expectedObjects.getObject(i), o);
    }
  }

  protected abstract void run() throws Exception;

}
