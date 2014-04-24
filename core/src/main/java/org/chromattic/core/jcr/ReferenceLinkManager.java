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

import org.chromattic.common.collection.AbstractFilterIterator;
import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.PropertyType;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PropertyIterator;
import java.util.Iterator;

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
public class ReferenceLinkManager extends AbstractLinkManager {

  public ReferenceLinkManager(SessionWrapper sessionWrapper) {
    super(sessionWrapper);
  }

  protected Node _getReferenced(Property property) throws RepositoryException {
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
  }

  protected void _setReferenced(Node referent, String propertyName, Node referenced) throws RepositoryException {
    referent.setProperty(propertyName, referenced);
  }

  @SuppressWarnings("unchecked")
  protected Iterator<Node> _getReferents(Node referenced, String propertyName) throws RepositoryException {
    PropertyIterator bilto = referenced.getReferences();
    return new AbstractFilterIterator<Node, Property>(bilto) {
      protected Node adapt(Property property) {
        try {
          String propertyName = property.getName();
          if (propertyName.equals(propertyName)) {
            return property.getParent();
          }
          return null;
        }
        catch (RepositoryException e) {
          throw new UndeclaredRepositoryException(e);
        }
      }
    };
  }
}
