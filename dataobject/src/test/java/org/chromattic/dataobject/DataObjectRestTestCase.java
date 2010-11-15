/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.dataobject;

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DataObjectRestTestCase extends AbstractDataObjectTestCase {


  /** . */
  private static final String dataObjectGroovy =
    "@org.chromattic.api.annotations.PrimaryType(name=\"nt:unstructured\")\n" +
    "class DataObject {\n" +
    "@org.chromattic.api.annotations.Property(name = \"a\") def String a;\n" +
    "}\n";

  /** . */
  private static final String serviceGroovy =
    "@javax.ws.rs.Path(\"a\")\n" +
    "class GroovyService {\n" +
    "  @javax.ws.rs.GET\n" +
    "  def get() {\n" +
    "    def builder = org.chromattic.api.ChromatticBuilder.create();\n" +
    "    builder.add(DataObject.class);\n" +
    "    def chromattic = builder.build();\n" +
    "    org.chromattic.api.ChromatticSession session = chromattic.openSession();\n" +
    "    try {\n" +
    "      def o = session.findByPath(DataObject.class, \"o\");\n" +
    "      return o.a;\n" +
    "    } finally {\n" +
    "      session.close();\n" +
    "    }\n" +
    "  }\n" +
    "  @javax.ws.rs.PUT\n" +
    "  def put() {\n" +
    "    def builder = org.chromattic.api.ChromatticBuilder.create();\n" +
    "    builder.add(DataObject.class);\n" +
    "    def chromattic = builder.build();\n" +
    "    def session = chromattic.openSession();\n" +
    "    try {\n" +
    "      def o = session.insert(DataObject.class, \"o\");\n" +
    "      o.a = \"a_value\";\n" +
    "      session.save();\n" +
    "    } finally {\n" +
    "      session.close();\n" +
    "    }\n" +
    "    return \"done\";\n" +
    "  }\n" +
    "}\n";

  @Override
  protected void setUp() throws Exception {

    super.setUp();

    // Insert
    saveService("script.groovy", serviceGroovy + dataObjectGroovy);
  }

  public void testFoo() throws Exception {

    ContainerResponse resp = launcher.service("PUT", "/a", "", null, null, null, null);
    assertEquals(200, resp.getStatus());

    ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
    ContainerResponse getResp = launcher.service("GET", "/a", "", null, null, writer, null);
    assertEquals(200, getResp.getStatus());
    assertEquals("a_value", new String(writer.getBody()));
  }
}
