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

import org.chromattic.api.RelationshipType;
import org.chromattic.metamodel.mapping.NodeTypeMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class RelationshipMapping<T extends RelationshipMapping<U, T>, U extends RelationshipMapping<T, U>> extends ValueMapping {

  /** . */
  private NodeTypeMapping owner;

  /** . */
  private NodeTypeMapping relatedType;

  /** . */
  private final Multiplicity multiplicity;

  /** . */
  private final Multiplicity relatedMultiplicity;

  /** . */
  private final RelationshipType type;

  public RelationshipMapping(NodeTypeMapping owner, NodeTypeMapping relatedType, Multiplicity multiplicity, Multiplicity relatedMultiplicity, RelationshipType type) {
    this.owner = owner;
    this.relatedType = relatedType;
    this.multiplicity = multiplicity;
    this.relatedMultiplicity = relatedMultiplicity;
    this.type = type;
  }

  public NodeTypeMapping getOwner() {
    return owner;
  }

  public U getRelatedRelationship() {
    throw new UnsupportedOperationException();
  }

  public RelationshipType getType() {
    return type;
  }

  public NodeTypeMapping getRelatedType() {
    return relatedType;
  }

  public Multiplicity getMultiplicity() {
    return multiplicity;
  }

  public Multiplicity getRelatedMultiplicity() {
    return relatedMultiplicity;
  }
}