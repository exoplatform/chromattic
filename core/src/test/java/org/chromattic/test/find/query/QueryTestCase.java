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

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.find.TFI_A;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.query.QueryLanguage;
import org.chromattic.api.query.Query;

import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class QueryTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TFI_A.class);
  }

  public void testQuery() {
    ChromatticSession session = login();
    TFI_A a = session.insert(TFI_A.class, "a");
    Query tmp = session.createQuery(QueryLanguage.SQL, "SELECT * FROM nt:base");
    Iterator<TFI_A> i = tmp.execute(TFI_A.class);
    assertTrue(i.hasNext());
    assertSame(a, i.next());
    assertFalse(i.hasNext());
  }
}
