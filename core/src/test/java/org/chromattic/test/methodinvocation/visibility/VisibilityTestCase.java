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

package org.chromattic.test.methodinvocation.visibility;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class VisibilityTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testProtectedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    assertNull(a.getProtectedProperty());
    a.setProtectedProperty("foo");
    assertEquals("foo", a.getProtectedProperty());
  }

  public void testPackageProtectedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    assertNull(a.getPackageProtectedProperty());
    a.setPackageProtectedProperty("bar");
    assertEquals("bar", a.getPackageProtectedProperty());
  }
}