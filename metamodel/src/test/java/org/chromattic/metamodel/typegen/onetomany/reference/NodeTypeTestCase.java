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

package org.chromattic.metamodel.typegen.onetomany.reference;

import org.chromattic.common.collection.Collections;
import org.chromattic.metamodel.typegen.AbstractSchemaTestCase;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.PropertyDefinition;

import javax.jcr.PropertyType;
import java.util.Arrays;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeTestCase extends AbstractSchemaTestCase {

  public void testProperty() throws Exception {
    Map<Class<?>, NodeType> map = assertValid(C1.class, C2.class);
    NodeType _1 = map.get(C1.class);
    assertEquals(Collections.<String>set(), _1.getPropertyDefinitions().keySet());
    NodeType _2 = map.get(C2.class);
    assertEquals(Collections.<String>set("ref"), _2.getPropertyDefinitions().keySet());
    PropertyDefinition stringPD = _2.getPropertyDefinition("ref");
    assertEquals("ref", stringPD.getName());
    assertEquals(PropertyType.REFERENCE, stringPD.getType());
    assertEquals(null, stringPD.getDefaultValues());
    assertEquals(Arrays.asList(_1.getName()), stringPD.getValueConstraints());
  }
}