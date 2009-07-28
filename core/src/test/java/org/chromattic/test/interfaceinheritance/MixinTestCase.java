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

package org.chromattic.test.interfaceinheritance;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MixinTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TII_A_2.class);
  }

  public void testFoo() throws Exception {
    ChromatticSession session = login();
    TII_A_2 a = session.insert(TII_A_2.class, "tii_a");
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Set<String> mixinNames = new HashSet<String>();
    for (NodeType mixinNodeType : aNode.getMixinNodeTypes()) {
      mixinNames.add(mixinNodeType.getName());
    }
    assertTrue(mixinNames.contains("tii_mixin"));
  }
}