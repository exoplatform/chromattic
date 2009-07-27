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

  public Iterator<Node> getReferences(Node dst, String name) throws RepositoryException {
    Entry entry = getEntry(dst);
    return entry.iterator(name);
  }

  public Node setReference(Node src, String name, Node newDst) throws RepositoryException {

    Node oldDst = null;
    if (src.hasProperty(name)) {
      oldDst = src.getProperty(name).getNode();
      Entry entry = getEntry(oldDst);

      boolean scheduleForAddition = true;
      Set<Node> propertyScheduledForAddition = entry.propertiesScheduledForAddition.get(name);
      if (propertyScheduledForAddition != null) {
        if (propertyScheduledForAddition.contains(src)) {
          propertyScheduledForAddition.remove(src);
          scheduleForAddition = false;
        }
      }

      //
      if (scheduleForAddition) {
        Set<Node> propertyScheduledForRemoval = entry.propertiesScheduledForRemoval.get(name);
        if (propertyScheduledForRemoval == null) {
          propertyScheduledForRemoval = new HashSet<Node>();
          entry.propertiesScheduledForRemoval.put(name, propertyScheduledForRemoval);
        }
        propertyScheduledForRemoval.add(src);
        entry.version++;
      }
    }

    //
    src.setProperty(name, newDst);

    //
    if (newDst != null) {
      Entry entry = getEntry(newDst);
      Set<Node> srcs = entry.propertiesScheduledForAddition.get(name);
      if (srcs == null) {
        srcs = new HashSet<Node>();
        entry.propertiesScheduledForAddition.put(name, srcs);
      }
      srcs.add(src);
      entry.version++;
    } else {
      //
    }

    //
    return oldDst;
  }

  public void clear() {
    entries.clear();
  }

  private Entry getEntry(Node dst) throws RepositoryException {
    Entry entry = entries.get(dst.getUUID());
    if (entry == null) {
      entry = new Entry(dst);
      entries.put(dst.getUUID(), entry);
    }
    return entry;
  }

  private static class Entry {

    /** . */
    private int version;

    /** . */
    private final Node dst;

    /** . */
    private final Map<String, Set<Node>> propertiesScheduledForAddition;

    /** . */
    private final Map<String, Set<Node>> propertiesScheduledForRemoval;

    private Entry(Node dst) {
      this.version = 0;
      this.dst = dst;
      this.propertiesScheduledForAddition = new HashMap<String, Set<Node>>();
      this.propertiesScheduledForRemoval = new HashMap<String, Set<Node>>();
    }

    public Iterator<Node> iterator(final String name) throws RepositoryException {

      PropertyIterator properties = dst.getReferences();

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
