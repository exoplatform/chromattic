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
    addClass(A.class);
    addClass(C.class);
    addClass(D.class);
  }

  public void testFoo() throws Exception {

    ChromatticSession session = login();

    A a = session.create(A.class, "a");
    String aId = session.persist(a);
    C c = session.create(C.class, "c");
    String cId = session.persist(c);
    D d = session.create(D.class, "d");
    String dId = session.persist(d);
    a.getBs().add(d);
    a.getBs().add(c);
    session.save();

    session = login();
    a = session.findById(A.class, aId);
    c = session.findById(C.class, cId);
    d = session.findById(D.class, dId);
    Collection<B> bs =  a.getBs();
    assertTrue(bs.contains(c));
    assertTrue(bs.contains(d));
    assertEquals(2, bs.size());
    Collection<B> copy = new ArrayList<B>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());
  }

  public void testTyped() throws Exception {

    ChromatticSession session = login();
    A a = session.insert(A.class, "a");
    C c = session.insert(C.class, "c");
    D d = session.insert(D.class, "d");

    Collection<B> bs = a.getBs();
    bs.add(c);
    bs.add(d);

    Collection<B> copy = new ArrayList<B>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());

    bs.remove(c);
    copy = new ArrayList<B>(bs);
    assertFalse(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(1, copy.size());

  }

}