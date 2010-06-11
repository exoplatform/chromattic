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

package org.chromattic.exo;

import org.chromattic.spi.jcr.SessionLifeCycle;

import javax.jcr.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ExoSessionLifeCycle implements SessionLifeCycle {

  /** . */
  private static final Repository repo;

  static {
    try {
      RepositoryBootstrap bootstrap = new RepositoryBootstrap();
      bootstrap.bootstrap();
      repo = bootstrap.getRepository();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ExoSessionLifeCycle() throws Exception {
  }

  public Session login() throws RepositoryException {
    return repo.login(new SimpleCredentials("exo", "exo".toCharArray()));
  }

  public Session login(String workspace) throws RepositoryException {
    return repo.login(new SimpleCredentials("exo", "exo".toCharArray()), workspace);
  }

  public Session login(Credentials credentials, String workspace) throws RepositoryException {
    return repo.login(credentials, workspace);
  }

  public Session login(Credentials credentials) throws RepositoryException {
    return repo.login(credentials);
  }

  public void save(Session session) throws RepositoryException {
    session.save();
  }

  public void close(Session session) {
    session.logout();
  }
}
