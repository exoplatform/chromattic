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
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.ItemNotFoundException;
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
public class ReferenceManager {

  /** . */
  private Map<String, Entry> entries = new HashMap<String, Entry>();

  /** . */
  private final Session session;

  public ReferenceManager(Session session) {
    this.session = session;
  }

  public Iterator<Node> getReferents(Node referenced, String name) throws RepositoryException {
    Entry entry = getEntry(referenced);
    return entry.iterator(name);
  }

  public Node getReferenced(Node referent, String propertyName) throws RepositoryException {
    if (referent.hasProperty(propertyName)) {
      Property property = referent.getProperty(propertyName);
      if (property.getType() == PropertyType.REFERENCE) {
        try {
          return property.getNode();
        }
        catch (ItemNotFoundException e) {
          // The node has been transiently removed or concurrently removed
          return null;
        }
      } else {
        // throw new MappingException("Property " + name + " is not mapped to a reference type");
        // maybe issue a warn
        return null;
      }
    } else {
      return null;
    }
  }

  public Node setReferenced(Node referent, String propertyName, Node referenced) throws RepositoryException {

    Node oldReferenced = null;
    if (referent.hasProperty(propertyName)) {
      oldReferenced = referent.getProperty(propertyName).getNode();
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

    //
    referent.setProperty(propertyName, referenced);

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

  private static class Entry {

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

    public Iterator<Node> iterator(final String name) throws RepositoryException {

      // Julien : that looks like a query that would be executed each time
      // does it make sense to cache it ?
      PropertyIterator properties = referenced.getReferences();

      Set<Node> blah = propertiesScheduledForRemoval.get(name);
      if (blah == null) {
        blah = Collections.emptySet();
      }
      final Set<Node> tutu = blah;

      AbstractFilterIterator<Node, Property> i1 = new AbstractFilterIterator<Node, Property>((Iterator<Property>)properties) {
        protected Node adapt(Property property) {
          try {
            String propertyName = property.getName();
            if (propertyName.equals(name)) {
              Node src = property.getParent();
              if (!tutu.contains(src)) {
                return src;
              }
            }
            return null;
          }
          catch (RepositoryException e) {
            throw new UndeclaredRepositoryException(e);
          }
        }
      };

      //
      Set<Node> srcs = propertiesScheduledForAddition.get(name);
      if (srcs == null) {
        srcs = new HashSet<Node>();
        propertiesScheduledForAddition.put(name, srcs);
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
