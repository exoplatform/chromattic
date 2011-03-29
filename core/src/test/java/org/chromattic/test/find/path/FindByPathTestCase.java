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

package org.chromattic.test.find.path;

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
public class FindByPathTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testSessionRelativeFind() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    A b = session.findByPath(A.class, "a", false);
    assertSame(a, b);
  }

  public void testSessionAbsoluteFind() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String path = session.getPath(a);
    A b = session.findByPath(A.class, path, true);
    assertSame(a, b);
  }

  public void testCCERelative() throws Exception {
    ChromatticSession session = login();
    session.insert(A.class, "a");
    try {
      session.findByPath(String.class, "a", false);
      fail();
    }
    catch (ClassCastException e) {
    }
  }

  public void testCCEAbsolute() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    String path = session.getPath(a);
    try {
      session.findByPath(String.class, path, true);
      fail();
    }
    catch (ClassCastException e) {
    }
  }

  public void testRelativeNotFound() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.findByPath(A.class, "foo", false);
    assertNull(a);
  }

  public void testAbsoluteNotFound() throws RepositoryException {
    ChromatticSession session = login();
    A a = session.findByPath(A.class, "/foo", true);
    assertNull(a);
  }

  public void testRelativeNPE() throws Exception {
    ChromatticSession session = login();
    session.insert(A.class, "a");
    try {
      session.findByPath(A.class, null, false);
      fail();
    }
    catch (NullPointerException e) {
    }
    try {
      session.findByPath(null, "a", false);
      fail();
    }
    catch (NullPointerException e) {
    }
  }

  public void testAbsoluteNPE() throws Exception {
    ChromatticSession session = login();
    session.insert(A.class, "a");
    try {
      session.findByPath(A.class, null, true);
      fail();
    }
    catch (NullPointerException e) {
    }
    try {
      session.findByPath(null, "a", true);
      fail();
    }
    catch (NullPointerException e) {
    }
  }

  public void testFindWithOrigin() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    A b = session.create(A.class);
    a.setChild(b);
    assertSame(b, session.findByPath(a, A.class, "child"));
  }

  public void testFindWithNullOrigin() throws Exception {
    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    assertSame(a, session.findByPath(null, A.class, "a"));
  }

  public void testFindWithNonInstrumentedOrigin() throws Exception {
    ChromatticSession session = login();
    session.insert(A.class, "a");
    try {
      session.findByPath(new Object(), A.class, "a");
      fail();
    }
    catch (IllegalArgumentException ignore) {
    }
  }
}