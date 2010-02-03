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
import javax.jcr.Session;
import javax.jcr.PropertyType;
import javax.jcr.PathNotFoundException;
import javax.jcr.ValueFactory;
import javax.jcr.Value;
import javax.jcr.query.QueryManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PathLinkManager extends AbstractLinkManager {

  public PathLinkManager(Session session) {
    super(session);
  }

  protected Node _getReferenced(Property property) throws RepositoryException {
    int type = property.getType();
    if (type == PropertyType.PATH) {
      String path = property.getString();
      try {
        return (Node)session.getItem(path);
      }
      catch (PathNotFoundException e) {
        // The node has been transiently removed or concurrently removed
        return null;
      }
    } else {
      // throw new MappingException("Property " + name + " is not mapped to a path type");
      // maybe issue a warn
      return null;
    }
  }

  protected void _setReferenced(Node referent, String propertyName, Node referenced) throws RepositoryException {
    if (referenced != null) {
      String path = referenced.getPath();
      ValueFactory valueFactory = session.getValueFactory();
      Value value = valueFactory.createValue(path, PropertyType.PATH);
      referent.setProperty(propertyName, value);
    } else {
      referent.setProperty(propertyName, (String)null);
    }
  }

  protected Iterator<Node> _getReferents(Node referenced, String propertyName) throws RepositoryException {
    String path = referenced.getPath();
    QueryManager queryMgr = session.getWorkspace().getQueryManager();
    Query query = queryMgr.createQuery("SELECT * FROM nt:base WHERE " + propertyName + "='" + path + "'", Query.SQL);
    QueryResult result = query.execute();
    @SuppressWarnings("unchecked") Iterator<Node> nodes = result.getNodes();
    return new AbstractFilterIterator<Node, Node>(nodes) {
      private Node current;
      protected Node adapt(Node node) {
        current = node;
        return node;
      }
      @Override
      public void remove() {
        if (current == null) {
          throw new IllegalStateException();
        }
        try {
          current.remove();
        }
        catch (RepositoryException e) {
          throw new UndeclaredRepositoryException(e);
        }
      }
    };
  }
}
