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
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import java.util.*;

public class JCRSchema implements Schema {

  private static class Resolver {

    /** . */
    private final NodeTypeManager mgr;

    /** . */
    private final LinkedHashMap<String, JCRObjectType> types;

    private Resolver(NodeTypeManager mgr) {
      this.mgr = mgr;
      this.types = new LinkedHashMap<String, JCRObjectType>();
    }

    private void resolve() throws RepositoryException {
      for (NodeTypeIterator it = mgr.getAllNodeTypes();it.hasNext();) {
        NodeType nodeType = it.nextNodeType();
        resolve(nodeType.getName());
      }
    }

    private void resolve(Set<String> names) throws RepositoryException {
      for (String name : names) {
        resolve(name);
      }
    }

    private ObjectType resolve(String name) throws RepositoryException {
      JCRObjectType resolved = types.get(name);
      if (resolved == null) {
        NodeType nodeType = mgr.getNodeType(name);

        //
        if (nodeType.isMixin()) {
          resolved = new JCRMixinType(name);
        } else {
          resolved = new JCREntityType(name);
        }
        types.put(name, resolved);

        //
        List<JCRExtendsRelationship> extendsRelationships = Collections.emptyList();
        for (NodeType superNodeType : nodeType.getDeclaredSupertypes()) {
          ObjectType superType = resolve(superNodeType.getName());
          if (extendsRelationships.isEmpty()) {
            extendsRelationships = new ArrayList<JCRExtendsRelationship>();
          }
          extendsRelationships.add(new JCRExtendsRelationship(resolved, superType));
        }
        resolved.extendsRelationships = extendsRelationships;

        //
        List<JCRHierarchicalRelationship> childrenRelationships = Collections.emptyList();
        NodeDefinition[] defs = nodeType.getDeclaredChildNodeDefinitions();
        for (NodeDefinition def : defs) {
          ObjectType childType = resolve(def.getRequiredPrimaryTypes()[0].getName());
          JCRHierarchicalRelationship relationship = new JCRHierarchicalRelationship(
              resolved,
              childType,
              def.getName()
          );
          if (childrenRelationships.isEmpty()) {
            childrenRelationships = new ArrayList<JCRHierarchicalRelationship>();
          }
          childrenRelationships.add(relationship);
        }
        resolved.childrenRelationships = childrenRelationships;
      }

      //
      return resolved;
    }
  }

  public static JCRSchema build(NodeTypeManager mgr) throws RepositoryException {
    Resolver resolver = new Resolver(mgr);
    resolver.resolve();
    return new JCRSchema(new ArrayList<JCRObjectType>(resolver.types.values()));
  }

  public static JCRSchema build(NodeTypeManager mgr, Set<String> names) throws RepositoryException {
    Resolver resolver = new Resolver(mgr);
    resolver.resolve(names);
    return new JCRSchema(new ArrayList<JCRObjectType>(resolver.types.values()));
  }

  /** . */
  private final LinkedHashMap<String, JCRObjectType> types;

  private JCRSchema(ArrayList<JCRObjectType> types) {
    LinkedHashMap<String, JCRObjectType> tmp = new LinkedHashMap<String, JCRObjectType>();
    for (JCRObjectType type : types) {
      tmp.put(type.getName(), type);
    }

    //
    this.types = tmp;
  }

  public Collection<? extends ObjectType> getTypes() {
    return types.values();
  }

  public ObjectType getType(String name) throws NullPointerException {
    if (name == null) {
      throw new NullPointerException("No null name accepted");
    }
    return types.get(name);
  }
}
