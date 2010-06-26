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

import org.chromattic.api.RelationshipType;
import org.chromattic.api.annotations.*;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "ptm:page")
public abstract class Page {

  /**
   * Returns the page name.
   *
   * @return the page name
   */
  @Name
  public abstract String getName();

  /**
   * Returns the collection of page children.
   *
   * @return the children
   */
  @OneToMany
  public abstract Collection<Page> getChildren();

  /**
   * Returns the page parent.
   *
   * @return the parent
   */
  @ManyToOne
  public abstract Page getParent();

  /**
   * Update the page parent.
   *
   * @param page the parent
   */
  public abstract void setParent(Page page);

  /**
   * Returns the parent site.
   *
   * @return the parent site
   */
  @OneToOne
  @MappedBy("root")
  public abstract WebSite getSite();

  /**
   * Returns the content associated to this page.
   *
   * @return the content
   */
  @ManyToOne(type = RelationshipType.REFERENCE)
  @MappedBy("content")
  public abstract Content getContent();

  /**
   * Set thet content on this page
   *
   * @param content the content
   */
  public abstract void setContent(Content content);
}
