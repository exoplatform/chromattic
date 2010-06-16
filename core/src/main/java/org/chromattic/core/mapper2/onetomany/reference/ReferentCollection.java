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

package org.chromattic.core.mapper2.onetomany.reference;

import org.chromattic.core.EntityContext;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReferentCollection extends AbstractCollection<Object> {

  /** . */
  private final EntityContext context;

  /** . */
  private final JCRReferentCollectionPropertyMapper mapper;

  public ReferentCollection(EntityContext context, JCRReferentCollectionPropertyMapper mapper) {
    this.context = context;
    this.mapper = mapper;
  }

  @Override
  public boolean add(Object o) {
    EntityContext referentCtx = context.getSession().unwrapEntity(o);
    return context.addReference(mapper.propertyName, referentCtx, mapper.linkType);
  }

  public Iterator<Object> iterator() {
    Class<?> filterClass = mapper.getRelatedClass();
    return (Iterator<Object>)context.getReferents(mapper.propertyName, filterClass, mapper.linkType);
  }

  public int size() {
    int size = 0;
    Iterator<Object> iterator = iterator();
    while (iterator.hasNext()) {
      iterator.next();
      size++;
    }
    return size;
  }
}