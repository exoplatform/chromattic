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

import org.chromattic.api.RelationshipType;
import org.chromattic.metamodel.bean.BeanInfo;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SingleValuedPropertyInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class RelationshipMapping<P extends PropertyInfo<BeanValueInfo>, R extends RelationshipMapping> extends PropertyMapping<P, BeanValueInfo> {


  /** The related property if any. */
  R relatedRelationshipMapping;

  /** The related bean mapping. */
  BeanMapping relatedBeanMapping;

  public RelationshipMapping(P property) {
    super(property);
  }

  public BeanInfo getRelatedBean() {
    return property.getValue().getBean(); 
  }

  public BeanMapping getRelatedBeanMapping() {
    return relatedBeanMapping;
  }

  public R getRelatedRelationshipMapping() {
    return relatedRelationshipMapping;
  }

  public boolean isNew() {
    if (parent == null) {
      return true;
    } else {
      RelationshipMapping<?, ?> a = (RelationshipMapping<?,?>)parent;
      return property.getValue().getBean() != a.property.getValue().getBean();
    }
  }

  public abstract static class OneToOne<R extends OneToOne> extends RelationshipMapping<SingleValuedPropertyInfo<BeanValueInfo>, R> {

    /** Owner / not owner. */
    final boolean owner;

    protected OneToOne(SingleValuedPropertyInfo<BeanValueInfo> property, boolean owner) {
      super(property);

      //
      this.owner = owner;
    }

    public boolean isOwner() {
      return owner;
    }

    public static class Hierarchic extends OneToOne<Hierarchic> {

      /** Mapped by value. */
      final String mappedBy;

      /** . */
      final boolean mandatory;

      /** . */
      final boolean autocreated;

      public Hierarchic(
          SingleValuedPropertyInfo<BeanValueInfo> property,
          boolean owner,
          String mappedBy,
          boolean mandatory,
          boolean autocreated) {
        super(property, owner);

        //
        this.mappedBy = mappedBy;
        this.mandatory = mandatory;
        this.autocreated = autocreated;
      }

      public boolean getMandatory() {
        return mandatory;
      }

      public boolean getAutocreated() {
        return autocreated;
      }

      public String getMappedBy() {
        return mappedBy;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToOneHierarchic(this);
      }
    }
    public static class Embedded extends OneToOne<Embedded> {
      public Embedded(SingleValuedPropertyInfo<BeanValueInfo> property, boolean owner) {
        super(property, owner);
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToOneEmbedded(this);
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

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.manyToOneHierarchic(this);
      }
    }

    public static class Reference extends ManyToOne<OneToMany.Reference> {

      /** Mapped by value. */
      final String mappedBy;

      /** The relationship type. */
      final RelationshipType type;

      public Reference(SingleValuedPropertyInfo<BeanValueInfo> property, String mappedBy, RelationshipType type) {
        super(property);

        //
        this.mappedBy = mappedBy;
        this.type = type;
      }

      public String getMappedBy() {
        return mappedBy;
      }

      public RelationshipType getType() {
        return type;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.manyToOneReference(this);
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

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToManyHierarchic(this);
      }
    }
    public static class Reference extends OneToMany<ManyToOne.Reference> {

      /** Mapped by value. */
      final String mappedBy;

      /** The relationship type. */
      final RelationshipType type;

      public Reference(MultiValuedPropertyInfo<BeanValueInfo> property, String mappedBy, RelationshipType type) {
        super(property);

        //
        this.mappedBy = mappedBy;
        this.type = type;
      }

      public String getMappedBy() {
        return mappedBy;
      }

      public RelationshipType getType() {
        return type;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToManyReference(this);
      }
    }
  }
}
