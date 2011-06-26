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

package org.chromattic.test.find.id;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.find.A;
import org.chromattic.api.ChromatticSession;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class FindByIdTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testMethodFind() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String id = session.getId(a);
    A b = a.findById(id);
    assertSame(a, b);
  }

  public void testSessionFind() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String id = session.getId(a);
    A b = session.findById(A.class, id);
    assertSame(a, b);
  }

  public void testCCE() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String id = session.getId(a);
    try {
      session.findById(String.class, id);
      fail();
    }
    catch (ClassCastException e) {
    }
  }

  public void testNotFound() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    assertNotNull(a);
    String id = session.getId(a);
    session.remove(a);
    a = session.findById(A.class, id);
    assertNull(a);
  }

  public void testNPE() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String id = session.getId(a);
    try {
      session.findById(A.class, null);
      fail();
    }
    catch (NullPointerException e) {
    }
    try {
      session.findById(null, id);
      fail();
    }
    catch (NullPointerException e) {
    }
  }
}
