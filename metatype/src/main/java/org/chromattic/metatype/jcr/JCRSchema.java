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

import org.chromattic.common.Safe;
import org.chromattic.metatype.ObjectType;
import org.chromattic.metatype.Schema;
import org.chromattic.metatype.ValueType;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.*;
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

      // Resolve each type
      for (NodeTypeIterator it = mgr.getAllNodeTypes();it.hasNext();) {
        NodeType nodeType = it.nextNodeType();
        resolve(nodeType.getName());
      }

      // Resolve ancestors
      for (JCRObjectType type : types.values()) {
        HashSet<JCRObjectType> ancestorTypes = new HashSet<JCRObjectType>();
        ancestors(type, ancestorTypes);
        type.ancestors = ancestorTypes;
      }
    }

    private void ancestors(JCRObjectType type, Set<JCRObjectType> set) {
      for (JCRInheritanceRelationshipDescriptor superRelationship : type.superRelationships.values()) {
        set.add(superRelationship.destination);
        ancestors(superRelationship.destination, set);
      }
    }

    private void resolve(Set<String> names) throws RepositoryException {
      for (String name : names) {
        resolve(name);
      }
    }

    private static final Map<Integer, ValueType> foo = new HashMap<Integer, ValueType>();

    static
    {
      foo.put(PropertyType.STRING, ValueType.STRING);
      foo.put(PropertyType.NAME, ValueType.STRING);
      foo.put(PropertyType.LONG, ValueType.LONG);
      foo.put(PropertyType.BOOLEAN, ValueType.BOOLEAN);
      foo.put(PropertyType.DATE, ValueType.DATE);
      foo.put(PropertyType.DOUBLE, ValueType.DOUBLE);
      foo.put(PropertyType.BINARY, ValueType.BINARY);
      foo.put(PropertyType.UNDEFINED, ValueType.ANY);
    }

    private JCRObjectType resolve(String name) throws RepositoryException {
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
        Map<String, JCRInheritanceRelationshipDescriptor> superRelationships = null;
        for (NodeType superNodeType : nodeType.getDeclaredSupertypes()) {
          JCRObjectType superType = resolve(superNodeType.getName());
          if (superRelationships == null) {
            superRelationships = new LinkedHashMap<String, JCRInheritanceRelationshipDescriptor>();
          }
          superRelationships.put(superNodeType.getName(), new JCRInheritanceRelationshipDescriptor(resolved, superType));
        }

        //
        Map<String, JCRInheritanceRelationshipDescriptor> superEntityRelationships = null;
        Map<String, JCRInheritanceRelationshipDescriptor> superMixinRelationships = null;
        if (superRelationships != null)
        {
          for (Map.Entry<String, JCRInheritanceRelationshipDescriptor> entry : superRelationships.entrySet())
          {
            if (entry.getValue().getDestination() instanceof JCREntityType)
            {
              if (superEntityRelationships == null)
              {
                superEntityRelationships = new HashMap<String, JCRInheritanceRelationshipDescriptor>();
              }
              superEntityRelationships.put(entry.getKey(), entry.getValue());
            }
            else
            {
              if (superMixinRelationships == null)
              {
                superMixinRelationships = new HashMap<String, JCRInheritanceRelationshipDescriptor>();
              }
              superMixinRelationships.put(entry.getKey(), entry.getValue());
            }
          }
        }

        //
        resolved.superRelationships = Safe.unmodifiable(superRelationships);
        resolved.superEntityRelationships = Safe.unmodifiable(superEntityRelationships);
        resolved.superMixinRelationships = Safe.unmodifiable(superMixinRelationships);

        //
        Map<String, JCRHierarchicalRelationshipDescriptor> childrenRelationships = null;
        NodeDefinition[] defs = nodeType.getDeclaredChildNodeDefinitions();
        for (NodeDefinition def : defs) {
          JCRObjectType childType = resolve(def.getRequiredPrimaryTypes()[0].getName());
          JCRHierarchicalRelationshipDescriptor relationship = new JCRHierarchicalRelationshipDescriptor(
              resolved,
              childType,
              def.getName()
          );
          if (childrenRelationships == null) {
            childrenRelationships = new LinkedHashMap<String, JCRHierarchicalRelationshipDescriptor>();
          }
          childrenRelationships.put(def.getName(), relationship);
        }
        resolved.childrenRelationships = Safe.unmodifiable(childrenRelationships);

        //
        Map<String, JCRPropertyDescriptor> properties = null;
        for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions()) {
          String propertyName = propertyDefinition.getName();
          int propertyType = propertyDefinition.getRequiredType();
          switch (propertyType) {
            case PropertyType.BINARY:
            case PropertyType.BOOLEAN:
            case PropertyType.DATE:
            case PropertyType.STRING:
            case PropertyType.LONG:
            case PropertyType.DOUBLE:
            case PropertyType.UNDEFINED:
            case PropertyType.NAME:
              if (properties == null) {
                properties = new LinkedHashMap<String, JCRPropertyDescriptor>();
              }
              ValueType valueType = foo.get(propertyType);
              if (valueType == null)
              {
                throw new UnsupportedOperationException("Unsupported property type " + propertyType);
              }
              boolean singleValued = !propertyDefinition.isMultiple();
              JCRPropertyDescriptor property = new JCRPropertyDescriptor(propertyName, valueType, singleValued);
              properties.put(propertyName, property);
              break;
            case PropertyType.REFERENCE:
            case PropertyType.PATH:
              // To do relationships by reference or path
              break;
            default:
              // It may be an internal property (exo permission type), we skip it
              break;
          }
        }
        resolved.properties = Safe.unmodifiable(properties);
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
