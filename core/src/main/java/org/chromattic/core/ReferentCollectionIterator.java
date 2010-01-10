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

package org.chromattic.core;

import org.chromattic.common.AbstractFilterIterator;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
final class ReferentCollectionIterator<T> extends AbstractFilterIterator<T, Node> {

  /** . */
  private final Class<T> relatedClass;

  /** . */
  private final ChromatticSessionImpl session;

  /** . */
  private final String propertyName;

  public ReferentCollectionIterator(
    ChromatticSessionImpl session,
    Iterator<Node> iterator,
    Class<T> relatedClass,
    String propertyName) throws RepositoryException {
    super(iterator);

    //
    this.session = session;
    this.relatedClass = relatedClass;
    this.propertyName = propertyName;
  }

  Node previous;

  protected T adapt(Node node) {
    Object o = session.findByNode(Object.class, node);
    if (relatedClass.isInstance(o)) {
      previous = node;
      return relatedClass.cast(o);
    } else {
      return null;
    }
  }
}