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
package org.chromattic.test.onetomany.hierarchical.generic;

import org.chromattic.api.ChromatticSession;
import org.chromattic.common.TypeLiteral;
import org.chromattic.test.AbstractTestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractMultiChildrenTestCase<O, M1, M2 extends M1, M3 extends M1> extends AbstractTestCase {

  /** . */
  private final Class<O> oneSide = TypeLiteral.get(getClass(), 0);

  /** . */
  private final Class<M1> manySide1 = TypeLiteral.get(getClass(), 1);

  /** . */
  private final Class<M2> manySide2 = TypeLiteral.get(getClass(), 2);

  /** . */
  private final Class<M3> manySide3 = TypeLiteral.get(getClass(), 3);

  public abstract <M extends M1> Collection<M> getMany(O one, Class<M> manySide);

  protected void createDomain() {
    addClass(oneSide);
    addClass(manySide1);
    addClass(manySide2);
    addClass(manySide3);
  }

  public void testFoo() throws Exception {
    ChromatticSession session = login();

    //
    O a = session.create(oneSide, "a");
    M1 b = session.create(manySide1, "b");
    M2 c = session.create(manySide2, "c");
    M3 d = session.create(manySide3, "d");

    //
    session.persist(a);
    getMany(a, manySide1).add(b);
    getMany(a, manySide2).add(c);
    getMany(a, manySide3).add(d);

    //
    ArrayList<M1> bsCopy = new ArrayList<M1>(getMany(a, manySide1));
    ArrayList<M2> csCopy = new ArrayList<M2>(getMany(a, manySide2));
    ArrayList<M3> dsCopy = new ArrayList<M3>(getMany(a, manySide3));

    //
    assertEquals(3, bsCopy.size());
    assertTrue(bsCopy.contains(b));
    assertTrue(bsCopy.contains(c));
    assertTrue(bsCopy.contains(d));

    //
    assertEquals(1, csCopy.size());
    assertTrue(csCopy.contains(c));

    //
    assertEquals(1, dsCopy.size());
    assertTrue(dsCopy.contains(d));
  }

  public void testBar() throws Exception {

    ChromatticSession session = login();

    O a = session.create(oneSide, "a");
    String aId = session.persist(a);
    M2 c = session.create(manySide2, "c");
    getMany(a, manySide1).add(c);
    String cId = session.getId(c);
    M3 d = session.create(manySide3, "d");
    getMany(a, manySide1).add(d);
    String dId = session.getId(d);
    session.save();

    session = login();
    a = session.findById(oneSide, aId);
    c = session.findById(manySide2, cId);
    d = session.findById(manySide3, dId);
    Collection<M1> bs =  getMany(a, manySide1);
    assertTrue(bs.contains(c));
    assertTrue(bs.contains(d));
    assertEquals(2, bs.size());
    Collection<M1> copy = new ArrayList<M1>(bs);
    assertTrue(copy.contains(c));
    assertTrue(copy.contains(d));
    assertEquals(2, copy.size());
  }
}
