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

package org.chromattic.metamodel.typegen.property;

import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.PropertyDefinition;
import org.chromattic.metamodel.typegen.TypeGenTestCase;

import javax.jcr.PropertyType;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyTestCase extends TypeGenTestCase {

  public void testProperty() throws Exception { testProperty(A1.class); }
  public void testDefaultValues() throws Exception { testDefaultValues(A2.class); }

  protected void testProperty(Class<?> clazz) throws Exception {
    Map<Class<?>, NodeType> a = assertValid(clazz);
    NodeType aNT = a.get(clazz);
    assertEquals(Collections.<String>set("string"), aNT.getPropertyDefinitions().keySet());
    PropertyDefinition stringPD = aNT.getPropertyDefinition("string");
    assertEquals("string", stringPD.getName());
    assertEquals(PropertyType.STRING, stringPD.getType());
    assertEquals(null, stringPD.getDefaultValues());
  }

  protected void testDefaultValues(Class<?> clazz) throws Exception {
    Map<Class<?>, NodeType> a = assertValid(clazz);
    NodeType bNT = a.get(clazz);
    assertEquals(Collections.<String>set("string"), bNT.getPropertyDefinitions().keySet());
    PropertyDefinition stringPD = bNT.getPropertyDefinition("string");
    assertEquals("string", stringPD.getName());
    assertEquals(PropertyType.STRING, stringPD.getType());
    assertEquals(Collections.list("foo"), stringPD.getDefaultValues());
  }
}