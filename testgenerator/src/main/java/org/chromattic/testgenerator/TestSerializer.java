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

package org.chromattic.testgenerator;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Set;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class TestSerializer
{
   private static final String ROOT_TAG = "testgen";
   private static final String TEST_TAG = "test";
   
   private Set<String> names;

   public TestSerializer(final Set<String> names)
   {
      this.names = names;
   }

   public void writeTo(Writer writer) throws IOException
   {
      startTag(writer, ROOT_TAG);
      for (String name : names)
      {
         startTag(writer, TEST_TAG);
         plaintext(writer, name);
         endTag(writer, TEST_TAG);
      }
      endTag(writer, ROOT_TAG);
   }

   private void startTag(Writer writer, String tagName) throws IOException
   {
      writer.append(String.format("<%s>", tagName));
   }

   private void endTag(Writer writer, String tagName) throws IOException
   {
      writer.append(String.format("</%s>", tagName));
   }

   private void plaintext(Writer writer, CharSequence text) throws IOException
   {
      writer.append(text);
   }
}
