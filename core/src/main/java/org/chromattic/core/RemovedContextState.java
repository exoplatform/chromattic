/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.chromattic.core;

import org.chromattic.api.Status;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class RemovedContextState extends ContextState {

  /** . */
  private final String id;

  RemovedContextState(String id) {
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

  public String toString() {
    return "ObjectStatus[id=" + id + ",status=" + Status.REMOVED + "]";
  }
}
