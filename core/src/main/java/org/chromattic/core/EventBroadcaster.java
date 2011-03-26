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

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class EventBroadcaster implements EventListener {

  /** . */
  private List<EventListener> listeners;

  @SuppressWarnings("unchecked")
  public EventBroadcaster() {
    this.listeners = null;
  }

  public final void addLifeCycleListener(EventListener listener) {
    if (listener == null) {
      throw new NullPointerException();
    }
    if (listeners == null) {
      listeners = new ArrayList<EventListener>();
    }
    listeners.add(listener);
  }

  public void created(Object o) {
    if (listeners == null) {
      return;
    }
    for (EventListener listener : listeners) {
      if (listener instanceof LifeCycleListener) {
        try {
          ((LifeCycleListener)listener).created(o);
        }
        catch (Exception ignore) {
        }
      }
    }
  }

  public void loaded(Object o) {
    if (listeners == null) {
      return;
    }
    for (EventListener listener : listeners) {
      if (listener instanceof LifeCycleListener) {
        try {
          ((LifeCycleListener)listener).loaded(o);
        }
        catch (Exception ignore) {
        }
      }
    }
  }

  public void persisted(Object o) {
    if (listeners == null) {
      return;
    }
    for (EventListener listener : listeners) {
      if (listener instanceof LifeCycleListener) {
        try {
          ((LifeCycleListener)listener).persisted(o);
        }
        catch (Exception ignore) {
        }
      }
    }
  }

  public void removed(Object o) {
    if (listeners == null) {
      return;
    }
    for (EventListener listener : listeners) {
      if (listener instanceof LifeCycleListener) {
        try {
          ((LifeCycleListener)listener).removed(o);
        }
        catch (Exception ignore) {
        }
      }
    }
  }

  public void propertyChanged(Object o, String propertyName, Object propertyValue) {
    if (listeners == null) {
      return;
    }
    for (EventListener listener : listeners) {
      if (listener instanceof LifeCycleListener) {
        try {
          ((StateChangeListener)listener).propertyChanged(o, propertyName, propertyValue);
        }
        catch (Exception ignore) {
        }
      }
    }
  }
}
