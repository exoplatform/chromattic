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

package org.chromattic.test.support;

import org.chromattic.api.event.LifeCycleListener;
import org.chromattic.api.event.StateChangeListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class EventQueue implements LifeCycleListener, StateChangeListener {

  /** . */
  private final Set<Event> events = new HashSet<Event>();

  public void clear() {
    events.clear();
  }

  public void assertLifeCycleEvent(LifeCycleEventType type, String id, String path, String name, Object object) {
    assertNotEmpty();
    Assert.assertTrue(events.remove(new LifeCycleEvent(type, id, path, name, object)));
  }

  public void assertPropertyChangedEvent(String id, Object object, String name, Object value) {
    assertNotEmpty();
    Assert.assertTrue(events.remove(new PropertyChangedEvent(id, object, name, value)));
  }

  public void assertEmpty() {
    Assert.assertTrue("Was expecting event queue to be empty instead of " + events, events.size() == 0);
  }

  public void assertNotEmpty() {
    Assert.assertTrue(events.size() > 0);
  }

  public void created(Object o) {
    events.add(new LifeCycleEvent(LifeCycleEventType.CREATED, o));
  }

  public void loaded(String id, String path, String name, Object o) {
    events.add(new LifeCycleEvent(LifeCycleEventType.LOADED, id, path, name, o));
  }

  public void added(String id, String path, String name, Object o) {
    events.add(new LifeCycleEvent(LifeCycleEventType.ADDED, id, path, name, o));
  }

  public void removed(String id, String path, String name, Object o) {
    events.add(new LifeCycleEvent(LifeCycleEventType.REMOVED, id, path, name, o));
  }

  public void propertyChanged(String id, Object o, String propertyName, Object propertyValue) {
    events.add(new PropertyChangedEvent(id, o, propertyName, propertyValue));
  }
}
