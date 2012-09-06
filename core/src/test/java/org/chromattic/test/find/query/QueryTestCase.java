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

import org.chromattic.api.query.Ordering;
import org.chromattic.api.query.QueryResult;
import org.chromattic.common.collection.Collections;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.find.A;
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class QueryTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(A.class);
  }

  public void testQueryWithJCRPath() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    session.save();
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(A.class).where("jcr:path LIKE '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(A.class).where("jcr:path  LIKE '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(A.class).where("jcr:path LIKE  '" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(A.class).where("jcr:path='" + session.getPath(a) + "'").get().objects()));
    assertEquals(Arrays.asList(a), Collections.list(session.createQueryBuilder(A.class).where("jcr:path = '" + session.getPath(a) + "'").get().objects()));
  }

  public void testQuery() throws Exception {
    ChromatticSessionImpl session = login();

    //
    String value = "BILTO";

    //
    A a = session.insert(A.class, "a");
    a.setFoo(value);
    session.save();

    //
    Collection<A> r1 = new ArrayList<A>();
    Iterator<A> it1 = session.createQueryBuilder(A.class).get().objects();
    while (it1.hasNext()) {
      A b = it1.next();
      r1.add(b);
    }
    assertEquals(1, r1.size());
    assertTrue(r1.contains(a));

    //
    Collection<A> r2 = new ArrayList<A>();
    Iterator<A> it2 = session.createQueryBuilder(A.class).where("foo='" + value + "'").get().objects();
    while (it2.hasNext()) {
      A b = it2.next();
      r2.add(b);
    }
    assertEquals(1, r2.size());
    Iterator<A> i2 = r2.iterator();
    assertSame(a, i2.next());
  }

  public void testNoOffsetnoLimit() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("bilto_offset");
    A b = session.insert(A.class, "b");
    b.setFoo("bilto_offset");
    A c = session.insert(A.class, "c");
    c.setFoo("bilto_offset");
    session.save();

    //
    QueryResult<A> it1 = session.createQueryBuilder(A.class).where("foo='bilto_offset'").get().objects();
    assertTrue(it1.hasNext());
    it1.next();
    assertTrue(it1.hasNext());
    it1.next();
    assertTrue(it1.hasNext());
    it1.next();
    assertFalse(it1.hasNext());

    //
    assertEquals(3, it1.size());
    assertEquals(3, it1.hits());
  }

  public void testOffset() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("bilto_offset");
    A b = session.insert(A.class, "b");
    b.setFoo("bilto_offset");
    A c = session.insert(A.class, "c");
    c.setFoo("bilto_offset");
    session.save();

    //
    QueryResult<A> it1 = session.createQueryBuilder(A.class).where("foo='bilto_offset'").get().objects(1L, null);
    assertTrue(it1.hasNext());
    it1.next();
    assertTrue(it1.hasNext());
    it1.next();
    assertFalse(it1.hasNext());

    //
    assertEquals(2, it1.size());
    assertEquals(3, it1.hits());
  }

  public void testLimit() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("bilto_offset");
    A b = session.insert(A.class, "b");
    b.setFoo("bilto_offset");
    A c = session.insert(A.class, "c");
    c.setFoo("bilto_offset");
    session.save();

    //
    QueryResult<A> it1 = session.createQueryBuilder(A.class).where("foo='bilto_offset'").get().objects(null, 2L);
    assertTrue(it1.hasNext());
    it1.next();
    assertTrue(it1.hasNext());
    it1.next();
    assertFalse(it1.hasNext());

    //
    assertEquals(2, it1.size());
    assertEquals(3, it1.hits());
  }

  public void testOffsetLimit() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("bilto_offset");
    A b = session.insert(A.class, "b");
    b.setFoo("bilto_offset");
    A c = session.insert(A.class, "c");
    c.setFoo("bilto_offset");
    session.save();

    //
    QueryResult<A> it1 = session.createQueryBuilder(A.class).where("foo='bilto_offset'").get().objects(1L, 1L);
    assertTrue(it1.hasNext());
    it1.next();
    assertFalse(it1.hasNext());

    //
    assertEquals(1, it1.size());
    assertEquals(3, it1.hits());
  }

  public void testOrderByASC() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("a");
    A b = session.insert(A.class, "b");
    b.setFoo("b");
    A c = session.insert(A.class, "c");
    c.setFoo("c");
    session.save();

    //
    QueryResult<A> it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.ASC).get().objects();
    assertTrue(it.hasNext());
    assertEquals("a", it.next().getFoo());
    assertTrue(it.hasNext());
    assertEquals("b", it.next().getFoo());
    assertTrue(it.hasNext());
    assertEquals("c", it.next().getFoo());
    assertFalse(it.hasNext());

  }

  public void testOrderByDESC() throws Exception {

    ChromatticSessionImpl session = login();

    //
    A a = session.insert(A.class, "a");
    a.setFoo("a");
    A b = session.insert(A.class, "b");
    b.setFoo("b");
    A c = session.insert(A.class, "c");
    c.setFoo("c");
    session.save();

    //
    QueryResult<A> it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.DESC).get().objects();
    assertTrue(it.hasNext());
    assertEquals("c", it.next().getFoo());
    assertTrue(it.hasNext());
    assertEquals("b", it.next().getFoo());
    assertTrue(it.hasNext());
    assertEquals("a", it.next().getFoo());
    assertFalse(it.hasNext());

  }

  public void testOrderByMultiValueDESC() throws Exception{
    ChromatticSessionImpl session = login();

    A a = session.insert(A.class, "a");
    a.setFoo("a");
    a.setBar("b");
    A b = session.insert(A.class, "b");
    b.setFoo("a");
    b.setBar("a");
    A c = session.insert(A.class, "c");
    c.setFoo("b");
    c.setBar("b");
    session.save();

        //
    QueryResult<A> it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.DESC).orderBy("bar", Ordering.DESC).get().objects();
    assertTrue(it.hasNext());
    A result = it.next();
    assertEquals("b", result.getFoo());
    assertEquals("b", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("b", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("a", result.getBar());
    assertFalse(it.hasNext());

    it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.DESC).orderBy("bar", Ordering.ASC).get().objects();
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("b", result.getFoo());
    assertEquals("b", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("a", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("b", result.getBar());
    assertFalse(it.hasNext());
  }

  public void testOrderByMultiValueASC() throws Exception{
    ChromatticSessionImpl session = login();

    A a = session.insert(A.class, "a");
    a.setFoo("a");
    a.setBar("b");
    A b = session.insert(A.class, "b");
    b.setFoo("a");
    b.setBar("a");
    A c = session.insert(A.class, "c");
    c.setFoo("b");
    c.setBar("b");
    session.save();

    //
    QueryResult<A> it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.ASC).orderBy("bar", Ordering.ASC).get().objects();
    assertTrue(it.hasNext());
    A result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("a", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("b", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("b", result.getFoo());
    assertEquals("b", result.getBar());
    assertFalse(it.hasNext());

    it = session.createQueryBuilder(A.class).orderBy("foo", Ordering.ASC).orderBy("bar", Ordering.DESC).get().objects();
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("b", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("a", result.getFoo());
    assertEquals("a", result.getBar());
    assertTrue(it.hasNext());
    result = it.next();
    assertEquals("b", result.getFoo());
    assertEquals("b", result.getBar());
    assertFalse(it.hasNext());
  }
}
