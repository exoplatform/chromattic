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

package org.chromattic.docs.reference.gettingstarted;

import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

/**
 * The page of a site.
 */
@PrimaryType(name = "page") // <1> The Page class is mapped to the page node type
public abstract class Page {

  /**
   * Returns the page name.
   * @return the page name
   */
  @Name
  public abstract String getName(); // <2> The name property is mapped to the node name

  /**
   * Returns the page title.
   * @return the page title
   */
  @Property(name = "title")
  public abstract String getTitle(); // <3> The title property is mapped to the title node property

  /**
   * Updates the page title.
   * @param title the new page title
   */
  public abstract void setTitle(String title);

  /**
   * Returns the page content.
   * @return the page content
   */
  @Property(name = "content")
  public abstract String getContent(); // <4> The content property is mapped to the content node property

  /**
   * Updates the page content.
   * @param content the new page content
   */
  public abstract void setContent(String content);

}
