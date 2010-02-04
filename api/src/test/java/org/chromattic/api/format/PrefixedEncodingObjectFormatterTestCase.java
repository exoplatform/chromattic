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

package org.chromattic.api.format;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:theute@redhat.com">Thomas Heute</a>
 * @version $Revision$
 */
public class PrefixedEncodingObjectFormatterTestCase extends TestCase
{

   private String prefix = "toto:";
   
   /** . */
   private final ObjectFormatter formatter = new TotoEncodingObjectFormatter();

   private void assertString(String expected, String s)
   {
      assertEquals(expected, formatter.encodeNodeName(null, s));
      assertEquals(s, formatter.decodeNodeName(null, expected));
   }

   private void assertCannotDecode(String s)
   {
      try
      {
         formatter.decodeNodeName(null, s);
         fail();
      }
      catch (IllegalStateException ignore)
      {
      }
   }

   public void testStrings()
   {
      assertString(prefix, "");
      assertString(prefix + "a", "a");
      assertString(prefix + "%00", "{");
      assertString(prefix + "%01", "}");
      assertString(prefix + "%02", ".");
      assertString(prefix + "%03", "/");
      assertString(prefix + "%04", ":");
      assertString(prefix + "%05", "[");
      assertString(prefix + "%06", "]");
      assertString(prefix + "%07", "|");
      assertString(prefix + "%08", "*");
      assertString(prefix + "%09", "%");
      assertString(prefix + "a%03b", "a/b");
   }

   public void testDecodeFailure()
   {
      assertCannotDecode("%0");
      assertCannotDecode("%0" + (char)('0' - 1));
      assertCannotDecode("%0" + (char)('9' + 1));
      assertCannotDecode("%1");
   }
}