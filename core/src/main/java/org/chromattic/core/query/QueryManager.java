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
import org.chromattic.api.query.QueryLanguage;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.DomainSession;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryManager {

  /** . */
  private static EnumMap<QueryLanguage, String> languages = new EnumMap<QueryLanguage, String>(QueryLanguage.class);

  static {
    languages.put(QueryLanguage.SQL, javax.jcr.query.Query.SQL);
    languages.put(QueryLanguage.XPATH, javax.jcr.query.Query.XPATH);
  }

  /** . */
  private EnumMap<QueryLanguage, Map<QueryKey, Query<?>>> globalQueryCache;

  /** . */
  private final String rootNodePath;

  public QueryManager(String rootNodePath) {
    this.rootNodePath = rootNodePath;
  }

  public <O> QueryBuilder<O> createQueryBuilder(DomainSession session, Class<O> fromClass) throws ChromatticException {
    return new QueryBuilderImpl<O>(session, fromClass, rootNodePath);
  }

  public <O> Query<O> getObjectQuery(DomainSession session, Class<O> objectClass, String statement) {
    try {
      // For now we support on SQL
      QueryLanguage language = QueryLanguage.SQL;

      QueryKey key = new QueryKey(objectClass, statement);

      Query<?> query = null;
      if (globalQueryCache != null) {
        Map<QueryKey, Query<?>> queryCache = globalQueryCache.get(language);
        if (queryCache != null) {
          query = queryCache.get(key);
        }
      }

      //
      if (query == null) {
        Session jcrSession = session.getJCRSession();
        javax.jcr.query.QueryManager queryMgr = jcrSession.getWorkspace().getQueryManager();
        javax.jcr.query.Query jcrQuery = queryMgr.createQuery(statement, javax.jcr.query.Query.SQL);

        //
        query = new QueryImpl<O>(session, objectClass, jcrQuery);

        //
        Map<QueryKey, Query<?>> queryCache;
        if (globalQueryCache == null) {
          globalQueryCache = new EnumMap<QueryLanguage, Map<QueryKey, Query<?>>>(QueryLanguage.class);
          queryCache = new HashMap<QueryKey, Query<?>>();
          globalQueryCache.put(language, queryCache);
        } else {
          queryCache = globalQueryCache.get(language);
          if (queryCache == null) {
            globalQueryCache.put(language, queryCache);
          }
        }
        queryCache.put(key, query);
      }

      //
      @SuppressWarnings("unchecked") Query<O> ret = (Query<O>)query;
      return ret;
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }
}
