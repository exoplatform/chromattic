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

package org.chromattic.metamodel.mapping;

import org.chromattic.api.RelationshipType;
import org.chromattic.metamodel.bean.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class RelationshipMapping<P extends PropertyInfo<BeanValueInfo, K>, R extends RelationshipMapping, K extends ValueKind>
    extends PropertyMapping<P, BeanValueInfo, K> {


  /** . */
  final Class<R> relatedRelationhipType;

  /** The related property if any. */
  private List<R> relatedRelationshipMapping;

  /** The related bean mapping. */
  BeanMapping relatedBeanMapping;

  public RelationshipMapping(Class<R> relatedRelationhipType, P property) {
    super(property);

    //
    this.relatedRelationhipType = relatedRelationhipType;
    this.relatedRelationshipMapping = null;
    this.relatedBeanMapping = null;
  }

  public BeanInfo getRelatedBean() {
    return property.getValue().getBean(); 
  }

  public BeanMapping getRelatedBeanMapping() {
    return relatedBeanMapping;
  }

  public List<R> getRelatedRelationshipMapping() {
    return relatedRelationshipMapping;
  }

  public boolean isTypeCovariant() {
    if (parent == null) {
      return true;
    } else {
      RelationshipMapping<?, ?, ?> parentRelationship = (RelationshipMapping<?, ?, ?>)parent;
      return property.getValue().getBean() != parentRelationship.property.getValue().getBean();
    }
  }

  /**
   * The resolution process attempt to find the related relationship for this relationship.
   */
  void resolve() {

    if (relatedRelationshipMapping != null) {
      return;
    }

    //
    List<R> found = Collections.emptyList();

    //
    for (PropertyMapping relatedBeanPropertyMapping : relatedBeanMapping.getProperties().values()) {
      if (relatedBeanPropertyMapping instanceof RelationshipMapping) {
        RelationshipMapping<?, ?, ?> relatedBeanRelationshipMapping = (RelationshipMapping<?, ?, ?>)relatedBeanPropertyMapping;
        if (relatedRelationhipType.isInstance(relatedBeanRelationshipMapping)) {
          R toRelationship = relatedRelationhipType.cast(relatedBeanRelationshipMapping);
          if (this != toRelationship) {
            if (matches(toRelationship)) {
              if (found.isEmpty()) {
                found = new LinkedList<R>();
              }
              found.add(toRelationship);
            }
          }
        }
      }
    }

    //
    this.relatedRelationshipMapping = found;
  }

  abstract boolean matches(R relationship);

  public abstract static class OneToOne<R extends OneToOne> extends RelationshipMapping<SingleValuedPropertyInfo<BeanValueInfo>, R, ValueKind.Single> {

    /** Owner / not owner. */
    final boolean owner;

    protected OneToOne(Class<R> relatedRelationhipType, SingleValuedPropertyInfo<BeanValueInfo> property, boolean owner) {
      super(relatedRelationhipType, property);

      //
      this.owner = owner;
    }

    public boolean isOwner() {
      return owner;
    }

    public static class Hierarchic extends OneToOne<Hierarchic> {

      /** . */
      final String declaredPrefix;

      /** . */
      final String prefix;

      /** . */
      final String localName;

      /** . */
      final boolean mandatory;

      /** . */
      final boolean autocreated;

      public Hierarchic(
          SingleValuedPropertyInfo<BeanValueInfo> property,
          boolean owner,
          String declaredPrefix,
          String prefix,
          String localName,
          boolean mandatory,
          boolean autocreated) {
        super(Hierarchic.class, property, owner);

        //
        this.declaredPrefix = declaredPrefix;
        this.prefix = prefix;
        this.localName = localName;
        this.mandatory = mandatory;
        this.autocreated = autocreated;
      }

      public boolean getMandatory() {
        return mandatory;
      }

      public boolean getAutocreated() {
        return autocreated;
      }

      public String getDeclaredPrefix() {
        return declaredPrefix;
      }

      public String getPrefix() {
        return prefix;
      }

      public String getLocalName() {
        return localName;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToOneHierarchic(this);
      }

      @Override
      public boolean matches(Hierarchic relationship) {
        String fromPrefix = prefix == null ? "" : prefix;
        String toPrefix = relationship.prefix == null ? "" : relationship.prefix;
        return fromPrefix.equals(toPrefix) && localName.equals(relationship.localName) && owner != relationship.owner;
      }
    }

    public static class Embedded extends OneToOne<Embedded> {
      public Embedded(SingleValuedPropertyInfo<BeanValueInfo> property, boolean owner) {
        super(Embedded.class, property, owner);
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToOneEmbedded(this);
      }

      @Override
      public boolean matches(Embedded relationship) {
        // For now we don't need this wiring
        return false;
      }
    }
  }

  public abstract static class ManyToOne<R extends OneToMany> extends RelationshipMapping<SingleValuedPropertyInfo<BeanValueInfo>, R, ValueKind.Single> {

    protected ManyToOne(Class<R> relatedRelationhipType, SingleValuedPropertyInfo<BeanValueInfo> property) {
      super(relatedRelationhipType, property);
    }

    public static class Hierarchic extends ManyToOne<OneToMany.Hierarchic> {

      /** . */
      final String declaredPrefix;

      /** . */
      final String prefix;

      public Hierarchic(SingleValuedPropertyInfo<BeanValueInfo> property, String declaredPrefix, String prefix) {
        super(OneToMany.Hierarchic.class, property);

        //
        this.declaredPrefix = declaredPrefix;
        this.prefix = prefix;
      }

      public String getDeclaredPrefix() {
        return declaredPrefix;
      }

      public String getPrefix() {
        return prefix;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.manyToOneHierarchic(this);
      }

      @Override
      public boolean matches(OneToMany.Hierarchic relationship) {
        return true;
      }
    }

    public static class Reference extends ManyToOne<OneToMany.Reference> {

      /** Mapped by value. */
      final String mappedBy;

      /** The relationship type. */
      final RelationshipType type;

      public Reference(SingleValuedPropertyInfo<BeanValueInfo> property, String mappedBy, RelationshipType type) {
        super(OneToMany.Reference.class, property);

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

      @Override
      public boolean matches(OneToMany.Reference relationship) {
        return mappedBy.equals(relationship.mappedBy);
      }
    }
  }

  public abstract static class OneToMany<R extends ManyToOne, K extends ValueKind.Multi> extends RelationshipMapping<MultiValuedPropertyInfo<BeanValueInfo, K>, R, K> {

    protected OneToMany(Class<R> relatedRelationhipType, MultiValuedPropertyInfo<BeanValueInfo, K> property) {
      super(relatedRelationhipType, property);
    }

    public static class Hierarchic<K extends ValueKind.Multi> extends OneToMany<ManyToOne.Hierarchic, K> {

      /** . */
      final String declaredPrefix;

      /** . */
      final String prefix;

      public Hierarchic(MultiValuedPropertyInfo<BeanValueInfo, K> property, String declaredPrefix, String prefix) {
        super(ManyToOne.Hierarchic.class, property);

        //
        this.declaredPrefix = declaredPrefix;
        this.prefix = prefix;
      }

      public String getDeclaredPrefix() {
        return declaredPrefix;
      }

      public String getPrefix() {
        return prefix;
      }

      @Override
      public void accept(MappingVisitor visitor) {
        visitor.oneToManyHierarchic(this);
      }

      @Override
      public boolean matches(ManyToOne.Hierarchic relationship) {
        return true;
      }
    }

    public static class Reference<K extends ValueKind.Multi> extends OneToMany<ManyToOne.Reference, K> {

      /** Mapped by value. */
      final String mappedBy;

      /** The relationship type. */
      final RelationshipType type;

      public Reference(MultiValuedPropertyInfo<BeanValueInfo, K> property, String mappedBy, RelationshipType type) {
        super(ManyToOne.Reference.class, property);

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

      @Override
      public boolean matches(ManyToOne.Reference relationship) {
        return mappedBy.equals(relationship.mappedBy);
      }
    }
  }
}
