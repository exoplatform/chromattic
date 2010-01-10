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

package org.chromattic.core.builder;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.core.DomainSessionImpl;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.core.jcr.SessionWrapperImpl;
import org.chromattic.spi.jcr.SessionLifeCycle;
import org.chromattic.core.Domain;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Chromattic;
import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Credentials;
import javax.jcr.Session;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticImpl implements Chromattic {

  /** . */
  private SessionLifeCycle sessionLifeCycle;

  /** . */
  private Domain domain;

  ChromatticImpl(Domain domain, SessionLifeCycle sessionLifeCycle) {
    this.domain = domain;
    this.sessionLifeCycle = sessionLifeCycle;
  }

  public ChromatticSession openSession() {
    try {
      Session session = sessionLifeCycle.login();
      SessionWrapper wrapper = new SessionWrapperImpl(sessionLifeCycle, session, domain.isHasPropertyOptimized(), domain.isHasNodeOptimized());
      return new ChromatticSessionImpl(new DomainSessionImpl(domain, wrapper));
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ChromatticSession openSession(String workspace) {
    try {
      Session session = sessionLifeCycle.login(workspace);
      SessionWrapper wrapper = new SessionWrapperImpl(sessionLifeCycle, session, domain.isHasPropertyOptimized(), domain.isHasNodeOptimized());
      return new ChromatticSessionImpl(new DomainSessionImpl(domain, wrapper));
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ChromatticSession openSession(Credentials credentials, String workspace) {
    try {
      Session session = sessionLifeCycle.login(credentials, workspace);
      SessionWrapper wrapper = new SessionWrapperImpl(sessionLifeCycle, session, domain.isHasPropertyOptimized(), domain.isHasNodeOptimized());
      return new ChromatticSessionImpl(new DomainSessionImpl(domain, wrapper));
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ChromatticSession openSession(Credentials credentials) {
    try {
      Session session = sessionLifeCycle.login(credentials);
      SessionWrapper wrapper = new SessionWrapperImpl(sessionLifeCycle, session, domain.isHasPropertyOptimized(), domain.isHasNodeOptimized());
      return new ChromatticSessionImpl(new DomainSessionImpl(domain, wrapper));
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }
}