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

package org.chromattic.common.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ContentWriter extends XMLWriter implements ContentHandler {

  /** . */
  private final ContentHandler out;

  /** . */
  private ElementWriter documentElement;

  public ContentWriter(ContentHandler out) {
    this.out = out;
  }

  @Override
  public ElementWriter element(String qName) {
    if (documentElement != null) {
      throw new IllegalStateException();
    }
    try {
      startDocument();
    }
    catch (SAXException e) {
      throw new UndeclaredThrowableException(e);
    }
    documentElement = new ElementWriter(this, this, qName);
    return documentElement;
  }

  public void close() {
    documentElement.close();
    try {
      endDocument();
    }
    catch (SAXException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

// ContentHandler implementation *************************************************************************************

  public void setDocumentLocator(Locator locator) {
    out.setDocumentLocator(locator);
  }

  public void startDocument() throws SAXException {
    out.startDocument();
  }

  public void endDocument() throws SAXException {
    out.endDocument();
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    out.startPrefixMapping(prefix, uri);
  }

  public void endPrefixMapping(String prefix) throws SAXException {
    out.endPrefixMapping(prefix);
  }

  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    out.startElement(uri, localName, qName, atts);
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    out.endElement(uri, localName, qName);
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
    out.characters(ch, start, length);
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    out.ignorableWhitespace(ch, start, length);
  }

  public void processingInstruction(String target, String data) throws SAXException {
    out.processingInstruction(target, data);
  }

  public void skippedEntity(String name) throws SAXException {
    out.skippedEntity(name);
  }
}
