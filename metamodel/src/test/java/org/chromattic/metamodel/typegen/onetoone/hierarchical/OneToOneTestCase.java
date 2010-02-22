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

import junit.framework.TestCase;
import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.TypeGen;
import org.reflext.api.ClassTypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToOneTestCase extends TestCase {

  public void testMappedBy() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo a1 = gen.addType(A1.class);
    ClassTypeInfo a2 = gen.addType(A2.class);
    gen.generate();
    NodeType a1NT = gen.getNodeType(a1);
    assertEquals("a1", a1NT.getName());
    NodeType a2NT = gen.getNodeType(a2);
    assertEquals("a2", a2NT.getName());
    assertEquals(Collections.set("child"), a1NT.getChildNodeDefinitions().keySet());
    assertEquals(0, a1NT.getPropertyDefinitions().size());
    assertEquals("a2", a1NT.getChildNodeDefinition("child").getNodeTypeName());
    assertEquals(Collections.<String>set(), a2NT.getChildNodeDefinitions().keySet());
    assertEquals(0, a2NT.getPropertyDefinitions().size());
  }

  public void testRelatedMappedBy() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo b1 = gen.addType(B1.class);
    ClassTypeInfo b2 = gen.addType(B2.class);
    gen.generate();
    NodeType b1NT = gen.getNodeType(b1);
    assertEquals("b1", b1NT.getName());
    NodeType b2NT = gen.getNodeType(b2);
    assertEquals("b2", b2NT.getName());
    assertEquals(Collections.set("child"), b1NT.getChildNodeDefinitions().keySet());
    assertEquals(0, b1NT.getPropertyDefinitions().size());
    assertEquals("b2", b1NT.getChildNodeDefinition("child").getNodeTypeName());
    assertEquals(0, b2NT.getChildNodeDefinitions().size());
    assertEquals(0, b2NT.getPropertyDefinitions().size());
  }

  public void testSelf() throws Exception {
    TypeGen gen = new TypeGen();
    ClassTypeInfo c = gen.addType(C.class);
    gen.generate();
    NodeType cNT = gen.getNodeType(c);
    assertEquals("c", cNT.getName());
    assertEquals(Collections.set("child"), cNT.getChildNodeDefinitions().keySet());
    assertEquals(0, cNT.getPropertyDefinitions().size());
    assertEquals("c", cNT.getChildNodeDefinition("child").getNodeTypeName());
  }
}
