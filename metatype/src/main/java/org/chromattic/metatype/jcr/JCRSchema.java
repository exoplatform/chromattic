/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.metatype.jcr;

import org.chromattic.metatype.ObjectType;
import org.chromattic.metatype.Schema;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import java.util.ArrayList;
import java.util.Collection;

public class JCRSchema implements Schema {

  public static JCRSchema build(NodeTypeManager mgr) throws RepositoryException {
    NodeTypeIterator it = mgr.getAllNodeTypes();
    ArrayList<JCRObjectType> types = new ArrayList<JCRObjectType>();
    while (it.hasNext()) {
      NodeType nodeType = it.nextNodeType();
      String name = nodeType.getName();
      JCRObjectType type;
      if (nodeType.isMixin()) {
        type = new JCRMixinType(name);
      } else {
        type = new JCREntityType(name);
      }
      types.add(type);
    }
    return new JCRSchema(types);
  }

  /** . */
  private final ArrayList<JCRObjectType> types;

  private JCRSchema(ArrayList<JCRObjectType> types) {
    this.types = types;
  }

  public Collection<? extends ObjectType> getTypes() {
    return types;
  }
}
