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

import org.chromattic.api.ChromatticIOException;
import org.chromattic.common.CloneableInputStream;
import org.chromattic.common.jcr.Path;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.spi.instrument.MethodHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ObjectContext implements MethodHandler {

  public abstract Object getObject();

  public abstract EntityContext getEntity();

  public abstract NodeTypeInfo getTypeInfo();

  public final <V> V getPropertyValue(String propertyName, SimpleValueInfo<V> type) {
    EntityContext ctx = getEntity();
    EntityContextState state = ctx.state;

    //
    propertyName = state.getSession().domain.encodeName(ctx, propertyName, NameKind.PROPERTY);
    Path.validateName(propertyName);

    //
    NodeTypeInfo typeInfo = getTypeInfo();
    return state.getPropertyValue(typeInfo, propertyName, type);
  }

  public final <V> List<V> getPropertyValues(String propertyName, SimpleValueInfo<V> simpleType, ListType listType) {
    EntityContext ctx = getEntity();
    EntityContextState state = ctx.state;

    //
    propertyName = state.getSession().domain.encodeName(ctx, propertyName, NameKind.PROPERTY);
    Path.validateName(propertyName);

    //
    NodeTypeInfo typeInfo = getTypeInfo();
    return state.getPropertyValues(typeInfo, propertyName, simpleType, listType);
  }

  public final <V> void setPropertyValue(String propertyName, SimpleValueInfo<V> type, V o) {
    EntityContext ctx = getEntity();
    EntityContextState state = ctx.state;

    //
    propertyName = state.getSession().domain.encodeName(ctx, propertyName, NameKind.PROPERTY);
    Path.validateName(propertyName);

    //
    Object object = getObject();

    //
    EventBroadcaster broadcaster = state.getSession().broadcaster;

    //
    NodeTypeInfo typeInfo = getTypeInfo();

    //
    if (o instanceof InputStream && broadcaster.hasStateChangeListeners()) {
      CloneableInputStream in;
      try {
        in = new CloneableInputStream((InputStream)o);
      }
      catch (IOException e) {
        throw new ChromatticIOException("Could not read stream", e);
      }
      @SuppressWarnings("unchecked") V v = (V)in;
      state.setPropertyValue(typeInfo, propertyName, type, v);
      broadcaster.propertyChanged(state.getId(), object, propertyName, in.clone());
    } else {
      state.setPropertyValue(typeInfo, propertyName, type, o);
      broadcaster.propertyChanged(state.getId(), object, propertyName, o);
    }
  }

  public final <V> void setPropertyValues(String propertyName, SimpleValueInfo<V> type, ListType listType, List<V> objects) {
    EntityContext ctx = getEntity();
    EntityContextState state = ctx.state;

    //
    propertyName = state.getSession().domain.encodeName(ctx, propertyName, NameKind.PROPERTY);
    Path.validateName(propertyName);

    //
    NodeTypeInfo typeInfo = getTypeInfo();

    //
    state.setPropertyValues(typeInfo, propertyName, type, listType, objects);
  }
}
