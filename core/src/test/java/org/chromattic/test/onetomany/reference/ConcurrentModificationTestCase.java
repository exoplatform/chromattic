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

package org.chromattic.test.onetomany.reference;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import java.util.Iterator;
import java.util.ConcurrentModificationException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ConcurrentModificationTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTMR_A_3.class);
    addClass(TOTMR_B_3.class);
  }

  public void testFoo() throws Exception {

    ChromatticSession session = login();

    TOTMR_A_3 a = session.insert(TOTMR_A_3.class, "totmr_a_25");
    TOTMR_B_3 b1 = session.insert(TOTMR_B_3.class, "totmr_b_25");

    a.getBs().add(b1);

    Iterator<TOTMR_B_3> i1 = a.getBs().iterator();
    Iterator<TOTMR_B_3> i2 = a.getBs().iterator();

    assertTrue(i1.hasNext());
    assertTrue(i2.hasNext());
    i1.next();
    i1.remove();
    assertTrue(i2.hasNext());

    // This is normal to not get the concurrent modif now since there is always a prefetch of one element
    i2.next();

    // Now we should get it
    try {
      i2.next();
      fail();
    }
    catch (ConcurrentModificationException e) {
    }
  }

}
