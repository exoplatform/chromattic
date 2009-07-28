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

package org.chromattic.test.interfaceinheritance;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticSession;

import javax.jcr.Node;
import javax.jcr.Property;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(TII_A_1.class);
  }

  public void testITFDeclareSingleValuedProperty() throws Exception {
    ChromatticSession session = login();
    TII_A_1 b = session.insert(TII_A_1.class, "tii_a");
    b.setString1("string_value");
    assertEquals("string_value", b.getString1());
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("string1");
    assertEquals("string_value", valueProperty.getString());
  }

  public void testClassOverridesSingleValuedProperty() throws Exception {
    ChromatticSession session = login();
    TII_A_1 b = session.insert(TII_A_1.class, "tii_a");
    b.setString2("string_value");
    assertEquals("string_value", b.getString2());
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("string2");
    assertEquals("string_value", valueProperty.getString());
  }

  public void testITFDeclareMultiValuedProperty() throws Exception {
    ChromatticSession session = login();
    TII_A_1 b = session.insert(TII_A_1.class, "tii_a");
    b.setStrings1(new String[]{"string_value1","string_value2"});
    assertEquals(2, b.getStrings1().length);
    assertEquals("string_value1", b.getStrings1()[0]);
    assertEquals("string_value2", b.getStrings1()[1]);
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("strings1");
    assertEquals(2, valueProperty.getValues().length);
    assertEquals("string_value1", valueProperty.getValues()[0].getString());
    assertEquals("string_value2", valueProperty.getValues()[1].getString());
  }

  public void testClassOverridesMultiValuedProperty() throws Exception {
    ChromatticSession session = login();
    TII_A_1 b = session.insert(TII_A_1.class, "tii_a");
    b.setStrings2(new String[]{"string_value1","string_value2"});
    assertEquals(2, b.getStrings2().length);
    assertEquals("string_value1", b.getStrings2()[0]);
    assertEquals("string_value2", b.getStrings2()[1]);
    Node rootNode = session.getJCRSession().getRootNode();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("strings2");
    assertEquals(2, valueProperty.getValues().length);
    assertEquals("string_value1", valueProperty.getValues()[0].getString());
    assertEquals("string_value2", valueProperty.getValues()[1].getString());
  }
}
