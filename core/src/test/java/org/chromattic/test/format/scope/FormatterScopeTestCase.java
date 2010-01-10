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
package org.chromattic.test.format.scope;

import org.chromattic.api.ChromatticBuilder;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.format.A;
import org.chromattic.test.format.B;
import org.chromattic.test.format.C;
import org.chromattic.test.format.FooPrefixerFormatter;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class FormatterScopeTestCase extends AbstractTestCase {


  protected void createDomain() {
    getBuilder().setOption(ChromatticBuilder.OBJECT_FORMATTER_CLASSNAME, FooPrefixerFormatter.class.getName());
    addClass(A.class);
    addClass(B.class);
    addClass(C.class);
  }

  public void testClassOverride() throws Exception {

    ChromatticSessionImpl session = login();
    A a = session.insert(A.class, "a");
    C c = session.insert(C.class, "c");

    //
    B b1 = session.create(B.class);
    a.getChildren().put("b", b1);
    assertEquals("b", b1.getName());
    Node b1Node = session.getNode(b1);
    assertEquals("foo_b", b1Node.getName());

    //
    B b2 = session.create(B.class);
    c.getChildren().put("b", b2);
    assertEquals("b", b2.getName());
    Node b2Node = session.getNode(b2);
    assertEquals("bar_b", b2Node.getName());
  }
}
