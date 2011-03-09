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

package org.chromattic.exo;

import junit.framework.TestCase;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class WeirdBugTestCase extends TestCase {

  public void testBootstrap() throws Exception {

    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();
    Session session = repo.login();
    Node list = session.getRootNode().addNode("list", "list");
    assertTrue(list.getDefinition().getDeclaringNodeType().hasOrderableChildNodes());
    list.addNode("foo", "nt:unstructured");
    list.addNode("bar", "nt:unstructured");
    list.addNode("juu", "nt:unstructured");
    session.save();
    session.logout();

    //
    session = repo.login();
    list = session.getRootNode().getNode("list");
    list.getNode("bar").remove();
    session.save();
    session.logout();

    //
    session = repo.login();
    list = session.getRootNode().getNode("list");
    list.addNode("daa", "nt:unstructured");
//    list.orderBefore("daa", null);
    NodeIterator it = list.getNodes();
    Node foo = it.nextNode();
    assertEquals("foo", foo.getName());
    Node juu = it.nextNode();
    assertEquals("juu", juu.getName());
    Node daa = it.nextNode();
    assertEquals("daa", daa.getName());
    assertFalse(it.hasNext());
    session.save();
    session.logout();

    //
    session = repo.login();
    list = session.getRootNode().getNode("list");
    it = list.getNodes();
    foo = it.nextNode();
    assertEquals("foo", foo.getName());
    juu = it.nextNode();
    assertEquals("juu", juu.getName());
    daa = it.nextNode();
    assertEquals("daa", daa.getName());
    assertFalse(it.hasNext());
    session.logout();
  }
}
