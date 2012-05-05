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

package org.chromattic.test.type.annotated;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Value;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnnotatedTypeTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(PortletWindow.class);
  }

  public void testMapping() throws Exception {
    ChromatticSessionImpl session = login();
    PortletWindow a = session.insert(PortletWindow.class, "a");
    Node node = session.getNode(a);
    assertFalse(node.hasProperty("foo"));
    assertEquals(null, a.getMode());
    a.setMode(new PortletMode("view"));
    assertEquals("view", a.getMode().getName());
    assertTrue(node.hasProperty("foo"));
    Property bytes = node.getProperty("foo");
    assertEquals(PropertyType.STRING, bytes.getType());
    assertEquals("view", node.getProperty("foo").getString());
    node.setProperty("foo", (Value)null);
    if (getProfile().isStateCacheDisabled()) {
      assertEquals(null, a.getMode());
    } else {
      assertEquals("view", a.getMode().getName());
    }
  }
}
