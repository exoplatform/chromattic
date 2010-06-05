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

package org.chromattic.docs.reference.primarytypemapping;

import junit.framework.TestCase;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PrimaryTypeMappingTestCase extends TestCase {

  /** . */
  private Chromattic chromattic;

  @Override
  protected void setUp() throws Exception {
    ChromatticBuilder builder = ChromatticBuilder.create();
    builder.add(Page.class);
    chromattic = builder.build();
  }

  public void testChildrenCollection() {
    ChromatticSession session = chromattic.openSession();
    Page page = session.insert(Page.class, "foo");

    // -1-
    Page child = session.create(Page.class, "bar"); // <> Create the transient page object
    Collection<Page> children = page.getChildren(); // <> Obtain the children collection from the parent
    children.add(child); // <> The child becomes persistent and the bar node is created under the foo node
    assertSame(page, child.getParent()); // <> The parent is set to foo

    // -2-
    children.remove(child); // <> Removing the child from the collection destroys the child
    assertFalse(page.getChildren().contains(child)); // <> And the parent does not contain the child anymore
  }

  public void testParent() {
    ChromatticSession session = chromattic.openSession();
    Page page = session.insert(Page.class, "foo1");

    // -1-
    Page child = session.create(Page.class, "bar"); // <> Create the transient page object
    child.setParent(page); // <> The child becomes persistent and the bar node is created under the foo node
    assertTrue(page.getChildren().contains(child)); // <> The children collection contains the child

    // -2-
    child.setParent(null); // <> Setting the parent to null destroys the child
    assertFalse(page.getChildren().contains(child)); // <> And the parent does not contain the child anymore
  }
}
