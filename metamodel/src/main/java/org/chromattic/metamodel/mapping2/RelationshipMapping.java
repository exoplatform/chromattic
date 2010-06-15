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

import org.chromattic.metamodel.bean2.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class RelationshipMapping<P extends PropertyInfo<BeanValueInfo>, R extends RelationshipMapping> extends PropertyMapping<P, BeanValueInfo> {


  /** The related property if any. */
  R related;

  public RelationshipMapping(P property) {
    super(property);
  }

  public BeanInfo getRelatedBean() {
    return property.getValue().getBean(); 
  }

  public R getRelatedMapping() {
    return related;
  }

  @Override
  public void accept(MappingVisitor visitor) {
/*
    if (relationship instanceof Relationship.OneToOne.Hierarchic) {
      Relationship.OneToOne.Hierarchic a = (Relationship.OneToOne.Hierarchic)relationship;
      visitor.oneToOneHierarchic(property, a.mappedBy, a.owner);
    }
*/
  }

  public abstract static class OneToOne<R extends OneToOne> extends RelationshipMapping<SingleValuedPropertyInfo<BeanValueInfo>, R> {

    protected OneToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      super(property);
    }

    public static class Hierarchic extends OneToOne<Hierarchic> {

      /** Owner / not owner. */
      final boolean owner;

      /** Mapped by value. */
      final String mappedBy;

      public Hierarchic(SingleValuedPropertyInfo<BeanValueInfo> property, boolean owner, String mappedBy) {
        super(property);

        //
        this.owner = owner;
        this.mappedBy = mappedBy;
      }

      public boolean isOwner() {
        return owner;
      }

      public String getMappedBy() {
        return mappedBy;
      }
    }
    public static class Embedded extends OneToOne<Embedded> {
      public Embedded(SingleValuedPropertyInfo<BeanValueInfo> property) {
        super(property);
      }
    }
  }

  public abstract static class ManyToOne<R extends OneToMany> extends RelationshipMapping<SingleValuedPropertyInfo<BeanValueInfo>, R> {

    protected ManyToOne(SingleValuedPropertyInfo<BeanValueInfo> property) {
      super(property);
    }

    public static class Hierarchic extends ManyToOne<OneToMany.Hierarchic> {
      public Hierarchic(SingleValuedPropertyInfo<BeanValueInfo> property) {
        super(property);
      }
    }
    public static class Reference extends ManyToOne<OneToMany.Reference> {

      /** Mapped by value. */
      final String mappedBy;

      public Reference(SingleValuedPropertyInfo<BeanValueInfo> property, String mappedBy) {
        super(property);

        //
        this.mappedBy = mappedBy;
      }

      public String getMappedBy() {
        return mappedBy;
      }
    }
  }

  public abstract static class OneToMany<R extends ManyToOne> extends RelationshipMapping<MultiValuedPropertyInfo<BeanValueInfo>, R> {

    protected OneToMany(MultiValuedPropertyInfo<BeanValueInfo> property) {
      super(property);
    }

    public static class Hierarchic extends OneToMany<ManyToOne.Hierarchic> {
      public Hierarchic(MultiValuedPropertyInfo<BeanValueInfo> property) {
        super(property);
      }
    }
    public static class Reference extends OneToMany<ManyToOne.Reference> {

      /** Mapped by value. */
      final String mappedBy;

      public Reference(MultiValuedPropertyInfo<BeanValueInfo> property, String mappedBy) {
        super(property);

        //
        this.mappedBy = mappedBy;
      }

      public String getMappedBy() {
        return mappedBy;
      }
    }
  }
}
