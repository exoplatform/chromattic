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

import org.chromattic.api.query.ObjectQueryBuilder;
import org.chromattic.api.query.ObjectQuery;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.Domain;

import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectQueryBuilderImpl implements ObjectQueryBuilder {

  /** . */
  private Class<?> fromClass;

  /** . */
  private String where;

  /** . */
  private TypeMapper mapper;

  /** . */
  private QueryManager manager;

  /** . */
  private Domain domain;

  public ObjectQueryBuilderImpl(Domain domain, QueryManager manager) {
    this.manager = manager;
    this.fromClass = null;
    this.where = null;
    this.domain = domain;
  }

  public ObjectQueryBuilder from(Class fromClass) {
    if (fromClass == null) {
      throw new NullPointerException();
    }
    if (this.fromClass != null) {
      throw new IllegalStateException();
    }
    TypeMapper mapper = domain.getTypeMapper(fromClass);
    if (mapper == null) {
      throw new IllegalArgumentException("Class " + fromClass.getName() + " is not mapped");
    }
    this.mapper = mapper;
    this.fromClass = fromClass;
    return this;
  }

  public ObjectQueryBuilder where(String whereStatement) {
    if (whereStatement == null) {
      throw new NullPointerException();
    }
    this.where = whereStatement;
    return this;
  }

  public ObjectQuery get() {
    if (fromClass == null) {
      throw new IllegalStateException();
    }

    //
    StringBuffer sb = new StringBuffer("SELECT * FROM ");
    sb.append(mapper.getNodeDef().getPrimaryNodeTypeName());
    if (where != null) {
      sb.append(" WHERE ");
      sb.append(where);
    }

    //
    return manager.getObjectQuery(mapper.getObjectClass(), sb.toString());
  }

  public Iterator iterator() {
    return get().iterator();
  }
}
