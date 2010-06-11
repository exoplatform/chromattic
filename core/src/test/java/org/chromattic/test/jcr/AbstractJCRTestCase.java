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

package org.chromattic.test.jcr;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.common.JCR;
import org.chromattic.core.api.ChromatticImpl;
import org.chromattic.spi.jcr.SessionLifeCycle;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractJCRTestCase extends TestCase {

  /** . */
  protected SessionLifeCycle sessionLF;

  /** . */
  private Session session;

  @Override
  protected void setUp() throws Exception {
    ChromatticBuilder builder = ChromatticBuilder.create();
    ChromatticImpl chromattic = (ChromatticImpl)builder.build();
    sessionLF = chromattic.getSessionLifeCycle();
  }

  @Override
  protected void tearDown() throws Exception {
    if (session != null && session.isLive()) {
      session.logout();
    }
    sessionLF = null;
  }

  protected final void assertEquals(Node expected, Node actual) {
    try {
      assertTrue("Was expected to have node " + actual + " equals to " + expected, JCR.equals(actual, expected));
    } catch (RepositoryException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
  }

  private static final Comparator<Node> comparator = new Comparator<Node>() {
    public int compare(Node a, Node b) {
      boolean equals;
      try {
        equals = JCR.equals(a, b);
      }
      catch (RepositoryException e) {
        AssertionFailedError afe = new AssertionFailedError();
        afe.initCause(e);
        throw afe;
      }
      if (equals) {
        return 0;
      } else {
        return System.identityHashCode(a) - System.identityHashCode(b);
      }
    }
  };

  protected final void assertEquals(Set<Node> expected, Set<Node> actual) {
    TreeSet<Node> expectedSet = new TreeSet<Node>(comparator);
    expectedSet.addAll(expected);
    TreeSet<Node> actualSet = new TreeSet<Node>(comparator);
    actualSet.addAll(actual);
    assertTrue("Was expected to have " + actual + " equals to " + expected, expectedSet.equals(actualSet)); 
  }

  protected void logout() {
    if (session == null) {
      throw new IllegalStateException();
    }
    Session session = this.session;
    this.session = null;
    session.logout();
  }

  protected Session login() {
    if (session != null) {
      session.logout();
      session = null;
    }
    try {
      return sessionLF.login();
    }
    catch (RepositoryException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
  }
}
