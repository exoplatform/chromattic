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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ElementWriter extends XMLWriter {

  /** . */
  private static final AttributesImpl EMPTY = new AttributesImpl();

  /** . */
  private static final int INITIAL = 0;

  /** . */
  private static final int OPEN = 1;

  /** . */
  private static final int CLOSED = 2;

  /** . */
  private final String qName;

  /** . */
  private AttributesImpl attrs;

  /** . */
  private int status;

  /** . */
  private final ContentHandler handler;

  /** . */
  private ElementWriter currentChild;

  /** . */
  private XMLWriter parent;

  public ElementWriter(ContentHandler handler, XMLWriter parent, String qName) {
    this.handler = handler;
    this.qName = qName;
    this.status = INITIAL;
    this.attrs = EMPTY;
    this.currentChild = null;
    this.parent = parent;
  }

  private void start() {
    try {
      handler.startElement("", "", qName, attrs);
    }
    catch (SAXException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  private void end() {
    try {
      handler.endElement("", "", qName);
    }
    catch (SAXException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public void content(String data) {
    switch (status) {
      case INITIAL:
        start();
        status = OPEN;
        break;
      case OPEN:
        if (currentChild != null) {
          currentChild.close();
          currentChild = null;
        }
        break;
      case CLOSED:
        throw new IllegalStateException();
    }
    try {
      handler.characters(data.toCharArray(), 0, data.length());
    }
    catch (SAXException e) {
      throw new UndeclaredThrowableException(e);
    }
  }

  public XMLWriter getParent() {
    return parent;
  }

  @Override
  public ElementWriter element(String qName) {
    switch (status) {
      case INITIAL:
        start();
        status = OPEN;
        break;
      case OPEN:
        if (currentChild != null) {
          currentChild.close();
          currentChild = null;
        }
        break;
      case CLOSED:
        throw new IllegalStateException("Element " + this.qName + " is already closed");
    }
    currentChild = new ElementWriter(handler, this, qName);
    return currentChild;
  }

  // Internal method called on demand
  void close() {
    switch (status) {
      case INITIAL:
        start();
        end();
        break;
      case OPEN:
        if (currentChild != null) {
          currentChild.close();
          currentChild = null;
        }
        end();
        break;
      case CLOSED:
        throw new IllegalStateException();
    }
    status = CLOSED;
  }

  public ElementWriter withAttribute(String qName, String value) {
    if (status != INITIAL) {
      throw new IllegalStateException();
    }
    if (attrs == EMPTY) {
      attrs = new AttributesImpl();
    }
    attrs.addAttribute("", "", qName, "", value);
    return this;
  }
}
