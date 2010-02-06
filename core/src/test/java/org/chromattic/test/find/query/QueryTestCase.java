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
package org.chromattic.test.find.query;

import org.chromattic.common.collection.Collections;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.find.TFI_A;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TFI_A.class);
  }

  public void testQueryWithJCRPath() throws Exception {
    ChromatticSessionImpl session = login();
    TFI_A a = session.insert(TFI_A.class, "tfi_a");
    session.save();
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(TFI_A.class).where("jcr:path LIKE '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(TFI_A.class).where("jcr:path  LIKE '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(TFI_A.class).where("jcr:path LIKE  '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(TFI_A.class).where("jcr:path='" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(TFI_A.class).where("jcr:path = '" + session.getPath(a) + "'").get().objects()));
  }

  public void testQuery() throws Exception {
    ChromatticSessionImpl session = login();

    //
    String value = "BILTO";

    //
    TFI_A a = session.insert(TFI_A.class, "tfi_a");
    a.setFoo(value);
    session.save();

    //
    Collection<TFI_A> r1 = new ArrayList<TFI_A>();
    Iterator<TFI_A> it1 = session.createQueryBuilder(TFI_A.class).get().objects();
    while (it1.hasNext()) {
      TFI_A b = it1.next();
      r1.add(b);
    }
    assertEquals(1, r1.size());
    assertTrue(r1.contains(a));

    //
    Collection<TFI_A> r2 = new ArrayList<TFI_A>();
    Iterator<TFI_A> it2 = session.createQueryBuilder(TFI_A.class).where("foo='" + value + "'").get().objects();
    while (it2.hasNext()) {
      TFI_A b = it2.next();
      r2.add(b);
    }
    assertEquals(1, r2.size());
    Iterator<TFI_A> i2 = r2.iterator();
    assertSame(a, i2.next());
  }
}
