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
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeInfoManager {

  /** . */
  private final Object primaryNodeInfosLock = new Object();

  /** . */
  private volatile Map<String, NodeInfo> primaryNodeInfos = new HashMap<String, NodeInfo>();

  /** . */
  private final Object nodeTypeInfosLock = new Object();

  /** . */
  private volatile Map<String, TypeInfo> nodeTypeInfos = new HashMap<String, TypeInfo>();

  public boolean isReferenceable(Node node) throws RepositoryException {

    //
    for (NodeType nt : node.getMixinNodeTypes()) {
      if (nt.getName().equals("mix:referenceable")) {
        return true;
      }
    }

    //
    PrimartyNodeTypeInfo ntInfo = (PrimartyNodeTypeInfo)getTypeInfo(node.getPrimaryNodeType());

    //
    return ntInfo.getMixinNames().contains("mix:referenceable");
  }

  public NodeInfo getNodeInfo(Node node) throws RepositoryException {

    //
    NodeType primaryNodeType = node.getPrimaryNodeType();
    String primaryNodeTypeName = primaryNodeType.getName();
    NodeType[] mixinNodeTypes = node.getMixinNodeTypes();

    //
    if (mixinNodeTypes.length == 0 || (mixinNodeTypes.length == 1 && mixinNodeTypes[0].getName().equals("mix:referenceable"))) {

      //
      NodeInfo nodeInfo = primaryNodeInfos.get(primaryNodeTypeName);
      if (nodeInfo == null) {
        synchronized (primaryNodeInfosLock) {
          PrimartyNodeTypeInfo primaryNodeTypeInfo = (PrimartyNodeTypeInfo)getTypeInfo(primaryNodeType);
          nodeInfo = new NodeInfo(primaryNodeTypeInfo);
          Map<String, NodeInfo> copy = new HashMap<String, NodeInfo>(primaryNodeInfos);
          copy.put(primaryNodeTypeName, nodeInfo);
          primaryNodeInfos = copy;
        }
      }
      return nodeInfo;
    } else {
      throw new UnsupportedOperationException("todo");
    }
  }

  private TypeInfo getTypeInfo(NodeType nodeType) {
    String nodeTypeName = nodeType.getName();
    TypeInfo nodeTypeInfo = nodeTypeInfos.get(nodeTypeName);
    if (nodeTypeInfo == null) {
      synchronized (nodeTypeInfosLock) {
        if (nodeType.isMixin()) {
          nodeTypeInfo = new MixinNodeTypeInfo(nodeType);
        } else {
          nodeTypeInfo = new PrimartyNodeTypeInfo(nodeType);
        }
        Map<String, TypeInfo> copy = new HashMap<String, TypeInfo>(nodeTypeInfos);
        copy.put(nodeTypeName, nodeTypeInfo);
        nodeTypeInfos = copy;
      }
    }
    return nodeTypeInfo;
  }
}
