/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.exo;

import junit.framework.TestCase;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class WeirdBug2TestCase extends TestCase {

  public void testWeird() throws Exception {
    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();
    Session session = repo.login(new SimpleCredentials("exo", "exo".toCharArray()));
    Node rootNode = session.getRootNode();
    Node aNode = rootNode.addNode("totmr_a");
    Node bNode = rootNode.addNode("totmr_b");
    ValueFactory valueFactory = rootNode.getSession().getValueFactory();
    Value value = valueFactory.createValue(aNode.getPath(), PropertyType.PATH);
    bNode.setProperty("ref", value);
    Property ref = bNode.getProperty("ref");
    assertEquals(PropertyType.PATH, ref.getType());
    rootNode.getSession().save();
    QueryManager mgr = rootNode.getSession().getWorkspace().getQueryManager();
    Query query = mgr.createQuery("SELECT * FROM nt:base WHERE ref='" + aNode.getPath() + "'", Query.SQL);
    QueryResult result = query.execute();
    NodeIterator i = result.getNodes();
    assertTrue(i.hasNext());
    Node found = i.nextNode();
    assertSame(bNode,  found);
    assertFalse(i.hasNext());
  }
}
