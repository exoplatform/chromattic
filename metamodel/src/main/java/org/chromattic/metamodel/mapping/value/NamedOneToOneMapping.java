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

package org.chromattic.metamodel.mapping.value;

import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.chromattic.api.RelationshipType;
import org.reflext.api.ClassTypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NamedOneToOneMapping extends AbstractOneToOneMapping<NamedOneToOneMapping> {

  /** . */
  private final String name;

  public NamedOneToOneMapping(ClassTypeInfo definer, NodeTypeMapping owner, NodeTypeMapping relatedType, String name, RelationshipType type, boolean owning) {
    super(definer, owner, relatedType, type, owning);

    //
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public NamedOneToOneMapping getRelatedRelationship() {
    for (PropertyMapping<?> propertyMapping : getRelatedMapping().getPropertyMappings()) {
      ValueMapping valueMapping = propertyMapping.getValueMapping();
      if (valueMapping instanceof NamedOneToOneMapping) {
        NamedOneToOneMapping otoMapping = (NamedOneToOneMapping)valueMapping;
        boolean sameOwner = otoMapping.getRelatedMapping().equals(getOwner());
        boolean sameType = getType() == otoMapping.getType();
        boolean sameName = name.equals(otoMapping.name);
        if (sameOwner && sameType && sameName) {
          return otoMapping;
        }
      }
    }

    //
    return null;
  }
}