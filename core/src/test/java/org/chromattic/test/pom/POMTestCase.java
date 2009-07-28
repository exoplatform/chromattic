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

package org.chromattic.test.pom;

import org.chromattic.test.AbstractTestCase;
import org.chromattic.test.pom.group.GroupSite;
import org.chromattic.test.pom.group.GroupSites;
import org.chromattic.test.pom.portal.PortalSite;
import org.chromattic.test.pom.portal.PortalSites;
import org.chromattic.api.ChromatticSession;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class POMTestCase extends AbstractTestCase {

  protected void createDomain() {
    addClass(WorkspaceImpl.class);
    addClass(UIContainerImpl.class);
    addClass(UIWindowImpl.class);
    addClass(UIInsertionImpl.class);
    addClass(PageImpl.class);
    addClass(NavigationImpl.class);
    addClass(PageNavigationTargetImpl.class);
    addClass(URLNavigationTargetImpl.class);
    addClass(PortalSites.class);
    addClass(PortalSite.class);
    addClass(GroupSites.class);
    addClass(GroupSite.class);
  }

  public void testPortals() throws Exception {
    test(0);
  }

  public void testGroups() throws Exception {
    test(1);
  }

  private void test(int type) throws Exception {

    ChromatticSession session = login();

    WorkspaceImpl workspace = session.create(WorkspaceImpl.class, "workspace");
    session.persist(workspace);

    //
    Sites sites;
    if (type == 0) {
      sites = workspace.getPortals();
    } else if (type == 1) {
      sites = workspace.getGroups();
    } else {
      throw new UnsupportedOperationException();
    }

    //
    SiteImpl site = sites.createSite("default");
    assertEquals(sites, site.getSites());
    PageImpl root = site.getRoot();
    assertNotNull(root);
    PageImpl template = root.addPage("template");
    assertNotNull(template);

    UIContainerImpl container = template.getContainer();
    assertNotNull(container);
    UIWindowImpl window = container.createWindow("window");
    container.getChildren().add(window);

    //
    PageImpl page = root.addPage("page");
    assertNotNull(page);
    page.setTemplate(template);

    if (site instanceof PortalSite) {
      ((PortalSite)site).setTemplate(template);
    }
//    session.save();

    //
    NavigationImpl nav = site.getNavigation();
    assertNotNull(nav);
    assertNull(nav.getTarget());

    //
    PageNavigationTargetImpl pageTarget = nav.createPageTarget();
    nav.setTarget(pageTarget);
    pageTarget.setPage(page);
//    session.save();

    //
    NavigationImpl subnav = nav.addChild("subnav");
    URLNavigationTargetImpl urlTarget = nav.createURLTarget();
    subnav.setTarget(urlTarget);
    urlTarget.setURL("http://www.exoplatform.com");
//    session.save();

    // Try something with template relationships

    Collection templatizedPages = template.getTemplatizedPages();
    assertNotNull(templatizedPages);
    assertEquals(1, templatizedPages.size());

    Collection templatizedSites = template.getTemplatizedPortals();
    assertNotNull(templatizedSites);
    if (site instanceof PortalSite) {
      assertEquals(1, templatizedSites.size());
    } else {
      assertEquals(0, templatizedSites.size());
    }

    Collection templatizedObjects = template.getTemplatizedObjects();
    assertNotNull(templatizedObjects);
    if (site instanceof PortalSite) {
      assertEquals(2, templatizedObjects.size());
    } else {
      assertEquals(1, templatizedObjects.size());
    }


  }

}
