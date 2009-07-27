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
import org.chromattic.common.JCR;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class ObjectState {

  abstract String getId();

  abstract String getName();

  abstract String getPath();

  abstract void setName(String name);

  abstract Node getNode();

  abstract DomainSession getSession();

  abstract Status getStatus();

  public abstract String toString();

  static class Transient extends ObjectState {

    /** . */
    private String name;

    Transient(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    void setName(String name) {
      if (name != null) {
        JCR.validateName(name);
      }
      this.name = name;
    }

    String getPath() {
      return null;
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
      return Status.TRANSIENT;
    }

    public String toString() {
      return "ObjectStatus[status=" + Status.TRANSIENT + "]";
    }
  }

  static class Persistent extends ObjectState {

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

    Persistent(Node node, DomainSession session) throws RepositoryException {
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

  static class Removed extends ObjectState {

    /** . */
    private final String id;

    Removed(String id) {
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
}
