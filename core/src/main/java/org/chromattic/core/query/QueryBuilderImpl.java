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
import org.chromattic.core.mapper.PrimaryNodeTypeMapper;
import org.chromattic.core.mapper.NodeTypeMapper;
import org.chromattic.core.Domain;
import org.chromattic.core.DomainSession;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryBuilderImpl implements QueryBuilder {

  /** . */
  private Class<?> fromClass;

  /** . */
  private String where;

  /** . */
  private PrimaryNodeTypeMapper mapper;

  /** . */
  private DomainSession session;

  QueryBuilderImpl(DomainSession session) {
    this.fromClass = null;
    this.where = null;
    this.session = session;
  }

  public QueryBuilder from(Class fromClass) {
    if (fromClass == null) {
      throw new NullPointerException();
    }
    if (this.fromClass != null) {
      throw new IllegalStateException();
    }

    //
    Domain domain = session.getDomain();
    NodeTypeMapper mapper = domain.getTypeMapper(fromClass);
    if (mapper == null) {
      throw new IllegalArgumentException("Class " + fromClass.getName() + " is not mapped");
    }
    if (!(mapper instanceof PrimaryNodeTypeMapper)) {
      throw new IllegalArgumentException("Class " + fromClass.getName() + " is mapped to a mixin type");
    }

    //
    this.mapper = (PrimaryNodeTypeMapper)mapper;
    this.fromClass = fromClass;
    return this;
  }

  public QueryBuilder where(String whereStatement) {
    if (whereStatement == null) {
      throw new NullPointerException();
    }
    this.where = whereStatement;
    return this;
  }

  public QueryBuilder orderBy(String orderBy) throws NullPointerException {
    throw new UnsupportedOperationException("todo");
  }

  public Query get() {
    if (fromClass == null) {
      throw new IllegalStateException();
    }

    //
    StringBuffer sb = new StringBuffer("SELECT * FROM ");
    sb.append(mapper.getNodeTypeName());
    if (where != null) {
      sb.append(" WHERE ");
      sb.append(where);
    }

    //
    return session.getDomain().getQueryManager().getObjectQuery(session, mapper.getObjectClass(), sb.toString());
  }
}
