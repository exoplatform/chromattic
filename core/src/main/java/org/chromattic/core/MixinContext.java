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

import org.chromattic.core.jcr.info.MixinTypeInfo;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.mapper.MixinTypeMapper;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class MixinContext extends ObjectContext {

  /** The object instance. */
  final Object object;

  /** The related type. */
  final MixinTypeMapper mapper;

  /** The related entity if not null, otherwise it means that we are not attached to anything. */
  EntityContext relatedEntity;

  /** . */
  MixinTypeInfo typeInfo;

  /** . */
  final DomainSession session;

  MixinContext(MixinTypeMapper mapper, DomainSession session) {
    this.mapper = mapper;
    this.object = mapper.createObject(this);
    this.session = session;
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

  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    return mapper.invoke(this, method, args);
  }

  @Override
  public String toString() {
    return "MixinContext[mapper=" + mapper + ",related=" + relatedEntity + "]";
  }
}
