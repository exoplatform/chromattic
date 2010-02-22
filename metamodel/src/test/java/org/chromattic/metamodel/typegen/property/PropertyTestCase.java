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

import junit.framework.TestCase;
import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.PropertyDefinition;
import org.chromattic.metamodel.typegen.TypeGen;
import org.chromattic.metamodel.typegen.onetoone.hierarchical.A1;
import org.chromattic.metamodel.typegen.onetoone.hierarchical.A2;
import org.chromattic.metamodel.typegen.onetoone.hierarchical.B1;
import org.chromattic.metamodel.typegen.onetoone.hierarchical.B2;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyTestCase extends TestCase {

  public void testProperty() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo a = gen.addType(A.class);
    gen.generate();
    NodeType aNT = gen.getNodeType(a);
    assertEquals(Collections.<String>set("string"), aNT.getPropertyDefinitions().keySet());
    PropertyDefinition stringPD = aNT.getPropertyDefinition("string");
    assertEquals("string", stringPD.getName());
    assertEquals(PropertyType.STRING, stringPD.getType());
    assertEquals(null, stringPD.getDefaultValues());
  }

  public void testDefaultValues() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo b = gen.addType(B.class);
    gen.generate();
    NodeType bNT = gen.getNodeType(b);
    assertEquals(Collections.<String>set("string"), bNT.getPropertyDefinitions().keySet());
    PropertyDefinition stringPD = bNT.getPropertyDefinition("string");
    assertEquals("string", stringPD.getName());
    assertEquals(PropertyType.STRING, stringPD.getType());
    assertEquals(Collections.list("foo"), stringPD.getDefaultValues());
  }
}