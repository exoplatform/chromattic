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

import org.chromattic.api.Status;
import org.chromattic.core.jcr.type.NodeTypeInfo;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.spi.instrument.ProxyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class EmbeddedContext extends ObjectContext<EmbeddedContext> {

  /** The object instance. */
  final Object object;

  /** The related type. */
  final ObjectMapper<EmbeddedContext> mapper;

  /** The related entity if not null, otherwise it means that we are not attached to anything. */
  EntityContext relatedEntity;

  /** The related type info. */
  NodeTypeInfo typeInfo;

  /** . */
  final DomainSession session;

  EmbeddedContext(ObjectMapper<EmbeddedContext> mapper, DomainSession session) {

    // Create our proxy
    ProxyType pt = session.domain.getProxyType(mapper.getObjectClass());
    Object object = pt.createProxy(this);

    //
    this.mapper = mapper;
    this.object = object;
    this.session = session;
  }

  /**
   * Returns the status of the related entity when the context is attached to an entity otherwise it returns
   * the {@link Status#TRANSIENT} value.
   *
   * @return the status
   */
  @Override
  public Status getStatus() {
    if (relatedEntity == null) {
      return Status.TRANSIENT;
    } else {
      return relatedEntity.getStatus();
    }
  }

  @Override
  public ObjectMapper<EmbeddedContext> getMapper() {
    return mapper;
  }

  public DomainSession getSession() {
    return session;
  }

  @Override
  public NodeTypeInfo getTypeInfo() {
    if (typeInfo == null) {
      throw new IllegalStateException();
    }
    return typeInfo;
  }

  @Override
  public Object getObject() {
    return object;
  }

  @Override
  public EntityContext getEntity() {
    return relatedEntity;
  }

  @Override
  public String toString() {
    return "EmbeddedContext[mapper=" + mapper + ",related=" + relatedEntity + "]";
  }
}
