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
import org.chromattic.core.api.ChromatticImpl;
import org.chromattic.spi.jcr.SessionLifeCycle;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

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
    builder.setOptionValue(ChromatticBuilder.USE_SYSTEM_PROPERTIES, true);
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
