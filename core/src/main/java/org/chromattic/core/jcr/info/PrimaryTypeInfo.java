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
package org.chromattic.core.jcr.info;

import javax.jcr.nodetype.NodeType;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PrimaryTypeInfo extends NodeTypeInfo
{

  /** . */
  private Set<String> mixinNames;

  /** . */
  private final Set<NodeTypeInfo> superTypes;

  /** . */
  private final Map<String, NodeTypeInfo> superTypesMap;

  /** . */
  private final boolean readAhead;

  public PrimaryTypeInfo(NodeType nodeType, Set<NodeTypeInfo> superTypes) {
    super(nodeType);

    //
    if (nodeType.isMixin()) {
      throw new IllegalArgumentException();
    }

    //
    Set<String> mixinNames = new HashSet<String>();
    for (NodeType superType : nodeType.getSupertypes()) {
      if (superType.isMixin()) {
        mixinNames.add(superType.getName());
      }
    }

    //
    Map<String, NodeTypeInfo> superTypesMap = new HashMap<String, NodeTypeInfo>();
    for (NodeTypeInfo superType : superTypes) {
      superTypesMap.put(superType.getName(), superType);
    }

    //
    boolean readAhead = "true".equals(System.getProperty(nodeType.getName(), "false"));

    //
    this.mixinNames = Collections.unmodifiableSet(mixinNames);
    this.superTypes = superTypes;
    this.superTypesMap = Collections.unmodifiableMap(superTypesMap);
    this.readAhead = readAhead;
  }

  public boolean isReadAhead() {
    return readAhead;
  }

  public Set<String> getSuperTypeNames() {
    return superTypesMap.keySet();
  }

  public NodeTypeInfo getSuperType(String name) {
    return superTypesMap.get(name);
  }

  public Set<NodeTypeInfo> getSuperTypes() {
    return superTypes;
  }

  public Set<String> getMixinNames() {
    return mixinNames;
  }
}
