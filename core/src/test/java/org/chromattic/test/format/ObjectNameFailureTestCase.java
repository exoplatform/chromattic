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

package org.chromattic.test.format;

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.format.FormatterContext;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectNameFailureTestCase extends AbstractTestCase {

  protected void createDomain() {
    getBuilder().setOptionValue(ChromatticBuilder.OBJECT_FORMATTER_CLASSNAME, DelegatingObjectFormatter.class.getName());
    addClass(A.class);
    addClass(B.class);
  }

  public void testEncoderThrowsIAE() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        throw new IllegalArgumentException();
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testEncodeThrowsNPE() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        throw new NullPointerException();
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (NullPointerException e) {
    }
  }

  public void testEncodeThrowsError() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        throw new Error();
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (Error e) {
    }
  }

  public void testEncodeReturnsNull() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        return null;
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testEncodeThrowsRE() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        throw new RuntimeException();
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (UndeclaredThrowableException e) {
      assertTrue(e.getCause().getClass().equals(RuntimeException.class));
    }
  }

  public void testEncodeReturnsIllegal() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String encodeNodeName(FormatterContext context, String externalName) {
        return "/";
      }
    };
    try {
      session.insert(A.class, "a");
      fail();
    }
    catch (IllegalArgumentException e) {
    }
  }

  public void testDecodeReturnsNullName() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String decodeNodeName(FormatterContext context, String internalName) {
        return null;
      }
    };
    Node aNode = session.getRoot().addNode("a", "format_a");
    A a = session.findByNode(A.class, aNode);
    try {
      a.getName();
      fail();
    }
    catch (IllegalStateException e) {
    }
  }

  public void testDecodeThrowsRE() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String decodeNodeName(FormatterContext context, String internalName) {
        throw new RuntimeException();
      }
    };
    Node aNode = session.getRoot().addNode("a", "format_a");
    A a = session.findByNode(A.class, aNode);
    try {
      a.getName();
      fail();
    }
    catch (UndeclaredThrowableException e) {
      assertTrue(e.getCause().getClass().equals(RuntimeException.class));
    }
  }

  public void testDecodeThrowsError() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String decodeNodeName(FormatterContext context, String internalName) {
        throw new Error();
      }
    };
    Node aNode = session.getRoot().addNode("a", "format_a");
    A a = session.findByNode(A.class, aNode);
    try {
      a.getName();
      fail();
    }
    catch (Error e) {
    }
  }

  public void testDecodeThrowsISE() throws RepositoryException {
    ChromatticSessionImpl session = login();
    DelegatingObjectFormatter.delegate = new AbstractObjectFormatter() {
      @Override
      public String decodeNodeName(FormatterContext context, String internalName) {
        throw new IllegalStateException();
      }
    };
    Node aNode = session.getRoot().addNode("a", "format_a");
    A a = session.findByNode(A.class, aNode);
    try {
      a.getName();
      fail();
    }
    catch (IllegalStateException e) {
    }
  }
}