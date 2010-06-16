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

package org.chromattic.metamodel.typegen.properties;

import junit.framework.TestCase;import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.PropertyDefinition;
import org.chromattic.metamodel.typegen.TypeGen;
import org.chromattic.metamodel.typegen.TypeGenTestCase;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertiesTestCase extends TypeGenTestCase {

  public void testStringProperties() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(A.class);
    NodeType aNT = a.get(A.class);
    assertEquals(Collections.<String>set("*"), aNT.getPropertyDefinitions().keySet());
    PropertyDefinition pd = aNT.getPropertyDefinition("*");
    assertEquals("*", pd.getName());
    // assertEquals(PropertyType.STRING, pd.getType());
    assertEquals(null, pd.getDefaultValues());
  }

  public void testObjectProperties() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(B.class);
    NodeType bNT = a.get(B.class);
    assertEquals(Collections.<String>set("*"), bNT.getPropertyDefinitions().keySet());
    PropertyDefinition pd = bNT.getPropertyDefinition("*");
    assertEquals("*", pd.getName());
    assertEquals(PropertyType.UNDEFINED, pd.getType());
    assertEquals(null, pd.getDefaultValues());
  }

  public void testAnyProperties() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(C.class);
    NodeType cNT = a.get(C.class);
    assertEquals(Collections.<String>set("*"), cNT.getPropertyDefinitions().keySet());
    PropertyDefinition pd = cNT.getPropertyDefinition("*");
    assertEquals("*", pd.getName());
    assertEquals(PropertyType.UNDEFINED, pd.getType());
    assertEquals(null, pd.getDefaultValues());
  }
}