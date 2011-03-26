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
package org.chromattic.test.type.stringenum;

import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class StringEnumTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(A.class);
  }

  public void testMapping() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    assertEquals(null, a.getCurrency());
    a.setCurrency(Currency.EURO);
    assertEquals(Currency.EURO, a.getCurrency());
    Node node = session.getNode(a);
    assertEquals("EURO", node.getProperty("currency").getString());
    a.setCurrency(null);
    assertFalse(node.hasProperty("currency"));
  }

  public void testIllegalValue() throws Exception {
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    Node node = session.getNode(a);
    node.setProperty("currency", "bilto");
    try {
      a.getCurrency();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
  }
}
