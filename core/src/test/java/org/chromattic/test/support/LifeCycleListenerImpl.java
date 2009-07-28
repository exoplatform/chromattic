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

import org.chromattic.api.LifeCycleListener;

import java.util.LinkedList;

import junit.framework.Assert;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class LifeCycleListenerImpl implements LifeCycleListener<Object> {

  /** . */
  private final LinkedList<Event> events = new LinkedList<Event>();

  public void clear() {
    events.clear();
  }

  public void assertEvent(EventType type, Object object) {
    assertNotEmpty();
    Event event = events.removeFirst();
    Assert.assertEquals(type, event.getType());
    Assert.assertEquals(object, event.getObject());
  }

  public void assertEmpty() {
    Assert.assertTrue("Was expecting event queue to be empty instead of " + events, events.size() == 0);
  }

  public void assertNotEmpty() {
    Assert.assertTrue(events.size() > 0);
  }

  public void created(Object o) {
    events.add(new Event(EventType.CREATED, o));
  }

  public void loaded(Object o) {
    events.add(new Event(EventType.LOADED, o));
  }

  public void persisted(Object o) {
    events.add(new Event(EventType.PERSISTED, o));
  }

  public void removed(Object o) {
    events.add(new Event(EventType.REMOVED, o));
  }
}
