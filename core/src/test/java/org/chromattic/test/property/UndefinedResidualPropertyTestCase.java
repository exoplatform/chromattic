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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class UndefinedResidualPropertyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TP_UndefinedResidual.class);
  }

  /** . */
  private TP_UndefinedResidual o;

  /** . */
  private Node node;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    //
    DomainSession session = login();
    Node rootNode = session.getJCRSession().getRootNode();
    node = rootNode.addNode("tp_undefinedresidual", "tp_d");
    o = session.findByNode(TP_UndefinedResidual.class, node);
  }

  public void testString() throws Exception {
    assertNull(o.getString());
    o.setString("foo");
    assertEquals("foo", o.getString());
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, o.getString());
    }
  }

  public void testMap() throws Exception {
    assertNull(o.getString());
    Map<String, Object> map = o.getProperties();
    map.put("string_property", "foo");
    assertEquals("foo", map.get("string_property"));
    assertEquals(PropertyType.STRING, node.getProperty("string_property").getType());
    assertEquals("foo", node.getProperty("string_property").getString());
    if (getConfig().isStateCacheDisabled()) {
      node.setProperty("string_property", (String)null);
      assertEquals(null, map.get("string_property"));
    }
  }
}
