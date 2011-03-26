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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class LifeCycleEvent extends Event {

  /** . */
  private final LifeCycleEventType type;

  /** . */
  private final String id;

  /** . */
  private final String path;

  /** . */
  private final String name;

  /** . */
  private final Object object;

  public LifeCycleEvent(LifeCycleEventType type, Object object) {
    this.type = type;
    this.id = null;
    this.name = null;
    this.path = null;
    this.object = object;
  }

  public LifeCycleEvent(LifeCycleEventType type, String id, String path, String name, Object object) {
    this.type = type;
    this.id = id;
    this.path = path;
    this.name = name;
    this.object = object;
  }

  public LifeCycleEventType getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public Object getObject() {
    return object;
  }

  @Override
  public String toString() {
    return "LifeCycleEvent[type=" + type + ",object=" + object + "]";
  }
}
