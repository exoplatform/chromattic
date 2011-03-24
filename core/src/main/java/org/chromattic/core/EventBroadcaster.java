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

import org.chromattic.api.event.LifeCycleListener;
import org.chromattic.api.event.EventListener;
import org.chromattic.api.event.StateChangeListener;
import org.chromattic.common.CloneableInputStream;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class EventBroadcaster implements EventListener {

  /** . */
  private List<LifeCycleListener> lifeCycleListeners;

  /** . */
  private List<StateChangeListener> stateChangeListeners;

  @SuppressWarnings("unchecked")
  public EventBroadcaster() {
    this.lifeCycleListeners = null;
  }

  public void addLifeCycleListener(EventListener listener) throws NullPointerException {
    if (listener == null) {
      throw new NullPointerException();
    }
    if (listener instanceof LifeCycleListener) {
      LifeCycleListener lifeCycleListener = (LifeCycleListener)listener;
      if (lifeCycleListeners == null  || !lifeCycleListeners.contains(lifeCycleListener)) {
        if (lifeCycleListeners == null) {
          lifeCycleListeners = new ArrayList<LifeCycleListener>();
        }
        lifeCycleListeners.add(lifeCycleListener);
      }
    }
    if (listener instanceof StateChangeListener) {
      StateChangeListener stateChangeListener = (StateChangeListener)listener;
      if (stateChangeListeners == null || !stateChangeListeners.contains(stateChangeListener)) {
        if (stateChangeListeners == null) {
          stateChangeListeners = new ArrayList<StateChangeListener>();
        }
        stateChangeListeners.add(stateChangeListener);
      }
    }
  }

  public boolean hasLifeCycleListeners() {
    return lifeCycleListeners != null;
  }

  public boolean hasStateChangeListeners() {
    return stateChangeListeners != null;
  }

  public void created(Object o) {
    if (lifeCycleListeners == null) {
      return;
    }
    for (EventListener listener : lifeCycleListeners) {
      try {
        ((LifeCycleListener)listener).created(o);
      }
      catch (Exception ignore) {
      }
    }
  }

  public void loaded(EntityContext ctx, Object o) {
    if (lifeCycleListeners == null) {
      return;
    }
    String id = ctx.getId();
    String path = ctx.getPath();
    String name = ctx.getLocalName();
    for (EventListener listener : lifeCycleListeners) {
      try {
        ((LifeCycleListener)listener).loaded(id, path, name, o);
      }
      catch (Exception ignore) {
      }
    }
  }

  public void added(EntityContext ctx, Object o) {
    if (lifeCycleListeners == null) {
      return;
    }
    String id = ctx.getId();
    String path = ctx.getPath();
    String name = ctx.getLocalName();
    for (EventListener listener : lifeCycleListeners) {
      try {
        ((LifeCycleListener)listener).added(id, path, name, o);
      }
      catch (Exception ignore) {
      }
    }
  }

  public void removed(String id, String path, String name, Object o) {
    if (lifeCycleListeners == null) {
      return;
    }
    for (EventListener listener : lifeCycleListeners) {
      try {
        ((LifeCycleListener)listener).removed(id, path, name, o);
      }
      catch (Exception ignore) {
      }
    }
  }

  public void propertyChanged(String id, Object o, String propertyName, Object propertyValue) {
    if (stateChangeListeners == null) {
      return;
    }
    for (EventListener listener : stateChangeListeners) {
      try {
        if (propertyValue instanceof CloneableInputStream) {
          ((StateChangeListener)listener).propertyChanged(id, o, propertyName, ((CloneableInputStream)propertyValue).clone());
        } else {
          ((StateChangeListener)listener).propertyChanged(id, o, propertyName, propertyValue);
        }
      }
      catch (Exception ignore) {
      }
    }
  }
}
