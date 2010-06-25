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

import org.chromattic.api.ChromatticSession;
import org.chromattic.docs.reference.AbstractTestCase;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PrimaryTypeMappingTestCase extends AbstractTestCase {

  @Override
  protected Iterable<Class<?>> classes() {
    return Arrays.asList(WebSite.class, Page.class, Content.class);
  }

  public void testChildrenCollection() {
    ChromatticSession session = login();
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
    ChromatticSession session = login();
    Page page = session.insert(Page.class, "foo1");

    // -1-
    Page child = session.create(Page.class, "bar"); // <> Create the transient page object
    child.setParent(page); // <> The child becomes persistent and the bar node is created under the foo node
    assertTrue(page.getChildren().contains(child)); // <> The children collection contains the child

    // -2-
    child.setParent(null); // <> Setting the parent to null destroys the child
    assertFalse(page.getChildren().contains(child)); // <> And the parent does not contain the child anymore
  }

  public void testOneToOne() {
    ChromatticSession session = login();
    WebSite site = session.insert(WebSite.class, "site");

    // -1-
    Page root = session.create(Page.class); // <> Create the transient page object
    site.setRootPage(root); // <> The page becomes persistent and the //root// node is inserted under the //site// node
    assertEquals(site, root.getSite()); // <> The parent of the root page is the site object
    session.save();

    // -2-
    site.setRootPage(null); // <> Setting the root page to null destroys the relationship
  }

  public void testOneToManyReference() {
    ChromatticSession session = login();
    WebSite site = session.insert(WebSite.class, "site");
    Content content1 = session.create(Content.class, "1");
    Content content2 = session.create(Content.class, "2");
    site.getContents().add(content1);
    Page root = session.create(Page.class);
    site.setRootPage(root);

    //
    root.setContent(content1);
  }
}
