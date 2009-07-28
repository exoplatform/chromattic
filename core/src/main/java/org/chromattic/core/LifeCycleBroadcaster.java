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

import org.chromattic.api.LifeCycleListener;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class LifeCycleBroadcaster<T> {

  /** . */
  private final LifeCycleListener<T> listener;

  /** . */
//  private final Class<? extends T> type;

  @SuppressWarnings("unchecked")
  public LifeCycleBroadcaster(LifeCycleListener<T> listener) {

/*
    Class<?> type;
    Type resolvedType = TypeParameterResolver.resolve(listener.getClass(), LifeCycleListener.class, 0);
    if (resolvedType instanceof Class) {
      type = (Class<?>)resolvedType;
    } else {
      throw new IllegalArgumentException("Cannot handle type " + resolvedType);
    }
*/

    //
    this.listener = listener;
//    this.type = (Class<? extends T>)type;
  }

  void fireEvent(LifeCycleType eventType, Object o) {
/*
    if (type.isInstance(o)) {
      T t = type.cast(o);
      switch (eventType) {
        case CREATED:
          listener.created(t);
          break;
        case LOADED:
          listener.loaded(t);
          break;
        case PERSISTED:
          listener.persisted(t);
          break;
        case REMOVED:
          listener.removed(t);
          break;
      }
    }
*/
    switch (eventType) {
      case CREATED:
        listener.created((T)o);
        break;
      case LOADED:
        listener.loaded((T)o);
        break;
      case PERSISTED:
        listener.persisted((T)o);
        break;
      case REMOVED:
        listener.removed((T)o);
        break;
    }
  }
}
