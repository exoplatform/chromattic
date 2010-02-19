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
import org.xml.sax.ContentHandler;

import javax.jcr.PropertyType;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class XMLNodeTypeVisitor implements NodeTypeVisitor {

  static final int NODETYPE_OPEN = 0;

  static final int PROPERTIES_OPEN = 1;

  static final int CHILDREN_OPEN = 2;

  int status;

  private ElementWriter currentWriter;

  private final ContentWriter writer;

  public XMLNodeTypeVisitor(Writer writer) throws TransformerConfigurationException {
    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
    TransformerHandler handler = factory.newTransformerHandler();
    handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "xml");
    handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
    handler.setResult(new StreamResult(writer));

    //
    this.writer = new ContentWriter(handler);
  }

  public XMLNodeTypeVisitor(ContentHandler handler) {
    this.writer = new ContentWriter(handler);
  }

  public void start() {
    currentWriter = writer.element("nodeTypes");
  }

  public void startType(String name, boolean primary) {
    currentWriter.element("nodeType").
      withAttribute("name", name).
      withAttribute("isMixin", Boolean.toString(!primary)).
      withAttribute("hasOrderableChildNodes", Boolean.FALSE.toString()).
      withAttribute("primaryItemName", "todo");
    status = NODETYPE_OPEN;
  }

  public void addProperty(String propertyName, boolean multiple, int propertyType) {
    if (status == NODETYPE_OPEN) {
      currentWriter = currentWriter.element("propertyDefinitions");
      status = PROPERTIES_OPEN;
    }
    currentWriter.element("propertyDefinition").
      withAttribute("name", propertyName).
      withAttribute("propertyType", PropertyType.nameFromValue(propertyType)).
      withAttribute("autoCreated", Boolean.FALSE.toString()).
      withAttribute("mandatory", Boolean.FALSE.toString()).
      withAttribute("onParentVersion", "COPY").
      withAttribute("protected", Boolean.FALSE.toString()).
      withAttribute("multiple", Boolean.toString(multiple)).
      element("valueConstraints");
  }

  public void addChildNodeDefinition(String childName, String nodeTypeName) {
    if (status == NODETYPE_OPEN) {
      currentWriter = currentWriter.element("childNodeDefinitions");
      status = CHILDREN_OPEN;
    } else if (status == PROPERTIES_OPEN) {
      currentWriter = currentWriter.getParent().element("childNodeDefinitions");
      status = CHILDREN_OPEN;
    }
    currentWriter.element("childNodeDefinition").
      withAttribute("name", childName).
      withAttribute("defaultPrimaryType", "nt:unstructured").
      withAttribute("autoCreated", "false").
      withAttribute("mandatory", "false").
      withAttribute("onParentVersion", "COPY").
      withAttribute("protected", "false").
      withAttribute("sameNameSiblings", "false").
      element("requiredPrimaryTypes").
      element("requiredPrimaryType").
      content(nodeTypeName);
  }

  public void endType() {
    if (status != NODETYPE_OPEN) {
      currentWriter = (ElementWriter)currentWriter.getParent();
    }
  }

  public void end() {
    writer.perform();
  }
}
