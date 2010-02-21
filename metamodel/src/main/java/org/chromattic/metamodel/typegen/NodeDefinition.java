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

package org.chromattic.metamodel.typegen;

import org.chromattic.metamodel.mapping.NodeTypeMapping;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeDefinition {

  /** . */
  private final String name;

  /** . */
  final Set<NodeTypeMapping> mappings;

  public NodeDefinition(String name) {
    this.name = name;
    this.mappings = new HashSet<NodeTypeMapping>();
  }

  public String getName() {
    return name;
  }

  public String getNodeTypeName() {
    // Try to find the common ancestor type of all types
    NodeTypeMapping ancestorMapping = null;
    foo:
    for (NodeTypeMapping relatedMapping1 : mappings) {
      for (NodeTypeMapping relatedMapping2 : mappings) {
        if (!relatedMapping1.getType().isAssignableFrom(relatedMapping2.getType())) {
          continue foo;
        }
      }
      ancestorMapping = relatedMapping1;
      break;
    }

    //
    if (ancestorMapping == null) {
      return "nt:base";
    } else {
      return ancestorMapping.getTypeName();
    }
  }
}
