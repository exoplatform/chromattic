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

import org.chromattic.common.logging.Logger;
import javax.lang.model.element.TypeElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class TestSerializer
{
   private static final Logger log = Logger.getLogger(TestSerializer.class);
   private static final String ROOT_TAG = "testgen";
   private static final String TEST_TAG = "test";

   public void writeTo(Writer writer, Set<TestRef> names) throws IOException
   {
      startTag(writer, ROOT_TAG);
      for (TestRef ref : names)
      {
         startTag(writer, TEST_TAG);
         writeName(writer, ref.getName());
         startTag(writer, "chromatticObjects");
         for (String chromatticObjectName : ref.getChromatticObject())
         {
            writeChromatticObject(writer, chromatticObjectName);
         }
         endTag(writer, "chromatticObjects");
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

   private void writeName(Writer writer, String name) throws IOException
   {
      startTag(writer, "name");
      plaintext(writer, name);
      endTag(writer, "name");
   }

   private void writeChromatticObject(Writer writer, String objectName) throws IOException
   {
      startTag(writer, "chromatticObject");
      plaintext(writer, objectName);
      endTag(writer, "chromatticObject");
   }

   private void plaintext(Writer writer, CharSequence text) throws IOException
   {
      writer.append(text);
   }

   public List<TestRef> getClassNames(InputStream is) {
      List<TestRef> refs = new ArrayList<TestRef>();
      try {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLStreamReader reader = factory.createXMLStreamReader(is);
         TestRef currentTestRef = null;
         while (reader.hasNext())
         {
            reader.next();
            if (reader.getEventType() == XMLStreamReader.START_ELEMENT)
            {
               String name = reader.getName().toString();
               if (name.equals("name"))
               {
                  reader.next();
                  currentTestRef = new TestRef(reader.getText());
                  refs.add(currentTestRef);
               }
               else if (name.equals("chromatticObject"))
               {
                  reader.next();
                  currentTestRef.getChromatticObject().add(reader.getText());
               }
            }
         }
         return refs;
      }
      catch (Exception e)
      {
         log.error(e.getMessage(),e);
         return null;
      }
   }
}
