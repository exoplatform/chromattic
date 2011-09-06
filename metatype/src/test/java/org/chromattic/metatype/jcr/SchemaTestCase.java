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
import org.chromattic.common.collection.Collections;
import org.chromattic.exo.RepositoryBootstrap;
import org.chromattic.metatype.*;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeTypeManager;
import java.util.Set;

public class SchemaTestCase extends TestCase {

  /** . */
  private Session session;

  /** . */
  private NodeTypeManager ntMgr;

  @Override
  protected void setUp() throws Exception {
    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();
    Session session = repo.login(new SimpleCredentials("exo", "exo".toCharArray()));
    NodeTypeManager ntMgr = session.getWorkspace().getNodeTypeManager();

    //
    this.session = session;
    this.ntMgr = ntMgr;
  }

  @Override
  protected void tearDown() throws Exception {
    session.logout();

    //
    ntMgr = null;
    session = null;
  }

  public void testFoo() throws Exception {
    Schema schema = JCRSchema.build(ntMgr);
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

  public void testProperty() throws Exception
  {
    Schema schema = JCRSchema.build(ntMgr);
    EntityType base = (EntityType)schema.getType("nt:base");

    //
    assertEquals(Collections.set("jcr:primaryType", "jcr:mixinTypes"), base.getPropertyNames());

    //
    PropertyDescriptor pt = base.getProperty("jcr:primaryType");
    assertNotNull(pt);
    assertEquals("jcr:primaryType", pt.getName());
    assertEquals(ValueType.STRING, pt.getValueType());
    assertEquals(true, pt.isSingleValued());
    assertEquals(false, pt.isMultiValued());

    //
    PropertyDescriptor mt = base.getProperty("jcr:mixinTypes");
    assertNotNull(mt);
    assertEquals("jcr:mixinTypes", mt.getName());
    assertEquals(ValueType.STRING, mt.getValueType());
    assertEquals(false, mt.isSingleValued());
    assertEquals(true, mt.isMultiValued());
  }
}
