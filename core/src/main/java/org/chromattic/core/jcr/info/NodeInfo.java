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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeInfo {

  /** . */
  private final NodeInfoManager manager;

  /** . */
  private final Node node;

  /** . */
  private final PrimaryTypeInfo primaryNodeTypeInfo;

  protected NodeInfo(NodeInfoManager manager, Node node, PrimaryTypeInfo primaryNodeTypeInfo) {
    this.manager = manager;
    this.node = node;
    this.primaryNodeTypeInfo = primaryNodeTypeInfo;
  }

  public Node getNode() {
    return node;
  }

  public PrimaryTypeInfo getPrimaryNodeTypeInfo() {
    return primaryNodeTypeInfo;
  }

  public PropertyDefinitionInfo getPropertyDefinitionInfo(String name) {
    return primaryNodeTypeInfo.getPropertyDefinitionInfo(name);
  }

  public PropertyDefinitionInfo findPropertyDefinition(String propertyName) throws RepositoryException {
    PropertyDefinitionInfo propertyDefinitionInfo = getPropertyDefinitionInfo(propertyName);

    // Should we also try residual definition for mixins ??????
    if (propertyDefinitionInfo == null) {
      return getPropertyDefinitionInfo("*");
    }

    //
    return propertyDefinitionInfo;
  }
}
