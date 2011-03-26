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
package org.chromattic.core.query;

import org.chromattic.api.query.ObjectQuery;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.DomainSession;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectQueryImpl<O> implements ObjectQuery<O> {

  /** . */
  private final javax.jcr.query.Query jcrQuery;

  /** . */
  private final DomainSession domainSession;

  /** . */
  private final Class<O> clazz;

  public ObjectQueryImpl(Class<O> clazz, DomainSession domainSession, String jcrStatement) throws RepositoryException {

    //
    Session jcrSession = domainSession.getJCRSession();
    javax.jcr.query.QueryManager queryMgr = jcrSession.getWorkspace().getQueryManager();
    javax.jcr.query.Query jcrQuery = queryMgr.createQuery(jcrStatement, javax.jcr.query.Query.SQL);

    //
    this.clazz = clazz;
    this.domainSession = domainSession;
    this.jcrQuery = jcrQuery;
  }

  public org.chromattic.api.query.QueryResult<O> iterator() {
    final NodeIterator iterator;
    try {
      QueryResult result = jcrQuery.execute();
      iterator = result.getNodes();
      return new QueryResultImpl<O>(domainSession, iterator, clazz);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }
}
