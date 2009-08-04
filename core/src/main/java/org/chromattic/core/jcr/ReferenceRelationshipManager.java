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

import org.chromattic.common.AbstractFilterIterator;
import org.chromattic.api.UndeclaredRepositoryException;

import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.PropertyType;
import javax.jcr.ItemNotFoundException;
import javax.jcr.PropertyIterator;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReferenceRelationshipManager extends AbstractRelationshipManager {

  public ReferenceRelationshipManager(Session session) {
    super(session);
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
