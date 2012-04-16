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
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.core.jcr.type.NodeTypeInfo;
import org.chromattic.core.jcr.type.PrimaryTypeInfo;

import javax.jcr.Node;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class RemovedEntityContextState extends EntityContextState {

  /** . */
  private final DomainSession session;

  /** . */
  private final String path;

  /** . */
  private final String name;

  /** . */
  private final PrimaryTypeInfo typeInfo;

  RemovedEntityContextState(DomainSession session, String path, String name, PrimaryTypeInfo typeInfo) {
    this.session = session;
    this.path = path;
    this.name = name;
    this.typeInfo = typeInfo;
  }

  String getLocalName() {
    return name;
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
    return session;
  }

  Status getStatus() {
    return Status.REMOVED;
  }

  PrimaryTypeInfo getTypeInfo() {
    return typeInfo;
  }

  <V> boolean hasProperty(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {
    throw new IllegalStateException();
  }

  <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt) {
    throw new IllegalStateException();
  }

  <L, V> L getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType) {
    throw new IllegalStateException();
  }

  <V> void setPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, V o) {
    throw new IllegalStateException();
  }

  <L, V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType, L propertyValues) {
    throw new IllegalStateException();
  }

  public String toString() {
    return "ObjectStatus[path=" + path + ",status=" + Status.REMOVED + "]";
  }
}
