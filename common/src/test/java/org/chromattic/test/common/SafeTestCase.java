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
package org.chromattic.test.common;

import junit.framework.TestCase;
import org.chromattic.common.Safe;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SafeTestCase extends TestCase {

  public void testEquals() {
    Object o1 = new Object();
    Object o2 = new Object();
    assertTrue(Safe.equals(null, null));
    assertTrue(Safe.equals(o1, o1));
    assertTrue(Safe.equals(o2, o2));
    assertFalse(Safe.equals(o1, null));
    assertFalse(Safe.equals(null, o2));
    assertFalse(Safe.equals(o1, o2));
  }

  public void testSafeHashCode() {
    Object o = new Object();
    assertEquals(0, Safe.hashCode(null));
    assertEquals(System.identityHashCode(o), Safe.hashCode(o));
  }
}
