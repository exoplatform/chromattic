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
package org.chromattic.test.spi.exo;

import org.chromattic.common.Collections;
import org.chromattic.common.JCR;
import org.chromattic.exo.RepositoryBootstrap;

import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyBehaviorTestCase extends TestCase {

  /** . */
  private Repository repo;

  @Override
  protected void setUp() throws Exception {
    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap();
    Repository repo = bootstrap.getRepository();

    //
    this.repo = repo;
  }

  public void testAddRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a1");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b1");

    //
    Property ref = b.setProperty("ref", a);
    assertTrue(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());

    //
    session.save();
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));
  }

  public void testRemoveRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a2");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b2");
    b.setProperty("ref", a);
    session.save();

    //
    session = repo.login();
    root = session.getRootNode();
    a = root.getNode("a2");
    b = root.getNode("b2");
    Property ref = b.getProperty("ref");
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    ref.remove();
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    session.save();
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
  }

  public void testReAddRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a3");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b3");
    b.setProperty("ref", a);
    session.save();

    //
    session = repo.login();
    root = session.getRootNode();
    a = root.getNode("a3");
    b = root.getNode("b3");
    Property ref = b.getProperty("ref");

    //
    ref.remove();
    b.setProperty("ref", a);
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));

    //
    session.save();
    b.setProperty("ref", a);
    assertFalse(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(a.getReferences())));
  }

  public void testUpdateRef() throws Exception {
    Session session = repo.login();

    //
    Node root = session.getRootNode();
    Node a = root.addNode("a4");
    a.addMixin("mix:referenceable");
    Node b = root.addNode("b4");
    b.addMixin("mix:referenceable");
    Node c = root.addNode("c4");
    Property ref = c.setProperty("ref", a);

    //
    assertTrue(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(0, Collections.set(JCR.adapt(b.getReferences())).size());

    //
    c.setProperty("ref", b);
    assertTrue(ref.isNew());
    assertTrue(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(0, Collections.set(JCR.adapt(b.getReferences())).size());

    //
    session.save();
    assertFalse(ref.isNew());
    assertFalse(ref.isModified());
    assertEquals(0, Collections.set(JCR.adapt(a.getReferences())).size());
    assertEquals(Collections.set(ref), Collections.set(JCR.adapt(b.getReferences())));
  }
}
