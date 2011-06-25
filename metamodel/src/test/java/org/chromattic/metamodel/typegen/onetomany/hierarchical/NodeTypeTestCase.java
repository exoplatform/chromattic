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

package org.chromattic.metamodel.typegen.onetomany.hierarchical;

import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.AbstractSchemaTestCase;
import org.chromattic.metamodel.typegen.NodeDefinition;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {E1.class, E2.class})
public class NodeTypeTestCase extends AbstractSchemaTestCase {

  public void testPrefix() throws Exception {
    Map<Class<?>, NodeType> map = assertValid(E1.class, E2.class);
    NodeType _1 = map.get(E1.class);
    assertEquals(Collections.<String>set(), _1.getPropertyDefinitions().keySet());
    assertEquals(1, _1.getChildNodeDefinitions().size());
    NodeDefinition cnd = _1.getChildNodeDefinitions().values().iterator().next();
    assertEquals("*", cnd.getName());
    assertEquals("2", cnd.getNodeTypeName());
  }
}
