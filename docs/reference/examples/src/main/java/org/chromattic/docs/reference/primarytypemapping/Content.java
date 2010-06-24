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

package org.chromattic.docs.reference.primarytypemapping;

import org.chromattic.api.RelationshipType;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "ptm:content")
public abstract class Content {

  /**
   * Returns the page content.
   * @return the page content
   */
  @Property(name = "content")
  public abstract String getContent();

  /**
   * Updates the page content.
   * @param content the new page content
   */
  public abstract void setContent(String content);

  /**
   * Returns the list of the page tags.
   *
   * @return the list of tags
   */
  @Property(name = "tags")
  public abstract List<String> getTags();

  /**
   * Update the list of the page tags.
   *
   * @param tags the list of tags
   */
  public abstract void setTags(List<String> tags);

  /**
   * Returns the page title.
   *
   * @return the page title
   */
  @Property(name = "title")
  public abstract String getTitle();

  /**
   * Updates the page title.
   *
   * @param title the new page title
   */
  public abstract void setTitle(String title);

  /**
   * Returns the date of the page last modification.
   *
   * @return the date of the last modification
   */
  @Property(name = "lastmodifieddate")
  public abstract Date getLastModifiedDate();

  /**
   * Updates the date of the page last modification.
   *
   * @param date the date of the last modification
   */
  public abstract void setLastModifiedDate(Date date);

  /**
   * Returns all the pages associated with this content.
   *
   * @return the associated pages
   */
  @OneToMany(type = RelationshipType.REFERENCE)
  @MappedBy("content")
  public abstract Collection<Page> getPages();
}
