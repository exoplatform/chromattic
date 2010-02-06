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

import org.chromattic.api.query.Query;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.core.DomainSession;
import org.chromattic.core.mapper.NodeTypeKind;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.core.Domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryBuilderImpl<O> implements QueryBuilder<O> {

  /** . */
  private final String rootNodePath;

  /** . */
  private Class<O> fromClass;

  /** . */
  private String where;

  /** . */
  private ObjectMapper mapper;

  /** . */
  private DomainSession session;

  QueryBuilderImpl(DomainSession session, Class<O> fromClass, String rootNodePath) throws NullPointerException, IllegalArgumentException {
    if (session == null) {
      throw new NullPointerException("No null domain session accepted");
    }
    if (fromClass == null) {
      throw new NullPointerException("No null from class accepted");
    }
    if (rootNodePath == null) {
      throw new NullPointerException("No null root node path accepted");
    }

    //
    Domain domain = session.getDomain();
    ObjectMapper mapper = domain.getTypeMapper(fromClass);
    if (mapper == null) {
      throw new IllegalArgumentException("Class " + fromClass.getName() + " is not mapped");
    }
    if (mapper.getKind() != NodeTypeKind.PRIMARY) {
      throw new IllegalArgumentException("Class " + fromClass.getName() + " is mapped to a mixin type");
    }

    //
    this.fromClass = fromClass;
    this.mapper = mapper;
    this.where = null;
    this.session = session;
    this.rootNodePath = rootNodePath;
  }

  public QueryBuilder<O> where(String whereStatement) {
    if (whereStatement == null) {
      throw new NullPointerException();
    }
    this.where = whereStatement;
    return this;
  }

  public QueryBuilder<O> orderBy(String orderBy) throws NullPointerException {
    throw new UnsupportedOperationException("todo");
  }

  /** This is not the way I like to do things, but well for now it'll be fine. */
  private static final Pattern JCR_LIKE_PATH = Pattern.compile("jcr:path[\\s]*[^\\s]+[\\s]*'[^']*'");

  public Query<O> get() {
    if (fromClass == null) {
      throw new IllegalStateException();
    }

    //
    StringBuilder sb = new StringBuilder("SELECT * FROM ");

    //
    sb.append(mapper.getNodeTypeName());

    //
    if (where != null) {
      Matcher matcher = JCR_LIKE_PATH.matcher(where);
      if (!matcher.find()) {
        sb.append(" WHERE jcr:path LIKE '").append(rootNodePath).append("/%'");
        sb.append(" AND ");
        sb.append(where);
      } else {
        sb.append(" WHERE ");
        sb.append(where);
      }
    } else {
      sb.append(" WHERE jcr:path LIKE '").append(rootNodePath).append("/%'");
    }

    //
    return session.getDomain().getQueryManager().getObjectQuery(session, fromClass, sb.toString());
  }
}
