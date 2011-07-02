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

import org.chromattic.metatype.InheritanceRelationshipDescriptor;
import org.chromattic.metatype.HierarchicalRelationshipDescriptor;
import org.chromattic.metatype.ObjectType;
import org.chromattic.metatype.PropertyDescriptor;

import java.util.Collection;
import java.util.List;

public class JCRObjectType implements ObjectType {

  /** . */
  private final String name;

  /** . */
  List<JCRInheritanceRelationshipDescriptor> extendsRelationships;

  /** . */
  List<JCRHierarchicalRelationshipDescriptor> childrenRelationships;

  /** . */
  List<JCRPropertyDescriptor> properties;

  public JCRObjectType(String name) {
    this.name = name;
    this.extendsRelationships = null;
    this.childrenRelationships = null;
    this.properties = null;
  }

  public String getName() {
    return name;
  }

  public Collection<? extends PropertyDescriptor> getProperties() {
    return properties;
  }

  public PropertyDescriptor getProperty(String name) throws NullPointerException {
    if (name == null) {
      throw new NullPointerException();
    }
    if (!properties.isEmpty()) {
      for (JCRPropertyDescriptor property : properties) {
        if (property.getName().equals(name)) {
          return property;
        }
      }
    }
    return null;
  }

  public Collection<? extends HierarchicalRelationshipDescriptor> getChildrenRelationships() {
    return childrenRelationships;
  }

  public Collection<? extends InheritanceRelationshipDescriptor> getSuperRelationships() {
    return extendsRelationships;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + name + "]";
  }
}
