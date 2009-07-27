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
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PersistentContextState extends ContextState {

  /** . */
  private String name;

  /** . */
  private String path;

  /** . */
  private final String id;

  /** . */
  private final Node node;

  /** . */
  private final DomainSession session;

  PersistentContextState(Node node, DomainSession session) throws RepositoryException {
    this.name = node.getName();
    this.id = node.getUUID();
    this.path = node.getPath();
    this.node = node;
    this.session = session;
  }

  String getId() {
    return id;
  }

  String getPath() {
    return path;
  }

  String getName() {
    return name;
  }

  void setName(String name) {
    throw new IllegalStateException("Node name are read only");
  }

  Node getNode() {
    return node;
  }

  DomainSession getSession() {
    return session;
  }

  Status getStatus() {
    return Status.PERSISTENT;
  }

  public String toString() {
    return "ObjectStatus[id=" + id + ",status=" + Status.PERSISTENT + "]";
  }
}
