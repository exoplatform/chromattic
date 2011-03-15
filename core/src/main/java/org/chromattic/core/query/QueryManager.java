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

import org.chromattic.api.ChromatticException;
import org.chromattic.api.query.Query;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.DomainSession;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryManager {

  /** . */
  private final String rootNodePath;

  public QueryManager(String rootNodePath) {
    this.rootNodePath = rootNodePath;
  }

  public <O> QueryBuilder<O> createQueryBuilder(DomainSession session, Class<O> fromClass) throws ChromatticException {
    return new QueryBuilderImpl<O>(session, fromClass, rootNodePath);
  }

  /**
   * Create a query.
   *
   * @param session the current session
   * @param objectClass the expected object class
   * @param statement the query statement
   * @param offset the offset
   * @param limit the limit
   * @param <O> the object generic type
   * @return the query
   * @throws IllegalArgumentException when limit or offset are negative numbers
   */
  public <O> Query<O> getObjectQuery(DomainSession session, Class<O> objectClass, String statement, Long offset, Long limit) throws IllegalArgumentException {
    if (offset != null && offset < 0)
    {
      throw new IllegalArgumentException();
    }
    if (offset != null && offset < 0)
    {
      throw new IllegalArgumentException();
    }

    //
    try {
      // For now we support on SQL
      javax.jcr.query.Query jcrQuery = session.getSessionWrapper().createQuery(statement, offset, limit);
      Query<?> query = new QueryImpl<O>(session, objectClass, jcrQuery);

      //
      @SuppressWarnings("unchecked") Query<O> ret = (Query<O>)query;
      return ret;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }
}
