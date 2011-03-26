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

package org.chromattic.core.jcr;

import org.chromattic.common.CompoundIterator;
import org.chromattic.common.AbstractFilterIterator;
import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.RepositoryException;
import javax.jcr.Property;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.ConcurrentModificationException;

/**
 * <p>The reference manager takes care of managing references between nodes. The main reason is that
 * JCR reference management is a bit weird about the usage of <tt>Node#getReferences()</tt>. The goal
 * of this class is to manage one to many relationships between nodes and their consistency.</p>
 *
 * <p>The life time of this object is valid from the beginning of the session until the session
 * or a portion of the session is saved. When a session is saved, the clear operation will reset
 * the state of the reference manager.</p>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractLinkManager {

  /** . */
  private Map<String, Entry> entries = new HashMap<String, Entry>();

  /** . */
  protected final Session session;

  public AbstractLinkManager(Session session) {
    this.session = session;
  }

  public Iterator<Node> getReferents(Node referenced, String propertyName) throws RepositoryException {
    Entry entry = getEntry(referenced);
    return entry.iterator(propertyName);
  }

  protected abstract Node _getReferenced(Property property) throws RepositoryException;

  protected abstract void _setReferenced(Node referent, String propertyName, Node referenced) throws RepositoryException;

  protected abstract Iterator<Node> _getReferents(Node referenced, String propertyName) throws RepositoryException;

  public Node getReferenced(Node referent, String propertyName) throws RepositoryException {
    if (referent.hasProperty(propertyName)) {
      Property property = referent.getProperty(propertyName);
      return _getReferenced(property);
    } else {
      return null;
    }
  }

  public Node setReferenced(Node referent, String propertyName, Node referenced) throws RepositoryException {

    Node oldReferenced = null;
    if (referent.hasProperty(propertyName)) {
      Property property = referent.getProperty(propertyName);
      oldReferenced = _getReferenced(property);
      if (oldReferenced != null) {
        Entry entry = getEntry(oldReferenced);

        boolean scheduleForAddition = true;
        Set<Node> propertyScheduledForAddition = entry.propertiesScheduledForAddition.get(propertyName);
        if (propertyScheduledForAddition != null) {
          if (propertyScheduledForAddition.contains(referent)) {
            propertyScheduledForAddition.remove(referent);
            scheduleForAddition = false;
          }
        }

        //
        if (scheduleForAddition) {
          Set<Node> propertyScheduledForRemoval = entry.propertiesScheduledForRemoval.get(propertyName);
          if (propertyScheduledForRemoval == null) {
            propertyScheduledForRemoval = new HashSet<Node>();
            entry.propertiesScheduledForRemoval.put(propertyName, propertyScheduledForRemoval);
          }
          propertyScheduledForRemoval.add(referent);
          entry.version++;
        }
      }
    }

    //
    _setReferenced(referent, propertyName, referenced);

    //
    if (referenced != null) {
      Entry entry = getEntry(referenced);
      Set<Node> srcs = entry.propertiesScheduledForAddition.get(propertyName);
      if (srcs == null) {
        srcs = new HashSet<Node>();
        entry.propertiesScheduledForAddition.put(propertyName, srcs);
      }
      srcs.add(referent);
      entry.version++;
    } else {
      //
    }

    //
    return oldReferenced;
  }

  public void clear() {
    entries.clear();
  }

  private Entry getEntry(Node referenced) throws RepositoryException {
    Entry entry = entries.get(referenced.getUUID());
    if (entry == null) {
      entry = new Entry(referenced);
      entries.put(referenced.getUUID(), entry);
    }
    return entry;
  }

  private class Entry {

    /** . */
    private int version;

    /** . */
    private final Node referenced;

    /** . */
    private final Map<String, Set<Node>> propertiesScheduledForAddition;

    /** . */
    private final Map<String, Set<Node>> propertiesScheduledForRemoval;

    private Entry(Node referenced) {
      this.version = 0;
      this.referenced = referenced;
      this.propertiesScheduledForAddition = new HashMap<String, Set<Node>>();
      this.propertiesScheduledForRemoval = new HashMap<String, Set<Node>>();
    }

    public Iterator<Node> iterator(final String propertyName) throws RepositoryException {

      // Julien : that looks like a query that would be executed each time
      // does it make sense to cache it ?

      //
      Set<Node> blah = propertiesScheduledForRemoval.get(propertyName);
      if (blah == null) {
        blah = Collections.emptySet();
      }
      final Set<Node> tutu = blah;
      Iterator<Node> aaa = _getReferents(referenced, propertyName);
      AbstractFilterIterator<Node, Node> i1 = new AbstractFilterIterator<Node, Node>(aaa) {
        protected Node adapt(Node node) {
          if (!tutu.contains(node)) {
            return node;
          } else {
            return null;
          }
        }
      };

      //
      Set<Node> srcs = propertiesScheduledForAddition.get(propertyName);
      if (srcs == null) {
        srcs = new HashSet<Node>();
        propertiesScheduledForAddition.put(propertyName, srcs);
      }
      final Iterator<Node> i2 = srcs.iterator();

      //
      return new CompoundIterator<Node>(i1, i2) {

        int version = Entry.this.version;

        private void check() {
          if (version != Entry.this.version) {
            throw new ConcurrentModificationException();
          }
        }

        @Override
        public boolean hasNext() {
          check();
          return super.hasNext();
        }

        @Override
        public Node next() {
          check();
          return super.next();
        }

        @Override
        public void remove() {
          check();
          super.remove();
          version = Entry.this.version++;
        }
      };
    }
  }
}