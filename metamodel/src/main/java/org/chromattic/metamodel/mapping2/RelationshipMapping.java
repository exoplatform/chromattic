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

package org.chromattic.metamodel.mapping2;

import org.chromattic.metamodel.bean2.BeanInfo;
import org.chromattic.metamodel.bean2.BeanValueInfo;
import org.chromattic.metamodel.bean2.PropertyInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class RelationshipMapping<P extends PropertyInfo<BeanValueInfo>> extends PropertyMapping<P, BeanValueInfo> {


  /** The related property if any. */
  RelationshipMapping related;

  /** . */
  private final Relationship<P> relationship;

  public RelationshipMapping(P property, Relationship<P> relationship) {
    super(property);

    //
    this.relationship = relationship;
  }

  public Relationship<P> getRelationship() {
    return relationship;
  }

  public BeanInfo getRelatedBean() {
    return property.getValue().getBean(); 
  }

  public RelationshipMapping getRelatedMapping() {
    return related;
  }

  @Override
  public void accept(MappingVisitor visitor) {
    if (relationship instanceof Relationship.OneToOne.Hierarchic) {
      Relationship.OneToOne.Hierarchic a = (Relationship.OneToOne.Hierarchic)relationship;
      visitor.oneToOneHierarchic(property, a.mappedBy, a.owner);
    }
  }
}
