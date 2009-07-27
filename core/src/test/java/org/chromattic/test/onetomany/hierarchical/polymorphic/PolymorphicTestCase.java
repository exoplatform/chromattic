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
package org.chromattic.test.onetomany.hierarchical.polymorphic;

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PolymorphicTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TOTOP_A_1.class);
    addClass(TOTOP_C_1.class);
    addClass(TOTOP_D_1.class);
  }

  public void testFoo() throws Exception {

    ChromatticSession session = login();

    TOTOP_A_1 a = session.create(TOTOP_A_1.class, "a");
    String aId = session.persist(a);
    TOTOP_C_1 c = session.create(TOTOP_C_1.class, "c");
    a.getBs().add(c);
    String cId = session.getId(c);
    TOTOP_D_1 d = session.create(TOTOP_D_1.class, "d");
    a.getBs().add(d);
    String dId = session.getId(d);
    session.save();

    session = login();
    a = session.findById(TOTOP_A_1.class, aId);
    c = session.findById(TOTOP_C_1.class, cId);
    d = session.findById(TOTOP_D_1.class, dId);
    Collection<TOTOP_B_1> bs =  a.getBs();
    assertTrue(bs.contains(c));
    assertTrue(bs.contains(d));
    assertEquals(2, bs.size());
    Collection<TOTOP_B_1> copy = new ArrayList<TOTOP_B_1>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());



  }

}
