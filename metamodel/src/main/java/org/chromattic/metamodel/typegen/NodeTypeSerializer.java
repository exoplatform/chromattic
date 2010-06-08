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

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class NodeTypeSerializer {

  /** . */
  private final List<NodeType> nodeTypes;

  public NodeTypeSerializer(List<NodeType> nodeTypes) {
    if (nodeTypes == null) {
      throw new NullPointerException();
    }
    this.nodeTypes = nodeTypes;
  }

  public abstract void writeTo(Writer writer) throws IOException;

  public void writeTo() throws SAXException {
    startNodeTypes();

    //
    for (NodeType nodeType : nodeTypes) {
      LinkedHashSet<String> superTypeNames = new LinkedHashSet<String>();

      //
      if (nodeType.declaredSuperTypes.isEmpty()) {
        superTypeNames.add("nt:base");
      }
      for (NodeType superType : nodeType.declaredSuperTypes) {
        superTypeNames.add(superType.getName());
      }
      // Add nt:base and mix:referenceable
      superTypeNames.add("mix:referenceable");

      //
      startNodeType(
        nodeType.mapping.getType().getName(),
        nodeType.getName(),
        nodeType.isMixin(),
        nodeType.isOrderable(),
        superTypeNames
      );

      //
      startProperties();

      //
      for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions().values()) {
        property(
          propertyDefinition.getName(),
          propertyDefinition.getType(),
          propertyDefinition.isMultiple(),
          propertyDefinition.getDefaultValues()
        );
      }

      //
      endProperties();

      //
      startChildNodes();

      //
      for (NodeDefinition childNodeDefinition : nodeType.getChildNodeDefinitions().values()) {
        childNode(
          childNodeDefinition.getName(),
          childNodeDefinition.getNodeTypeName()
        );
      }

      //
      endChildNodes();

      //
      endNodeType();
    }

    //
    endNodeTypes();
  }

  public void startNodeTypes() throws SAXException {
  }

  public void startNodeType(
    String javaClassName,
    String name,
    boolean mixin,
    boolean orderableChildNodes,
    Collection<String> superTypeNames) throws SAXException {
  }

  public void startProperties() throws SAXException {
  }

  public void property(
    String name,
    int requiredType,
    boolean multiple,
    Collection<String> defaultValues) throws SAXException {
  }

  public void endProperties() throws SAXException {
  }

  public void startChildNodes() throws SAXException {
  }

  public void childNode(
    String name,
    String nodeTypeName) throws SAXException {
  }

  public void endChildNodes() throws SAXException {
  }

  public void endNodeType() throws SAXException {
  }

  public void endNodeTypes() throws SAXException {
  }
}
