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

package org.chromattic.test.format.transform.property;

import org.chromattic.api.ChromatticBuilder;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.format.FooPrefixerFormatter;
import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A.class})
public class PropertyNameTransformTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    getBuilder().setOptionValue(ChromatticBuilder.OBJECT_FORMATTER_CLASSNAME, FooPrefixerFormatter.class.getName());
    addClass(A.class);
  }

  public void testGetSetProperty() throws RepositoryException {
/*
    DomainSession session = login();

    //
    A a = session.insert(A.class, "a");
    String aId = session.getId(a);
    a.setString("string_value_1");
    Node aNode = session.getNode(a);
    Property prop = aNode.getProperty("foo_a");
    assertEquals("string_value_1", prop.getString());
    prop.setValue("string_value_2");
    session.save();

    //
    session = login();
    a = session.findById(A.class, aId);
    assertEquals("string_value_2", a.getString());
*/
  }

  public void testPropertyMap() throws RepositoryException {
/*
    DomainSession session = login();
    A a = session.insert(A.class, "a");
    a.setString("string_value_1");

    //
    Map<String, Object> props = a.getProperties();
    assertEquals(Collections.set("a"), props.keySet());
*/
  }
}
