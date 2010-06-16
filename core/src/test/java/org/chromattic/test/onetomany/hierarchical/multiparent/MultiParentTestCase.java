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

package org.chromattic.test.onetomany.hierarchical.multiparent;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MultiParentTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
    addClass(B.class);
    addClass(C.class);
  }

  public void testLoad() throws Exception {
    ChromatticSessionImpl session = login();
    Node rootNode = session.getRoot();
    Node aNode = rootNode.addNode("parents_a", getNodeTypeName(A.class));
    String aId = aNode.getUUID();
    Node bNode = aNode.addNode("b", getNodeTypeName(B.class));
    String bId = bNode.getUUID();
    Node cNode = bNode.addNode("c", getNodeTypeName(B.class));
    String cId = cNode.getUUID();
    rootNode.save();

    //
    session = login();
    A a = session.findById(A.class, aId);
    B b = session.findById(B.class, bId);
    B c = session.findById(B.class, cId);
    assertNull(c.getAParent());
    assertSame(b, c.getBParent());
    assertNull(c.getCParent());
    assertSame(a, b.getAParent());
    assertNull(b.getBParent());
    assertNull(b.getCParent());
  }
}
