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

package org.chromattic.test.onetoone.hierarchical;

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {C1.class, C2.class, C3.class, C4.class})
public class MultiParentTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(C2.class);
    addClass(C3.class);
    addClass(C4.class);
  }

  public void testFoo() throws Exception {

    ChromatticSession session = login();

    C2 c2 = session.insert(C2.class, "c2");
    C4 c4 = session.create(C4.class);
    c4.setParent1(c2);
    assertSame(c2, c4.getParent1());
    assertSame(null, c4.getParent2());

  }
}
