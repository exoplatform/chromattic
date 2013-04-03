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

import org.chromattic.api.query.QueryResult;
import org.chromattic.common.collection.AbstractFilterIterator;
import org.chromattic.common.JCR;
import org.chromattic.core.DomainSession;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryResultImpl<O> extends AbstractFilterIterator<O, Node> implements QueryResult<O> {

  /** . */
  private final Class<O> clazz;

  /** . */
  private final NodeIterator iterator;

  /** . */
  private final DomainSession session;

  QueryResultImpl(DomainSession session, NodeIterator iterator, Class<O> clazz) throws NullPointerException {
    super(JCR.adapt(iterator));

    //
    this.session = session;
    this.iterator = iterator;
    this.clazz = clazz;
  }

  protected O adapt(Node internal) {
    Object o = session.findByNode(Object.class, internal);
    if (clazz.isInstance(o)) {
      return clazz.cast(o);
    }
    else {
      return null;
    }
  }

  public int size() {
    return (int)iterator.getSize();
  }
}