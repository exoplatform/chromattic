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

package org.chromattic.metatype.jcr;

import junit.framework.TestCase;
import org.chromattic.exo.RepositoryBootstrap;
import org.chromattic.metatype.*;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeTypeManager;

public class SchemaTestCase extends TestCase {

  public void testFoo() throws Exception {

    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();
    Session session = repo.login(new SimpleCredentials("exo", "exo".toCharArray()));
    NodeTypeManager mgr = session.getWorkspace().getNodeTypeManager();

    //
    Schema schema = JCRSchema.build(mgr);
    EntityType base = (EntityType)schema.getType("nt:base");
    assertNotNull(base);

    //
    EntityType unstructured = (EntityType)schema.getType("nt:unstructured");
    assertNotNull(unstructured);
    assertEquals(1, unstructured.getSuperRelationships().size());
    InheritanceRelationshipDescriptor toBase = unstructured.getSuperRelationships().iterator().next();
    assertSame(base, toBase.getDestination());
    assertSame(unstructured, toBase.getOrigin());
    assertEquals(1, unstructured.getChildrenRelationships().size());
    HierarchicalRelationshipDescriptor toAny = unstructured.getChildrenRelationships().iterator().next();
    assertSame(unstructured, toAny.getOrigin());
    assertSame(base, toAny.getDestination());
    assertEquals("*", toAny.getName());
  }
}
