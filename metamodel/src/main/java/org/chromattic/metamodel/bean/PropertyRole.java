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

package org.chromattic.metamodel.bean;

import org.chromattic.api.AttributeOption;
import org.chromattic.api.RelationshipType;
import org.chromattic.metamodel.mapping.NodeAttributeType;
import org.reflext.api.ClassTypeInfo;

import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PropertyRole {

  /** . */
  private final ClassTypeInfo declaringType;

  public ClassTypeInfo getDeclaringType() {
    return declaringType;
  }

  protected PropertyRole(ClassTypeInfo declaringType) {
    this.declaringType = declaringType;
  }

  public static class Relationship extends PropertyRole {
    public final RelationshipType type;
    public Relationship(ClassTypeInfo declaringType, RelationshipType type) {
      super(declaringType);
      this.type = type;
    }
  }

  public static class OneToMany extends Relationship {
    public OneToMany(ClassTypeInfo declaringType, RelationshipType type) {
      super(declaringType, type);
    }
  }

  public static class ManyToOne extends Relationship {
    public ManyToOne(ClassTypeInfo declaringType, RelationshipType type) {
      super(declaringType, type);
    }
  }

  public static class OneToOne extends Relationship {

    /** . */
    private final Set<AttributeOption> options;

    public OneToOne(ClassTypeInfo declaringType, Set<AttributeOption> options, RelationshipType type) {
      super(declaringType, type);

      //
      this.options = options;
    }

    public Set<AttributeOption> getOptions() {
      return options;
    }
  }

  public static class Properties extends PropertyRole {
    public Properties(ClassTypeInfo declaringType) {
      super(declaringType);
    }
  }

  public static class Property extends PropertyRole {
    public final String name;
    public final int type;
    public Property(ClassTypeInfo declaringType, String name, int type) {
      super(declaringType);
      this.name = name;
      this.type = type;
    }
  }

  public static class Attribute extends PropertyRole {
    public final NodeAttributeType type;
    public Attribute(ClassTypeInfo declaringType, NodeAttributeType type) {
      super(declaringType);
      this.type = type;
    }
  }
}
