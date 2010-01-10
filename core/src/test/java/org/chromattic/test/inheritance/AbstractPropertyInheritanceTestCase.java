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

package org.chromattic.test.inheritance;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.core.ChromatticSessionImpl;

import javax.jcr.Node;
import javax.jcr.Property;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractPropertyInheritanceTestCase<T> extends AbstractTestCase {

  protected void createDomain() {
    addClass(getType());
  }

  protected abstract Class<T> getType();

  protected abstract String getString1(T b);

  protected abstract void setString1(T b, String s);

  protected abstract void setString2(T b, String s);

  protected abstract String getString2(T b);

  protected abstract String[] getStrings1(T b);

  protected abstract void setStrings1(T b, String... s);

  protected abstract String[] getStrings2(T b);

  protected abstract void setStrings2(T b, String... s);

  public void testITFDeclareSingleValuedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    T b = session.insert(getType(), "tii_a");
    setString1(b, "string_value");
    assertEquals("string_value", getString1(b));
    Node rootNode = session.getRoot();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("string1");
    assertEquals("string_value", valueProperty.getString());
  }

  public void testClassOverridesSingleValuedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    T b = session.insert(getType(), "tii_a");
    setString2(b, "string_value");
    assertEquals("string_value", getString2(b));
    Node rootNode = session.getRoot();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("string2");
    assertEquals("string_value", valueProperty.getString());
  }

  public void testITFDeclareMultiValuedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    T b = session.insert(getType(), "tii_a");
    setStrings1(b, "string_value1","string_value2");
    assertEquals(2, getStrings1(b).length);
    assertEquals("string_value1", getStrings1(b)[0]);
    assertEquals("string_value2", getStrings1(b)[1]);
    Node rootNode = session.getRoot();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("strings1");
    assertEquals(2, valueProperty.getValues().length);
    assertEquals("string_value1", valueProperty.getValues()[0].getString());
    assertEquals("string_value2", valueProperty.getValues()[1].getString());
  }

  public void testClassOverridesMultiValuedProperty() throws Exception {
    ChromatticSessionImpl session = login();
    T b = session.insert(getType(), "tii_a");
    setStrings2(b, "string_value1","string_value2");
    assertEquals(2, getStrings2(b).length);
    assertEquals("string_value1", getStrings2(b)[0]);
    assertEquals("string_value2", getStrings2(b)[1]);
    Node rootNode = session.getRoot();
    Node aNode = rootNode.getNode("tii_a");
    assertNotNull(aNode);
    Property valueProperty = aNode.getProperty("strings2");
    assertEquals(2, valueProperty.getValues().length);
    assertEquals("string_value1", valueProperty.getValues()[0].getString());
    assertEquals("string_value2", valueProperty.getValues()[1].getString());
  }
}