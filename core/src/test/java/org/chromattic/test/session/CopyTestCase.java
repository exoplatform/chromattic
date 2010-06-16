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

package org.chromattic.test.session;

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CopyTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
  }

  public void testCopyProperties() {
    ChromatticSession session = login();
    A a1 = session.insert(A.class, "a1");
    a1.setString("string_value");
    a1.setStrings(Arrays.asList("string_value_0", "string_value_1"));
    A a2 = session.copy(a1, "a2");
    assertNull(a2.getA());
    assertNull(a2.getB());
    assertEquals("string_value", a2.getString());
    assertEquals(Arrays.asList("string_value_0", "string_value_1"), a2.getStrings());
  }

  public void testCopyChildren() {
    ChromatticSession session = login();
    A a1 = session.insert(A.class, "a1");
    A a1_a = session.insert(a1, A.class, "a");
    a1_a.setString("string_value");
    a1_a.setStrings(Arrays.asList("string_value_0", "string_value_1"));
    A a2 = session.copy(a1, "a2");
    A a2_a = a2.getA();
    assertNull(a2.getString());
    assertEquals(Collections.<String>emptyList(), a2.getStrings());
    assertNull(a2.getB());
    assertNotNull(a2_a);
    assertEquals("string_value", a2_a.getString());
    assertEquals(Arrays.asList("string_value_0", "string_value_1"), a2_a.getStrings());
  }

  public void testCopyMixins() {
    ChromatticSession session = login();
    A a1 = session.insert(A.class, "a1");
    B a1b = session.create(B.class);
    a1.setB(a1b);
    A a2 = session.copy(a1, "a2");
    B a2b = a2.getB();
    assertNull(a2.getString());
    assertNull(a2.getA());
    assertEquals(Collections.<String>emptyList(), a2.getStrings());
    assertNotNull(a2b);
  }
}
