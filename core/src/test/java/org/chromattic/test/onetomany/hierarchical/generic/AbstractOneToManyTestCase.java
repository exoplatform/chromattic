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
package org.chromattic.test.onetomany.hierarchical.generic;

import org.chromattic.common.TypeLiteral;
import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractOneToManyTestCase <O, M> extends AbstractTestCase {

  /** . */
  private final Class<O> oneSide = TypeLiteral.get(getClass(), 0);

  /** . */
  private final Class<M> manySide = TypeLiteral.get(getClass(), 1);

  protected void createDomain() {
    addClass(oneSide);
    addClass(manySide);
  }

  public abstract void setOne(M many, O one);

  public abstract O getOne(M many);

  public abstract Collection<M> getMany(O many);

  public void testAdd() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();

    //
    Node aNode = rootNode.addNode("totm_a_a", "totm_a");
    O a = session.findByNode(oneSide, aNode);
    assertNotNull(a);
    Collection<M> children = getMany(a);
    assertNotNull(children);
    assertEquals(0, children.size());

    //
    Node bNode = aNode.addNode("b", "totm_b");
    M b = session.findByNode(manySide, bNode);
    assertEquals(a, getOne(b));
    assertTrue(children.contains(b));
  }

  public void testLoad() throws Exception {
    DomainSession session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("totm_a_b", "totm_a");
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", "totm_b");
    String bId = bNode.getUUID();
    rootNode.save();

    //
    session = login();
    O a = session.findById(oneSide, aId);
    assertNotNull(a);
    M b = session.findById(manySide, bId);
    assertEquals(a, getOne(b));
    Collection<M> children = getMany(a);
    assertNotNull(children);
    assertTrue(children.contains(b));
  }
}
