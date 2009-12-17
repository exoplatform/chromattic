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
package org.chromattic.test.jcr;

import junit.framework.TestCase;
import org.chromattic.core.jcr.info.NodeInfo;
import org.chromattic.core.jcr.info.NodeInfoManager;
import org.chromattic.core.jcr.info.PropertyDefinitionInfo;
import org.chromattic.exo.RepositoryBootstrap;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeInfoTestCase extends TestCase {

  private Session session;

  private NodeInfoManager mgr;

  @Override
  protected void setUp() throws Exception {

    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();
    session = repo.login();
    mgr = new NodeInfoManager();

  }

  @Override
  protected void tearDown() throws Exception {
    session.logout();
    session = null;
  }

  public void testFoo() throws Exception {

    Node a = session.getRootNode().addNode("a", "nt:unstructured");

    NodeInfo info = mgr.getNodeInfo(a);

    PropertyDefinitionInfo pdi = info.findPropertyDefinition("jcr:primaryType");

    assertNotNull(pdi);

  }
}
