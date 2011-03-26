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

import javax.jcr.Session;
import javax.jcr.Node;

import org.chromattic.exo.ExoSessionLifeCycle;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.core.jcr.SessionWrapperImpl;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.spi.jcr.SessionLifeCycle;
import org.chromattic.common.Collections;

import java.util.Set;
import java.util.List;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SessionManagerTestCase extends TestCase {

  /** . */
  private SessionWrapper mgr;

  /** . */
  private final String primaryNodeTypeName = "nt:unstructured";

  /** . */
  private final List<String> mixinNodeTypeNames = Arrays.asList("mix:referenceable");

  @Override
  protected void setUp() throws Exception {
    SessionLifeCycle sessionLF = new ExoSessionLifeCycle();
    Session session = sessionLF.login();

    //
    this.mgr = new SessionWrapperImpl(sessionLF, session, false, false);
  }

  public void testRemoveTransientReferent() throws Exception {
    Node a = mgr.addNode(mgr.getSession().getRootNode(), "a", primaryNodeTypeName, mixinNodeTypeNames);
    Node b = mgr.addNode(mgr.getSession().getRootNode(), "b", primaryNodeTypeName, mixinNodeTypeNames);
    mgr.setReferenced(a, "ref", b, LinkType.REFERENCE);
    mgr.remove(a);
    Set<Node> referents = Collections.set(mgr.getReferents(b, "ref", LinkType.REFERENCE));
    assertEquals(Collections.<Node>set(), referents);
  }

  public void testRemovePersistentReferent() throws Exception {
    Node a = mgr.addNode(mgr.getSession().getRootNode(), "a", primaryNodeTypeName, mixinNodeTypeNames);
    Node b = mgr.addNode(mgr.getSession().getRootNode(), "b", primaryNodeTypeName, mixinNodeTypeNames);
    mgr.setReferenced(a, "ref", b, LinkType.REFERENCE);
    mgr.remove(a);
    mgr.save();
    Set<Node> referents = Collections.set(mgr.getReferents(b, "ref", LinkType.REFERENCE));
    assertEquals(Collections.<Node>set(), referents);
  }
}
