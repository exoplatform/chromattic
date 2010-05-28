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
package org.chromattic.test.property.single;

import junit.framework.AssertionFailedError;
import org.chromattic.test.support.MultiValue;
import org.chromattic.test.support.EventQueue;

import javax.jcr.ValueFactory;
import javax.jcr.Node;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SingleValuedMappedToSingleValuedTest extends AbstractSingleValuedTest {

  public SingleValuedMappedToSingleValuedTest(
    ValueFactory factory,
    Object o,
    Node node,
    String propertyName,
    String getterName,
    String setterName,
    int propertyType,
    MultiValue values,
    EventQueue events) throws Exception {
    super(factory, o, node, propertyName, getterName, setterName, propertyType, values, events);
  }

  public void run() throws Exception {
    try {
      assertEquals(null, getter.invoke(o));
      assertFalse(primitive);
    }
    catch (InvocationTargetException e) {
      if (e.getCause() instanceof IllegalStateException) {
        assertTrue(primitive);
      } else {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
    }
    events.assertEmpty();

    //
    node.setProperty(propertyName, create(values.getObject(0)));
    safeValueEquals(values.getObject(0), getter.invoke(o));
    setter.invoke(o, values.getObject(1));
    assertTrue(node.hasProperty(propertyName));
    safeValueEquals(values.getObject(1), node.getProperty(propertyName).getValue());
    events.assertPropertyChangedEvent(node.getUUID(), o, propertyName, values.getObject(1));
    events.assertEmpty();

    //
    if (!primitive) {
      setter.invoke(o, (Object)null);
      assertFalse(node.hasNode(propertyName));
      events.assertPropertyChangedEvent(node.getUUID(), o, propertyName, null);
      events.assertEmpty();
    }
  }
}
