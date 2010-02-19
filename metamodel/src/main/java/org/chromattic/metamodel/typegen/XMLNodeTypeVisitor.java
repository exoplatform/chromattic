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

import javax.jcr.PropertyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class XMLNodeTypeVisitor implements NodeTypeVisitor {

  final StringBuilder nodetypes = new StringBuilder();

  static final int PROPERTIES_OPEN = 0;

  static final int CHILDREN_OPEN = 1;

  int status;

  public XMLNodeTypeVisitor() {
    nodetypes.append("<nodeTypes xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:dc=\"http://purl.org/dc/elements/1.1\">\n");
  }

  public void startType(String name, boolean primary) {
    nodetypes.append("<nodeType name=\"").append(name).append(" isMixin=").append(!primary).append("\" hasOrderableChildNodes=\"false\" primaryItemName=\"\">\n");
    nodetypes.append("<propertyDefinitions>\n");
    status = PROPERTIES_OPEN;
  }

  public void addProperty(String propertyName, boolean multiple, int propertyType) {
    nodetypes.append("<propertyDefinition name=\"").append(propertyName).append("\" requiredType=\"").
      append(PropertyType.nameFromValue(propertyType)).append("\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"COPY\" protected=\"false\" multiple=\"true\">\n").
      append("<valueConstraints/>\n").append("</propertyDefinition>\n");
  }

  public void addChildNodeDefinition(String childName, String nodeTypeName) {
    if (status == PROPERTIES_OPEN) {
      status = CHILDREN_OPEN;
      nodetypes.append("</propertyDefinitions>\n");
      nodetypes.append("<childNodeDefinitions>\n");
    }
    nodetypes.append("<childNodeDefinition name=\"").append(childName).append("\" defaultPrimaryType=\"nt:unstructured\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"VERSION\" protected=\"false\" sameNameSiblings=\"false\">\n");
    nodetypes.append("<requiredPrimaryTypes>\n");
    nodetypes.append("<requiredPrimaryType>").append(nodeTypeName).append("</requiredPrimaryType>\n");
    nodetypes.append("</requiredPrimaryTypes>\n");
    nodetypes.append("</childNodeDefinition>\n");
  }

  public void endType() {
    if (status == PROPERTIES_OPEN) {
      nodetypes.append("</propertyDefinitions>\n");
    }
    else if (status == CHILDREN_OPEN) {
      nodetypes.append("</childNodeDefinitions>\n");
    }
    nodetypes.append("</nodeType>\n");
  }

  @Override
  public String toString() {
    nodetypes.append("</nodeTypes>");
    return nodetypes.toString();
  }
}
