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

package org.chromattic.test.type.extra;

import org.chromattic.common.IO;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.Value;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ExtraTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(A.class);
  }

  // todo : write an abstraction for the 3 same test using generics

  public void testByteArray() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    Node node = session.getNode(a);
    assertFalse(node.hasProperty("bytes"));
    assertEquals(null, a.getBytes());
    a.setBytes(new byte[]{0,1,2});
    assertTrue(Arrays.equals(new byte[]{0,1,2}, a.getBytes()));
    assertTrue(node.hasProperty("bytes"));
    Property bytes = node.getProperty("bytes");
    assertEquals(PropertyType.BINARY, bytes.getType());
    InputStream in = bytes.getStream();
    assertTrue(Arrays.equals(new byte[]{0,1,2},IO.getBytes(in)));
    node.setProperty("bytes", (Value)null);
    if (getConfig().isStateCacheDisabled()) {
      assertEquals(null, a.getBytes());
    } else {
      assertTrue(Arrays.equals(new byte[]{0,1,2}, a.getBytes()));
    }
  }

  public void testCalendar() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    Node node = session.getNode(a);
    assertFalse(node.hasProperty("calendar"));
    assertEquals(null, a.getCalendar());
    Calendar now = Calendar.getInstance();
    a.setCalendar(now);
    assertEquals(now, a.getCalendar());
    assertTrue(node.hasProperty("calendar"));
    Property calendar = node.getProperty("calendar");
    assertEquals(PropertyType.DATE, calendar.getType());
    assertEquals(now, calendar.getDate());
    node.setProperty("calendar", (Value)null);
    if (getConfig().isStateCacheDisabled()) {
      assertEquals(null, a.getCalendar());
    } else {
      assertEquals(now, a.getCalendar());
    }
  }

  public void testTimestamp() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    Node node = session.getNode(a);
    assertFalse(node.hasProperty("timestamp"));
    assertEquals(null, a.getTimestamp());
    Calendar now = Calendar.getInstance();
    a.setTimestamp(now.getTimeInMillis());
    assertEquals((Long)now.getTimeInMillis(), a.getTimestamp());
    assertTrue(node.hasProperty("timestamp"));
    Property timestamp = node.getProperty("timestamp");
    assertEquals(PropertyType.DATE, timestamp.getType());
    assertEquals(now, timestamp.getDate());
    node.setProperty("timestamp", (Value)null);
    if (getConfig().isStateCacheDisabled()) {
      assertEquals(null, a.getTimestamp());
    } else {
      assertEquals((Long)now.getTimeInMillis(), a.getTimestamp());
    }
  }

/*
  public void testIllegalValue() throws Exception {
    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    Node node = session.getNode(a);
    node.setProperty("currency", "bilto");
    try {
      a.getCurrency();
      fail();
    }
    catch (IllegalStateException ignore) {
    }
  }
*/
}