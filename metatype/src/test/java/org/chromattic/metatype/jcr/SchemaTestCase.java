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
    InheritanceRelationshipDescriptor toBase = unstructured.getSuperRelationship("nt:base");
    assertSame(base, toBase.getDestination());
    assertSame(toBase, unstructured.getSuperRelationship("nt:base"));
    assertSame(unstructured, toBase.getOrigin());
    assertEquals(unstructured.getSuperRelationships(), java.util.Collections.singletonMap("nt:base", toBase));
    assertEquals(unstructured.getSuperEntityRelationships(), java.util.Collections.singletonMap("nt:base", toBase));
    assertEquals(unstructured.getSuperMixinRelationships(), java.util.Collections.<String, InheritanceRelationshipDescriptor>emptyMap());
    assertEquals(1, unstructured.getChildrenRelationships().size());
    HierarchicalRelationshipDescriptor toAny = unstructured.getChildrenRelationships().values().iterator().next();
    assertSame(toAny, unstructured.getChildRelationship("*"));
    assertSame(unstructured, toAny.getOrigin());
    assertSame(base, toAny.getDestination());
    assertEquals("*", toAny.getName());

    //
    EntityType hierarchyNode = (EntityType)schema.getType("nt:hierarchyNode");
    EntityType file = (EntityType)schema.getType("nt:file");
    assertTrue(file.inherits(hierarchyNode));
    assertTrue(file.inherits(base));
  }

  public void testProperty() throws Exception
  {
    Schema schema = JCRSchema.build(ntMgr);
    EntityType base = (EntityType)schema.getType("nt:base");

    //
    assertEquals(Collections.set("jcr:primaryType", "jcr:mixinTypes"), base.getProperties().keySet());

    //
    PropertyDescriptor pt = base.getProperty("jcr:primaryType");
    assertNotNull(pt);
    assertEquals("jcr:primaryType", pt.getName());
    assertEquals(DataType.STRING, pt.getValueType());
    assertEquals(true, pt.isSingleValued());
    assertEquals(false, pt.isMultiValued());

    //
    PropertyDescriptor mt = base.getProperty("jcr:mixinTypes");
    assertNotNull(mt);
    assertEquals("jcr:mixinTypes", mt.getName());
    assertEquals(DataType.STRING, mt.getValueType());
    assertEquals(false, mt.isSingleValued());
    assertEquals(true, mt.isMultiValued());
  }
}
