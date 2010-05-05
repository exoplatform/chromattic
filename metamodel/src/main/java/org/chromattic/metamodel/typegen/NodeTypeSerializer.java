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
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import javax.jcr.PropertyType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeSerializer {

  /** . */
  private final List<NodeType> nodeTypes;

  public NodeTypeSerializer(List<NodeType> nodeTypes) {
    if (nodeTypes == null) {
      throw new NullPointerException();
    }
    this.nodeTypes = nodeTypes;
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
      writeTo(handler, handler);
    }
    catch (Exception e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void writeTo(ContentHandler contentHandler, LexicalHandler lexicalHandler) throws SAXException {

    DocumentEmitter writer = new DocumentEmitter(contentHandler, lexicalHandler);

    writer.comment("Node type generation prototype");

    ElementEmitter nodeTypesWriter = writer.documentElement("nodeTypes");

    for (NodeType nodeType : nodeTypes) {

      //
      nodeTypesWriter.comment(" Node type generated for the class " + nodeType.mapping.getType().getName() + " ");
      ElementEmitter nodeTypeWriter = nodeTypesWriter.element("nodeType").
        withAttribute("name", nodeType.getName()).
        withAttribute("isMixin", Boolean.toString(nodeType.isMixin())).
        withAttribute("hasOrderableChildNodes", Boolean.toString(nodeType.isOrderable()));
        // withAttribute("primaryItemName", "todo");

      //
      LinkedHashSet<String> superTypeNames = new LinkedHashSet<String>();

      //
      if (nodeType.declaredSuperTypes.isEmpty()) {
        superTypeNames.add("nt:base");
      }

      //
      for (NodeType superType : nodeType.declaredSuperTypes) {
        superTypeNames.add(superType.getName());
      }

      // Add nt:base and mix:referenceable
      superTypeNames.add("mix:referenceable");

      //
      ElementEmitter superTypesWriter = nodeTypeWriter.element("supertypes");
      for (String superTypeName : superTypeNames) {
        superTypesWriter.element("supertype").content(superTypeName);
      }

      //
      ElementEmitter propertyDefinitionsWriter = nodeTypeWriter.element("propertyDefinitions");
      for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions().values()) {
        ElementEmitter propertyDefinitionWriter = propertyDefinitionsWriter.element("propertyDefinition").
          withAttribute("name", propertyDefinition.getName()).
          withAttribute("requiredType", PropertyType.nameFromValue(propertyDefinition.getType())).
          withAttribute("autoCreated", Boolean.FALSE.toString()).
          withAttribute("mandatory", Boolean.FALSE.toString()).
          withAttribute("onParentVersion", "COPY").
          withAttribute("protected", Boolean.FALSE.toString()).
          withAttribute("multiple", Boolean.toString(propertyDefinition.isMultiple()));
        propertyDefinitionWriter.element("valueConstraints");

        //
        List<String> defaultValues = propertyDefinition.getDefaultValues();
        if (defaultValues != null) {
          ElementEmitter defaultValuesWriter = propertyDefinitionWriter.element("defaultValues");
          for (String s : defaultValues) {
            defaultValuesWriter.element("defaultValue").content(s);
          }
        }
      }

      //
      ElementEmitter childNodeDefinitionsWriter = nodeTypeWriter.element("childNodeDefinitions");
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
