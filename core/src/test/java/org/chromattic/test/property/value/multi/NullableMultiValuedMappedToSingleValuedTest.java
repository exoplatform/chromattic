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

package org.chromattic.test.property.value.multi;

import org.chromattic.test.support.MultiValue;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NullableMultiValuedMappedToSingleValuedTest extends AbstractMultiValuedTest {

  public NullableMultiValuedMappedToSingleValuedTest(ValueFactory factory, Object o, Node node, String propertyName, String getterName, String setterName, int propertyType, MultiValue objects) throws Exception {
    super(factory, o, node, propertyName, getterName, setterName, propertyType, objects);
  }

  public void run() throws Exception {
/*
    safeArrayEquals(values.sub(), MultiValue.create(getter.invoke(o)));

    //
    node.setProperty(propertyName, create(values.getObject(0)));
    safeArrayEquals(values.sub(0), MultiValue.create(getter.invoke(o)));
    safeValueEquals(values.getObject(0), node.getProperty(propertyName).getValue());

    //
    setter.invoke(o, values.sub(1).asNative());
    safeArrayEquals(values.sub(1), MultiValue.create(getter.invoke(o)));
    safeValueEquals(values.getObject(1), node.getProperty(propertyName).getValue());

    //
    setter.invoke(o, values.sub().asNative());
    safeArrayEquals(values.sub(), MultiValue.create(getter.invoke(o)));
    assertFalse(node.hasProperty(propertyName));

    //
    try {
      setter.invoke(o, values.sub(1, 2).asNative());
      fail();
    }
    catch (InvocationTargetException e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
    safeArrayEquals(values.sub(), MultiValue.create(getter.invoke(o)));
    assertFalse(node.hasProperty(propertyName));
*/
  }
}