/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.chromattic.api.ChromatticSession;
import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.format.X;
import org.chromattic.test.format.Y;
import org.chromattic.test.format.Z;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToOneEmbededTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(X.class);
    addClass(Y.class);
    addClass(Z.class);
  }

  public void testFoo() throws Exception {

/*
    ChromatticSession session = login();

    X x = session.insert(X.class, "x");
    Y y = session.create(Y.class);
    x.setY(y);
    Z z = session.create(Z.class);
    y.getChildren().put("z", z);

    String id = session.getId(z);
    Node node = session.getJCRSession().getNodeByUUID(id);
    assertEquals("bar_z", node.getName());
*/

  }

}
