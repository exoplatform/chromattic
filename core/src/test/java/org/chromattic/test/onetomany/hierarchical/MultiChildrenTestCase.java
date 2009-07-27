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
package org.chromattic.test.onetomany.hierarchical;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MultiChildrenTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(MULTICHILDREN_A.class);
    addClass(MULTICHILDREN_B.class);
    addClass(MULTICHILDREN_C.class);
    addClass(MULTICHILDREN_D.class);
  }

  public void testFoo() throws Exception {
    ChromatticSession session = login();

    MULTICHILDREN_A a = session.create(MULTICHILDREN_A.class, "a");
    MULTICHILDREN_B b = session.create(MULTICHILDREN_B.class, "b");
    MULTICHILDREN_C c = session.create(MULTICHILDREN_C.class, "c");
    MULTICHILDREN_D d = session.create(MULTICHILDREN_D.class, "d");

    session.persist(a);
    a.getBs().add(b);
    a.getBs().add(c);
    a.getBs().add(d);

    ArrayList<MULTICHILDREN_B> bsCopy = new ArrayList<MULTICHILDREN_B>(a.getBs());
    ArrayList<MULTICHILDREN_C> csCopy = new ArrayList<MULTICHILDREN_C>(a.getCs());
    ArrayList<MULTICHILDREN_D> dsCopy = new ArrayList<MULTICHILDREN_D>(a.getDs());

    assertEquals(3, bsCopy.size());
    assertTrue(bsCopy.contains(b));
    assertTrue(bsCopy.contains(c));
    assertTrue(bsCopy.contains(d));

    assertEquals(1, csCopy.size());
    assertTrue(csCopy.contains(c));

    assertEquals(1, dsCopy.size());
    assertTrue(dsCopy.contains(d));
  }
}
