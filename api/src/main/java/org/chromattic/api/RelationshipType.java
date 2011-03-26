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
package org.chromattic.api;

/**
 * The type of a relationship. It defines the semantics about how the relationship is maintained between
 * nodes.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @see org.chromattic.api.annotations.OneToMany
 * @see org.chromattic.api.annotations.ManyToOne
 */
public enum RelationshipType {

  /**
   * A hierarchic relationship uses the natural parent child relationship to associate nodes.
   */
  HIERARCHIC,

  /**
   * A reference relationship uses reference a typed property to associate nodes.
   */
  REFERENCE,

  /**
   * A path relationship uses path a typed property to associate nodes.
   */
  PATH

}
