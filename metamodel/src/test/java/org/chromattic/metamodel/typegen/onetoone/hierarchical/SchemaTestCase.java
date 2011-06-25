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

package org.chromattic.metamodel.typegen.onetoone.hierarchical;

import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.AbstractSchemaTestCase;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.TypeGen;
import org.chromattic.testgenerator.GroovyTestGeneration;
import org.reflext.api.ClassTypeInfo;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {A1.class, A2.class, B1.class, B2.class, E1.class, E2.class, D.class})
public class SchemaTestCase extends AbstractSchemaTestCase {

  public void testMappedBy() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(A1.class, A2.class);
    NodeType a1NT = a.get(A1.class);
    assertEquals("1", a1NT.getName());
    NodeType a2NT = a.get(A2.class);
    assertEquals("2", a2NT.getName());
    assertEquals(Collections.set("child"), a1NT.getChildNodeDefinitions().keySet());
    assertEquals(0, a1NT.getPropertyDefinitions().size());
    assertEquals("2", a1NT.getChildNodeDefinition("child").getNodeTypeName());
    assertEquals(false, a1NT.getChildNodeDefinition("child").isMandatory());
    assertEquals(Collections.<String>set(), a2NT.getChildNodeDefinitions().keySet());
    assertEquals(0, a2NT.getPropertyDefinitions().size());
  }

  public void testRelatedMappedBy() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(B1.class, B2.class);
    NodeType b1NT = a.get(B1.class);
    assertEquals("1", b1NT.getName());
    NodeType b2NT = a.get(B2.class);
    assertEquals("2", b2NT.getName());
    assertEquals(Collections.set("child"), b1NT.getChildNodeDefinitions().keySet());
    assertEquals(0, b1NT.getPropertyDefinitions().size());
    assertEquals("2", b1NT.getChildNodeDefinition("child").getNodeTypeName());
    assertEquals(false, b1NT.getChildNodeDefinition("child").isMandatory());
    assertEquals(0, b2NT.getChildNodeDefinitions().size());
    assertEquals(0, b2NT.getPropertyDefinitions().size());
  }

  public void testOptions() throws Exception {
    Map<Class<?>, NodeType> a = assertValid(E1.class, E2.class);
    NodeType c1NT = a.get(E1.class);
    assertEquals("1", c1NT.getName());
    NodeType c2NT = a.get(E2.class);
    assertEquals("2", c2NT.getName());

    //
    assertEquals(Collections.<String>set(), c1NT.getPropertyDefinitions().keySet());
    assertEquals(Collections.set("child1", "child2"), c1NT.getChildNodeDefinitions().keySet());
    assertEquals("2", c1NT.getChildNodeDefinition("child1").getNodeTypeName());
    assertEquals(true, c1NT.getChildNodeDefinition("child1").isMandatory());
    assertEquals(false, c1NT.getChildNodeDefinition("child1").isAutocreated());
    assertEquals("2", c1NT.getChildNodeDefinition("child2").getNodeTypeName());
    assertEquals(false, c1NT.getChildNodeDefinition("child2").isMandatory());
    assertEquals(true, c1NT.getChildNodeDefinition("child2").isAutocreated());

    //
    assertEquals(Collections.<String>set(), c2NT.getPropertyDefinitions().keySet());
    assertEquals(Collections.<String>set(), c2NT.getChildNodeDefinitions().keySet());
  }

  public void testSelf() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo c = gen.addType(D.class);
    gen.generate();
    NodeType cNT = gen.getNodeType(c);
    assertEquals("1", cNT.getName());
    assertEquals(Collections.set("child"), cNT.getChildNodeDefinitions().keySet());
    assertEquals(0, cNT.getPropertyDefinitions().size());
    assertEquals("1", cNT.getChildNodeDefinition("child").getNodeTypeName());
    assertEquals(false, cNT.getChildNodeDefinition("child").isMandatory());
  }
}
