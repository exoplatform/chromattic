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

import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.Hierarchic;
import org.chromattic.api.annotations.OneToOne;
import org.chromattic.api.annotations.ManyToOne;
import org.chromattic.api.annotations.RelatedMappedBy;
import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.test.pom.portal.PortalSite;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "exo:page")
public abstract class PageImpl {

  @Name
  public abstract String getName();

  @ManyToOne
  @MappedBy("template")
  public abstract PageImpl getTemplate();

  public abstract void setTemplate(PageImpl template);

  @OneToMany
  @RelatedMappedBy("template")
  public abstract Collection<PortalSite> getTemplatizedPortals();

  @OneToMany
  @RelatedMappedBy("template")
  public abstract Collection<PageImpl> getTemplatizedPages();

  @OneToMany
  @RelatedMappedBy("template")
  public abstract Collection<Object> getTemplatizedObjects();

  @OneToMany
  @Hierarchic
  public abstract Collection<PageImpl> getChildren();

  @ManyToOne
  @Hierarchic
  public abstract PageImpl getPageParent();

  @OneToOne
  @Hierarchic
  @RelatedMappedBy("root")
  public abstract SiteImpl getSiteParent();

  @OneToOne
  @Hierarchic
  @MappedBy("container")
  public abstract UIContainerImpl getContainer();

  @Create
  public abstract PageImpl createPage(String name);

  public PageImpl addPage(String name) {
    PageImpl page = createPage(name);
    getChildren().add(page);
    return page;
  }
}
