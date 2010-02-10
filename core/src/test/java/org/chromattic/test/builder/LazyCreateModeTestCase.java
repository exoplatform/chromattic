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

package org.chromattic.test.builder;

import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Item;
import javax.jcr.Session;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class LazyCreateModeTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    setOptionValue(ChromatticBuilder.CREATE_ROOT_NODE, true);
    setOptionValue(ChromatticBuilder.LAZY_CREATE_ROOT_NODE, true);
    addClass(A.class);
  }

  @Override
  protected boolean pingRootNode() {
    return false;
  }

  public void testRootNodeLifeCycle() throws Exception {
    // First check it does not exist
    ChromatticSession session = login();
    Session jcrSession = session.getJCRSession();
    String path = getRootNodePath();
    assertFalse(jcrSession.itemExists(path));

    // Perform an operation
    session.insert(A.class, "a");

    // It should exist and be new
    assertTrue(jcrSession.itemExists(path));
    Item virtualRoot = jcrSession.getItem(path);
    assertTrue(virtualRoot.isNew());

    // Save
    session.save();

    // It should be saved
    assertFalse(virtualRoot.isNew());

    // Close
    session.close();

    // Check it exists
    session = login();
    jcrSession = session.getJCRSession();
    assertTrue(jcrSession.itemExists(path));
    session.close();
  }
}