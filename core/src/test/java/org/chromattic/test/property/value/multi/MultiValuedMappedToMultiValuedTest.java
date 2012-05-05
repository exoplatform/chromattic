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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.support.MultiValue;

import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MultiValuedMappedToMultiValuedTest extends AbstractMultiValuedTest {

  public MultiValuedMappedToMultiValuedTest(ValueFactory factory, Object o, Node node, String propertyName, String getterName, String setterName, int propertyType, MultiValue objects) throws Exception {
    super(factory, o, node, propertyName, getterName, setterName, propertyType, objects);
  }

  public void run() throws Exception {
    safeArrayEquals(values.sub(), MultiValue.safeCreate(getter.invoke(o)));

    //
    if (AbstractTestCase.getCurrentConfig().isStateCacheDisabled()) {
      node.setProperty(propertyName, new Value[]{create(values.getObject(0))});
    } else {
      setter.invoke(o, values.sub(0).asNative());
    }
    safeArrayEquals(values.sub(0), MultiValue.create(getter.invoke(o)));
    safeArrayEquals(values.sub(0), node.getProperty(propertyName).getValues());

    //
    setter.invoke(o, values.sub(1, 2).asNative());
    safeArrayEquals(values.sub(1, 2), MultiValue.create(getter.invoke(o)));
    safeArrayEquals(values.sub(1, 2), node.getProperty(propertyName).getValues());

    //
    setter.invoke(o, (Object)null);
/*
    try {
      fail();
    }
    catch (InvocationTargetException e) {
      assertTrue(e.getCause() instanceof NullPointerException);
    }
*/
    assertEquals(0, MultiValue.create(getter.invoke(o)).size());
    assertFalse(node.hasProperty(propertyName));
  }
}
