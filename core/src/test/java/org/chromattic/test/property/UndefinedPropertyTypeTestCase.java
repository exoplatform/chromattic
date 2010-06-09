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

import org.chromattic.api.NoSuchPropertyException;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.api.ChromatticSessionImpl;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class UndefinedPropertyTypeTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_Undefined.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testUndefinedType() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node node = rootNode.addNode("tp_undefined", getNodeTypeName(TP_Undefined.class));
    TP_Undefined o = session.findByNode(TP_Undefined.class, node);
    assertNull(o.getUndefinedType());
    o.setUndefinedType("foo");
    assertEquals("foo", o.getUndefinedType());
    assertEquals(PropertyType.STRING, node.getProperty("undefined_type").getType());
    assertEquals("foo", node.getProperty("undefined_type").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("undefined_type", (String)null);
      assertEquals(null, o.getUndefinedType());
    }
  }

  public void testUndefinedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    TP_Undefined undef = session.insert(TP_Undefined.class, "a");
    try {
      undef.getUndefinedProperty();
      fail();
    }
    catch (NoSuchPropertyException expected) {
    }
    try {
      undef.setUndefinedProperty("foo");
      fail();
    }
    catch (NoSuchPropertyException ignore) {
    }
  }

  public void testUndefinedMultivaluedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    TP_Undefined undef = session.insert(TP_Undefined.class, "a");
    try {
      undef.getUndefinedMultivaluedProperty();
      fail();
    }
    catch (NoSuchPropertyException expected) {
    }
    try {
      undef.setUndefinedMultivaluedProperty(Collections.singletonList("foo"));
      fail();
    }
    catch (NoSuchPropertyException ignore) {
    }
  }
}
