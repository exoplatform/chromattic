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

import org.chromattic.common.xml.ContentWriter;
import org.chromattic.common.xml.ElementWriter;
import org.chromattic.metamodel.mapping.*;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.reflext.api.ClassTypeInfo;
import org.xml.sax.ContentHandler;

import javax.jcr.PropertyType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeBuilder extends BaseTypeMappingVisitor {

  /** . */
  private final Map<ClassTypeInfo, NodeType> nodeTypes;

  /** . */
  private NodeType current;

  public NodeTypeBuilder() {
    this.nodeTypes = new HashMap<ClassTypeInfo, NodeType>();
  }

  public NodeType getNodeType(ClassTypeInfo type) {
    return nodeTypes.get(type);
  }

  private NodeType resolve(NodeTypeMapping mapping) {
    NodeType nodeType = nodeTypes.get(mapping.getType());
    if (nodeType == null) {
      nodeType = new NodeType(mapping);
      nodeTypes.put(mapping.getType(), nodeType);
    }
    return nodeType;
  }

  public void start() {
    nodeTypes.clear();
  }

  @Override
  protected void startMapping(NodeTypeMapping mapping) {
    current = resolve(mapping);
  }

  @Override
  protected <V> void propertyMapping(ClassTypeInfo definer, JCRPropertyMapping<V> propertyMapping, PropertyInfo<SimpleValueInfo<V>> propertyInfo) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(propertyMapping.getName(), new PropertyDefinition(propertyMapping, propertyInfo));
    }
  }

  @Override
  protected void propertyMapMapping(ClassTypeInfo definer) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put("*", new PropertyDefinition("*", false, PropertyType.UNDEFINED));
    }
  }

  @Override
  protected void oneToManyByReference(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).properties.put(relatedName, new PropertyDefinition(relatedName, false, PropertyType.REFERENCE));
    }
  }

  @Override
  protected void oneToManyByPath(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).properties.put(relatedName, new PropertyDefinition(relatedName, false, PropertyType.PATH));
    }
  }

  @Override
  protected void oneToManyHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      current.addChildNodeType("*", relatedMapping);
    }
  }

  @Override
  protected void manyToOneByReference(ClassTypeInfo definer, String name, NodeTypeMapping relatedType) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(name, new PropertyDefinition(name, false, PropertyType.REFERENCE));
    }
  }

  @Override
  protected void manyToOneByPath(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      current.properties.put(name, new PropertyDefinition(name, false, PropertyType.PATH));
    }
  }

  @Override
  protected void manyToOneHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {
    if (definer.equals(current.mapping.getType())) {
      resolve(relatedMapping).addChildNodeType("*", current.mapping);
    }
  }

  @Override
  protected void oneToOneHierarchic(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping, boolean owner) {
    if (definer.equals(current.mapping.getType())) {
      if (owner) {
        current.addChildNodeType(name, relatedMapping);
      } else {
        resolve(relatedMapping).addChildNodeType(name, current.mapping);
      }
    }
  }

  @Override
  protected void endMapping() {
    current = null;
  }

  public void end() {

    // Resolve super types
    for (NodeType nodeType : nodeTypes.values()) {
      ClassTypeInfo cti = nodeType.mapping.getType();
      for (NodeType otherNodeType : nodeTypes.values()) {
        if (otherNodeType != nodeType) {
          if (cti.isSubType(otherNodeType.mapping.getType())) {
            nodeType.superTypes.add(otherNodeType);
          }
        }
      }
    }
  }

  public void writeTo(Writer writer) throws IOException {
    try {
      SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
      TransformerHandler handler = factory.newTransformerHandler();
      handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
      handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");

      // This is proprietary, so it's a best effort
      handler.getTransformer().setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      //
      handler.setResult(new StreamResult(writer));

      //
      writeTo(handler);
    }
    catch (Exception e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void writeTo(ContentHandler handler) {

    ContentWriter writer = new ContentWriter(handler);

    ElementWriter nodeTypesWriter = writer.element("nodeTypes");

    for (NodeType nodeType : nodeTypes.values()) {

      //
      ElementWriter nodeTypeWriter = nodeTypesWriter.element("nodeType").
        withAttribute("name", nodeType.getName()).
        withAttribute("isMixin", Boolean.toString(nodeType.isMixin())).
        withAttribute("hasOrderableChildNodes", Boolean.toString(nodeType.isOrderable()));
        // withAttribute("primaryItemName", "todo");

      //
      ElementWriter superTypesWriter = nodeTypeWriter.element("supertypes");
      for (NodeType superType : nodeType.superTypes) {
        superTypesWriter.element("supertype").content(superType.getName());
      }

      // Add mix:referenceable
      superTypesWriter.element("supertype").content("mix:referenceable");

      //
      ElementWriter propertyDefinitionsWriter = nodeTypeWriter.element("propertyDefinitions");
      for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions().values()) {
        ElementWriter propertyDefinitionWriter = propertyDefinitionsWriter.element("propertyDefinition").
          withAttribute("name", propertyDefinition.getName()).
          withAttribute("propertyType", PropertyType.nameFromValue(propertyDefinition.getType())).
          withAttribute("autoCreated", Boolean.FALSE.toString()).
          withAttribute("mandatory", Boolean.FALSE.toString()).
          withAttribute("onParentVersion", "COPY").
          withAttribute("protected", Boolean.FALSE.toString()).
          withAttribute("multiple", Boolean.toString(propertyDefinition.isMultiple()));
        propertyDefinitionWriter.element("valueConstraints");

        //
        List<String> defaultValues = propertyDefinition.getDefaultValues();
        if (defaultValues != null) {
          ElementWriter defaultValuesWriter = propertyDefinitionWriter.element("defaultValues");
          for (String s : defaultValues) {
            defaultValuesWriter.element("defaultValue").content(s);
          }
        }
      }

      //
      ElementWriter childNodeDefinitionsWriter = nodeTypeWriter.element("childNodeDefinitions");
      for (NodeDefinition childNodeDefinition : nodeType.getChildNodeDefinitions().values()) {
        childNodeDefinitionsWriter.element("childNodeDefinition").
          withAttribute("name", childNodeDefinition.getName()).
          withAttribute("defaultPrimaryType", "").
          withAttribute("autoCreated", "false").
          withAttribute("mandatory", "false").
          withAttribute("onParentVersion", "COPY").
          withAttribute("protected", "false").
          withAttribute("sameNameSiblings", "false").
          element("requiredPrimaryTypes").
          element("requiredPrimaryType").
          content(childNodeDefinition.getNodeTypeName());
      }
    }

    //
    writer.close();
  }
}
