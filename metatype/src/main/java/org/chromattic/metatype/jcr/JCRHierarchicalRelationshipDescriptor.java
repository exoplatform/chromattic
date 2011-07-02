/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.metatype.jcr;

import org.chromattic.metatype.HierarchicalRelationshipDescriptor;
import org.chromattic.metatype.ObjectType;

public class JCRHierarchicalRelationshipDescriptor extends JCRRelationshipDescriptor implements HierarchicalRelationshipDescriptor {

  /** . */
  private final String name;

  public JCRHierarchicalRelationshipDescriptor(ObjectType origin, ObjectType destination, String name) {
    super(origin, destination);

    //
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
