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
package org.chromattic.api.query;

/**
 * The query builder allows to create queries.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface QueryBuilder<O> {

  /**
   * <p>Set the node type of the from clause of the query. It is possible to call that method only once and
   * any attempt to call this method a second time will throw an <tt>IllegalStateException</tt>.</p>
   *
   * @param fromClass the node type of the from clause
   * @param <O> the object type parameter
   * @return this builder
   * @throws NullPointerException if the argument is null
   * @throws IllegalStateException if the builder already has a from clause
   * @throws IllegalArgumentException if the from class cannot be mapped to a node type
   */
  <O> QueryBuilder<O> from(Class<O> fromClass) throws NullPointerException, IllegalStateException, IllegalArgumentException;

  /**
   * <p>Set the where clause of the query.</p>
   *
   * @param where the where clause
   * @param <O> the object type parameter
   * @return this builder
   * @throws NullPointerException if the argument is null
   */
  <O> QueryBuilder<O> where(String where) throws NullPointerException;

  /**
   * <p>Set the order by clause of the query.</p>
   *
   * @param orderBy the order by clause
   * @param <O> the object type parameter
   * @return this builder
   * @throws NullPointerException if the argument is null
   */
  <O> QueryBuilder<O> orderBy(String orderBy) throws NullPointerException;

  /**
   * <p>Compute and returns the <tt>ObjectQuery</tt> for this builder.</p>
   *
   * @return this object query
   * @throws IllegalStateException if the builder cannot build the query
   */
  Query<O> get() throws IllegalStateException;

}
