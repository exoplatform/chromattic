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

package org.chromattic.test.type;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.DomainSession;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(C.class);
  }

  public void testA() {
    // For now we just have this to check that the APT integration generates the correct code
  }

  public void testC() {
    DomainSession session = login();
    C c = session.create(C.class);
    assertNotNull(c);
    assertNull(c.value);
    c.m1("foo");
    assertEquals("foo", c.value);
  }
}