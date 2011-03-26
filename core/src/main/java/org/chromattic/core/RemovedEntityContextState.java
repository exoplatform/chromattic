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
import org.chromattic.core.bean.SimpleValueInfo;

import javax.jcr.Node;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class RemovedEntityContextState extends EntityContextState {

  /** . */
  private final String id;

  RemovedEntityContextState(String id) {
    this.id = id;
  }

  String getName() {
    throw new IllegalStateException();
  }

  void setName(String name) {
    throw new IllegalStateException();
  }

  String getPath() {
    throw new IllegalStateException();
  }

  String getId() {
    throw new IllegalStateException();
  }

  Node getNode() {
    throw new IllegalStateException();
  }

  DomainSession getSession() {
    throw new IllegalStateException();
  }

  Status getStatus() {
    return Status.REMOVED;
  }

  <V> V getPropertyValue(String propertyName, SimpleValueInfo<V> type) {
    throw new IllegalStateException();
  }

  <V> List<V> getPropertyValues(String propertyName, SimpleValueInfo<V> simpleType, ListType listType) {
    throw new IllegalStateException();
  }

  <V> void setPropertyValue(String propertyName, SimpleValueInfo<V> type, V o) {
    throw new IllegalStateException();
  }

  <V> void setPropertyValues(String propertyName, SimpleValueInfo<V> type, ListType listType, List<V> objects) {
    throw new IllegalStateException();
  }

  public String toString() {
    return "ObjectStatus[id=" + id + ",status=" + Status.REMOVED + "]";
  }
}
