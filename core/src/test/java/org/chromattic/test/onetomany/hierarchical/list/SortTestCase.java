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
package org.chromattic.test.onetomany.hierarchical.list;

import org.chromattic.core.DomainSession;
import org.chromattic.test.AbstractTestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SortTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A1.class);
    addClass(B1.class);
  }

  public void testAddSingleton() throws Exception {
    DomainSession session = login();
    A1 a = session.insert(A1.class, "a");
    List<B1> bs = a.getChildren();
    B1 b1 = session.create(B1.class, "1");
    B1 b2 = session.create(B1.class, "2");
    B1 b3 = session.create(B1.class, "3");
    bs.add(b3);
    bs.add(b2);
    bs.add(b1);

    //
//    Collections.sort(bs);

    //
//    assertSame(b1, bs.get(0));
//    assertSame(b2, bs.get(1));
//    assertSame(b3, bs.get(2));
  }


}
