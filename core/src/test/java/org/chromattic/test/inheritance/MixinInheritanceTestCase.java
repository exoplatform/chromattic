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

package org.chromattic.test.inheritance;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

/**
 * @author Julien Viet
 */
public class MixinInheritanceTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(M0.class);
    addClass(M1.class);
    addClass(M2.class);
  }

  public void testCreate() throws Exception {
    ChromatticSessionImpl session = login();
    M0 m0 = session.insert(M0.class, "m0");
    M2 m2 = session.create(M2.class);
    session.setEmbedded(m0, M2.class, m2);
    session.save();
    session.close();

    //
    session = login();
    m0 = session.findByPath(M0.class, "m0");
    assertNotNull(m0);
    M1 m1 = session.getEmbedded(m0, M1.class);
    assertNotNull(m1);
    assertFalse(m1 instanceof M2);
    m2 = session.getEmbedded(m0, M2.class);
    assertNotNull(m2);
  }
}
