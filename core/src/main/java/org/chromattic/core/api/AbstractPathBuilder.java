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

package org.chromattic.core.api;

import org.chromattic.api.Path;
import org.chromattic.api.PropertyLiteral;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.core.EntityContext;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.chromattic.metamodel.mapping.RelationshipMapping;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class AbstractPathBuilder<O> implements Path<O> {

  /** . */
  private final BeanMapping mapping;

  AbstractPathBuilder(BeanMapping mapping) {
    this.mapping = mapping;
  }

  public Path<?> parent() {
    throw new UnsupportedOperationException();
  }

  public <P> Path<P> parent(PropertyLiteral<O, P> property) {
    throw new UnsupportedOperationException();
  }

  public <P> Path<P> child(PropertyLiteral<O, P> property, String childName) {
    RelationshipMapping.OneToMany.Hierarchic childMapping = mapping.getPropertyMapping(property.getName(), RelationshipMapping.OneToMany.Hierarchic.class);

    //
    if (childMapping == null) {
      throw new IllegalArgumentException();
    }

    // Check that the child name cannot be used for other properties
    for (PropertyMapping pm : mapping.getProperties().values()) {
      if (pm instanceof RelationshipMapping.OneToOne.Hierarchic) {
        RelationshipMapping.OneToOne.Hierarchic oto = (RelationshipMapping.OneToOne.Hierarchic)pm;
        if (oto.isOwner()) {
          if (oto.getLocalName().equals(childName)) {
            throw new IllegalArgumentException();
          }
        }
      }
    }

    //
    return new AnyChild<P>(this, childMapping, childName);
  }

  public <P> Path<P> child(PropertyLiteral<O, P> property) {
    RelationshipMapping.OneToOne.Hierarchic childMapping = mapping.getPropertyMapping(property.getName(), RelationshipMapping.OneToOne.Hierarchic.class);
    if (childMapping == null) {
      throw new IllegalArgumentException();
    }
    if (!childMapping.isOwner()) {
      throw new IllegalArgumentException();
    }
    return new Child<P>(this, childMapping);
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      appendTo(sb);
      return sb.toString();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public O object() {
    throw new UnsupportedOperationException();
  }

  protected abstract void appendTo(StringBuilder sb) throws RepositoryException;

  //

  static class AnyChild<O> extends AbstractPathBuilder<O> {

    /** . */
    private final AbstractPathBuilder<?> parent;

    /** . */
    private final RelationshipMapping.OneToMany.Hierarchic relationship;

    /** . */
    private final String name;

    AnyChild(
      AbstractPathBuilder<?> parent,
      RelationshipMapping.OneToMany.Hierarchic relationship,
      String name) {
      super(relationship.getRelatedBeanMapping());

      //
      this.parent = parent;
      this.relationship = relationship;
      this.name = name;
    }

    @Override
    protected void appendTo(StringBuilder sb) throws RepositoryException {
      parent.appendTo(sb);
      sb.append('/').append(name);
    }
  }

  static class Child<O> extends AbstractPathBuilder<O> {

    /** . */
    private final AbstractPathBuilder<?> parent;

    /** . */
    private final RelationshipMapping.OneToOne.Hierarchic relationship;

    private Child(AbstractPathBuilder<?> parent, RelationshipMapping.OneToOne.Hierarchic relationship) {
      super(relationship.getRelatedBeanMapping());

      //
      this.parent = parent;
      this.relationship = relationship;
    }

    @Override
    protected void appendTo(StringBuilder sb) throws RepositoryException {
      parent.appendTo(sb);
      sb.append('/');
      if (relationship.getPrefix() != null) {
        sb.append(relationship.getPrefix()).append(':');
      }
      sb.append(relationship.getLocalName());
    }
  }

  static class Root<O> extends AbstractPathBuilder<O> {

    /** . */
    private final EntityContext context;

    public Root(EntityContext context) {
      super(context.getMapper().getMapping());

      //
      this.context = context;
    }

    @Override
    protected void appendTo(StringBuilder sb) throws RepositoryException {
      sb.append(context.getNode().getPath());
    }
  }

}
