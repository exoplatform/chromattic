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

package org.chromattic.test.lifecycle;

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
//@GroovyTestGeneration(chromatticClasses = {NR.class})
public class NotReferenceableTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(NR.class);
    addClass(B.class);
  }

  public void testCreate() {
    ChromatticSession session = login();
    NR a = session.create(NR.class);
    assertNotNull(a);
  }

  public void testInsert() {
    ChromatticSession session = login();
    NR a = session.insert(NR.class, "a");
    assertNotNull(a);
    session.save();
  }

  public void testRootNodeMappedButNotReferenceable() throws Exception {
    ChromatticSession session = login();

    //  Setup state
    Session jcrSession = session.getJCRSession();
    Node root = (Node)jcrSession.getItem(getRootNodePath());
    Node aNode = root.addNode("a");
    aNode.addMixin("mix:referenceable");
    String id = aNode.getUUID();
    jcrSession.save();

    //
    B b = session.findByPath(B.class, "a");
    assertNotNull(b);
    assertEquals(id, session.getId(b));
  }
}
