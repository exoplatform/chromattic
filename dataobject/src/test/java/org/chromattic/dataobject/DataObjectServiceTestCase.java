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

import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import javax.jcr.Session;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DataObjectServiceTestCase extends AbstractDataObjectTestCase {

  /** . */
  private static final String dataObjectGroovy =
      "@org.chromattic.api.annotations.PrimaryType(name=\"nt:unstructured\")\n" +
      "class DataObject {\n" +
      "@org.chromattic.api.annotations.Property(name = \"a\") def String a;\n" +
      "}";

  /** . */
  private final DataObjectService service = new DataObjectService();

  @Override
  protected void setUp() throws Exception {

    super.setUp();

    // Insert data object
    saveDataObject("DataObject.groovy", dataObjectGroovy);
  }

  public void testNodeTypeGenration() throws Exception {
    String s = service.generateNodeTypes(NodeTypeFormat.EXO, "db1", "ws", "/", "/DataObject.groovy");
    System.out.println("Generated node types " + s);
  }

  public void testCompilation() throws Exception {
    Iterator<Class<?>> classes = service.generateClasses("db1", "ws", "/", "/DataObject.groovy").values().iterator();
    assertTrue(classes.hasNext());
    Class<?> dataObjectClass = classes.next();
    assertFalse(classes.hasNext());

    //
    ChromatticBuilder builder = ChromatticBuilder.create();
    builder.add(dataObjectClass);
    Chromattic chromattic = builder.build();

    //
    ChromatticSession session = chromattic.openSession();
    try {
      Object dataObject = session.insert(dataObjectClass, "dataobject");
      Method setter = dataObject.getClass().getMethod("setA", String.class);
      setter.invoke(dataObject, "a_value");
      session.save();
    }
    finally {
      session.close();
    }

    //
    Session jcrSession = bootstrap.getRepository().login();
    Node dataObjectNode = jcrSession.getRootNode().getNode("dataobject");
    assertNotNull(dataObjectNode);
//    assertEquals("a_value", dataObjectNode.getProperty("a").getString());
  }
}
