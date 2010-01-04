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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeInfoManager {

  /** . */
  private final Object nodeTypeInfosLock = new Object();

  /** . */
  private volatile Map<String, NodeTypeInfo> nodeTypeInfos = new HashMap<String, NodeTypeInfo>();

  public boolean isReferenceable(Node node) throws RepositoryException {

    //
    for (NodeType nt : node.getMixinNodeTypes()) {
      if (nt.getName().equals("mix:referenceable")) {
        return true;
      }
    }

    //
    PrimaryTypeInfo ntInfo = (PrimaryTypeInfo)getTypeInfo(node.getPrimaryNodeType());

    //
    return ntInfo.getMixinNames().contains("mix:referenceable");
  }

  public NodeInfo getInfo(Node node) throws RepositoryException {
    NodeType primaryNodeType = node.getPrimaryNodeType();
    PrimaryTypeInfo primaryTypeInfo = (PrimaryTypeInfo)getTypeInfo(primaryNodeType);
    return new NodeInfo(this, node, primaryTypeInfo);
  }

  public PrimaryTypeInfo getPrimaryTypeInfo(NodeType primaryType) throws RepositoryException {
    return (PrimaryTypeInfo)getTypeInfo(primaryType);
  }

  public MixinTypeInfo getMixinTypeInfo(NodeType mixinType) throws RepositoryException {
    return (MixinTypeInfo)getTypeInfo(mixinType);
  }

  private NodeTypeInfo getTypeInfo(NodeType nodeType) {
    String nodeTypeName = nodeType.getName();
    NodeTypeInfo nodeTypeInfo = nodeTypeInfos.get(nodeTypeName);
    if (nodeTypeInfo == null) {

      // Compute
      if (nodeType.isMixin()) {
        nodeTypeInfo = new MixinTypeInfo(nodeType);
      } else {
        Set<NodeTypeInfo> superTypes = new HashSet<NodeTypeInfo>();
        for (NodeType superType : nodeType.getSupertypes()) {
          NodeTypeInfo superTIs = getTypeInfo(superType);
          superTypes.add(superTIs);
        }
        nodeTypeInfo = new PrimaryTypeInfo(nodeType, Collections.unmodifiableSet(superTypes));
      }

      // Add
      synchronized (nodeTypeInfosLock) {
        Map<String, NodeTypeInfo> copy = new HashMap<String, NodeTypeInfo>(nodeTypeInfos);
        copy.put(nodeTypeName, nodeTypeInfo);
        nodeTypeInfos = copy;
      }
    }
    return nodeTypeInfo;
  }
}
