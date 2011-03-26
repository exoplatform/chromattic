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

import java.util.LinkedList;
import java.io.InputStream;
import java.io.IOException;

import junit.framework.Assert;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class EventQueue implements LifeCycleListener, StateChangeListener {

  /** . */
  private final LinkedList<Event> events = new LinkedList<Event>();

  public void clear() {
    events.clear();
  }

  public void assertLifeCycleEvent(LifeCycleEventType type, String id, String path, String name, Object object) {
    assertNotEmpty();
    Event event = events.removeFirst();
    Assert.assertTrue(event instanceof LifeCycleEvent);
    LifeCycleEvent lifeCycleEvent = (LifeCycleEvent)event;
    Assert.assertEquals(type, lifeCycleEvent.getType());
    Assert.assertEquals(id, lifeCycleEvent.getId());
    Assert.assertEquals(path, lifeCycleEvent.getPath());
    Assert.assertEquals(name, lifeCycleEvent.getName());
    Assert.assertEquals(object, lifeCycleEvent.getObject());
  }

  public void assertPropertyChangedEvent(Object object, String name, Object value) {
    assertNotEmpty();
    Event event = events.removeFirst();
    Assert.assertTrue(event instanceof PropertyChangedEvent);
    PropertyChangedEvent lifeCycleEvent = (PropertyChangedEvent)event;
    Assert.assertEquals(object, lifeCycleEvent.getObject());
    Assert.assertEquals(name, lifeCycleEvent.getName());
    if (value instanceof InputStream) {
      Assert.assertTrue(lifeCycleEvent.getValue() instanceof InputStream);
      InputStream s1 = (InputStream)value;
      InputStream s2 = (InputStream)lifeCycleEvent.getValue();
      while (true) {
        try {
          int i1 = s1.read();
          int i2 = s2.read();
          Assert.assertEquals(i1, i2);
          if (i1 == -1) {
            break;
          }
        }
        catch (IOException e) {
          Assert.fail();
        }
      }
    } else {
      Assert.assertEquals(value, lifeCycleEvent.getValue());
    }
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

  public void propertyChanged(Object o, String propertyName, Object propertyValue) {
    events.add(new PropertyChangedEvent(o, propertyName, propertyValue));
  }
}
