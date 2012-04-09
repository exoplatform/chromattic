/******************************************************************************
 * JBoss by Red Hat                                               *
 * Copyright 2010, Red Hat Middleware, LLC, and individual                 *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 *****************************************************************************/

package org.chromattic.ext.format;

import junit.framework.TestCase;
import org.chromattic.api.format.ObjectFormatter;

/**
 * @author <a href="mailto:theute@redhat.com">Thomas Heute</a>
 * @version $Revision$
 */
public class BaseEncodingObjectFormatterTestCase extends TestCase {

  /** . */
  private final ObjectFormatter formatter = new BaseEncodingObjectFormatter();

  private void assertEscapeString(String expected, String s) {
    assertEquals(expected, formatter.encodeNodeName(null, s));
    assertEquals(s, formatter.decodeNodeName(null, expected));
  }

  public void testStrings() {
    assertEscapeString("", "");
    assertEscapeString("a", "a");
    assertEscapeString("%3A", ":");
    assertEscapeString("%7C", "|");
    assertEscapeString("%5B", "[");
    assertEscapeString("%5D", "]");
    assertEscapeString("%2F", "/");
    assertEscapeString("%2A", "*");
    assertEscapeString("%25", "%");
    assertEscapeString("%27", "'");
    assertEscapeString("%22", "\"");
    assertEscapeString("%09", "\t");
    assertEscapeString("%0A", "\n");
    assertEscapeString("%0D", "\r");
    assertEscapeString("%20test", " test");
    assertEscapeString("test%20", "test ");
    assertEscapeString("%20test value%20", " test value ");
    assertEscapeString("%2Ed", ".d");
    assertEscapeString("a%2Fb", "a/b");
  }
}