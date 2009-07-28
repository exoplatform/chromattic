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

package org.chromattic.test.onetomany.reference.polymorphism;

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
    addClass(TOTMRP_A.class);
    addClass(TOTMRP_C.class);
    addClass(TOTMRP_D.class);
  }

  public void testFoo() throws Exception {

    ChromatticSession session = login();

    TOTMRP_A a = session.create(TOTMRP_A.class, "a");
    String aId = session.persist(a);
    TOTMRP_C c = session.create(TOTMRP_C.class, "c");
    String cId = session.persist(c);
    TOTMRP_D d = session.create(TOTMRP_D.class, "d");
    String dId = session.persist(d);
    a.getBs().add(d);
    a.getBs().add(c);
    session.save();

    session = login();
    a = session.findById(TOTMRP_A.class, aId);
    c = session.findById(TOTMRP_C.class, cId);
    d = session.findById(TOTMRP_D.class, dId);
    Collection<TOTMRP_B> bs =  a.getBs();
    assertTrue(bs.contains(c));
    assertTrue(bs.contains(d));
    assertEquals(2, bs.size());
    Collection<TOTMRP_B> copy = new ArrayList<TOTMRP_B>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());
  }

  public void testTyped() throws Exception {

    ChromatticSession session = login();
    TOTMRP_A a = session.insert(TOTMRP_A.class, "a");
    TOTMRP_C c = session.insert(TOTMRP_C.class, "c");
    TOTMRP_D d = session.insert(TOTMRP_D.class, "d");

    Collection<TOTMRP_B> bs = a.getBs();
    bs.add(c);
    bs.add(d);

    Collection<TOTMRP_B> copy = new ArrayList<TOTMRP_B>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());

    bs.remove(c);
    copy = new ArrayList<TOTMRP_B>(bs);
    assertFalse(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(1, copy.size());

  }

}