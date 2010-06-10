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

import org.chromattic.common.xml.DocumentEmitter;
import org.chromattic.common.xml.ElementEmitter;
import org.xml.sax.SAXException;

import javax.jcr.PropertyType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class XMLNodeTypeSerializer extends NodeTypeSerializer {

  /** . */
  private DocumentEmitter docXML;

  /** . */
  private ElementEmitter nodeTypesXML;

  /** . */
  private ElementEmitter nodeTypeXML;

  /** . */
  private ElementEmitter propertyDefinitionsXML;

  /** . */
  private ElementEmitter childNodeDefinitionsXML;

  public XMLNodeTypeSerializer(List<NodeType> nodeTypes, Map<String, String> mappings) {
    super(nodeTypes, mappings);
  }

  public XMLNodeTypeSerializer(List<NodeType> nodeTypes) {
    super(nodeTypes);
  }

  public XMLNodeTypeSerializer(Map<String, String> mappings) {
    super(mappings);
  }

  public XMLNodeTypeSerializer() {
  }

  @Override
  public void writeTo(Writer writer) throws Exception {
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
    docXML = new DocumentEmitter(handler, handler);
    docXML.comment("Node type generation prototype");

    //
    writeTo();
  }

  public void startNodeTypes(Map<String, String> mappings) throws SAXException {
    nodeTypesXML = docXML.documentElement("nodeTypes");
    for (Map.Entry<String, String> mapping : mappings.entrySet()) {
      nodeTypesXML.withNamespace(mapping.getKey(), mapping.getValue());
    }
  }

  public void startNodeType(
    String javaClassName,
    String name,
    boolean mixin,
    boolean orderableChildNodes,
    Collection<String> superTypeNames) throws SAXException {
    nodeTypesXML.comment(" Node type generated for the class " + javaClassName + " ");
    nodeTypeXML = nodeTypesXML.element("nodeType").
      withAttribute("name", name).
      withAttribute("isMixin", Boolean.toString(mixin)).
      withAttribute("hasOrderableChildNodes", Boolean.toString(orderableChildNodes));
      // withAttribute("primaryItemName", "todo");

    //
    ElementEmitter superTypesWriter = nodeTypeXML.element("supertypes");
    for (String superTypeName : superTypeNames) {
      superTypesWriter.element("supertype").content(superTypeName);
    }
  }

  public void startProperties() throws SAXException {
    propertyDefinitionsXML = nodeTypeXML.element("propertyDefinitions");
  }

  public void property(
    String name,
    int requiredType,
    boolean multiple,
    Collection<String> defaultValues) throws SAXException {
    ElementEmitter propertyDefinitionXML = propertyDefinitionsXML.element("propertyDefinition").
      withAttribute("name", name).
      withAttribute("requiredType", PropertyType.nameFromValue(requiredType)).
      withAttribute("autoCreated", Boolean.FALSE.toString()).
      withAttribute("mandatory", Boolean.FALSE.toString()).
      withAttribute("onParentVersion", "COPY").
      withAttribute("protected", Boolean.FALSE.toString()).
      withAttribute("multiple", Boolean.toString(multiple));

    // Empty for now
    ElementEmitter valueConstraintsXML = propertyDefinitionXML.element("valueConstraints");
    valueConstraintsXML.close();

    //
    if (defaultValues != null) {
      ElementEmitter defaultValuesXML = propertyDefinitionXML.element("defaultValues");
      for (String s : defaultValues) {
        defaultValuesXML.element("defaultValue").content(s);
      }
    }
  }

  public void endProperties() throws SAXException {
    propertyDefinitionsXML.close();
    propertyDefinitionsXML = null;
  }

  public void startChildNodes() throws SAXException {
    childNodeDefinitionsXML = nodeTypeXML.element("childNodeDefinitions");
  }

  public void childNode(
    String name,
    String nodeTypeName,
    boolean mandatory,
    boolean autocreated) throws SAXException {
    childNodeDefinitionsXML.element("childNodeDefinition").
      withAttribute("name", name).
      withAttribute("defaultPrimaryType", nodeTypeName).
      withAttribute("autoCreated", Boolean.valueOf(autocreated).toString()).
      withAttribute("mandatory", Boolean.valueOf(mandatory).toString()).
      withAttribute("onParentVersion", "COPY").
      withAttribute("protected", "false").
      withAttribute("sameNameSiblings", "false").
      element("requiredPrimaryTypes").
      element("requiredPrimaryType").
      content(nodeTypeName);
  }

  public void endChildNodes() throws SAXException {
    childNodeDefinitionsXML.close();
    childNodeDefinitionsXML = null;
  }

  @Override
  public void endNodeType() throws SAXException {
    nodeTypeXML.close();
    nodeTypeXML = null;
  }

  @Override
  public void endNodeTypes() throws SAXException {
    docXML.close();
    docXML = null;
  }
}