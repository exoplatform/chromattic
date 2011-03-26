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

package org.chromattic.core.mapping;

import org.chromattic.api.format.ObjectFormatter;
import org.reflext.api.ClassTypeInfo;
import org.chromattic.api.NameConflictResolution;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * The meta data for the mapping of a java class to a node type and set of mixins. Note that
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class TypeMapping {

  /** . */
  protected final ClassTypeInfo objectClass;

  /** . */
  private final Set<PropertyMapping> propertyMappings;

  /** . */
  private final Set<MethodMapping> methodMappings;

  /** . */
  private final NameConflictResolution onDuplicate;

  /** . */
  private Class<? extends ObjectFormatter> formatterClass;

  public TypeMapping(
    ClassTypeInfo objectClass,
    Set<PropertyMapping> propertyMappings,
    Set<MethodMapping> methodMappings,
    NameConflictResolution onDuplicate,
    Class<? extends ObjectFormatter> formatterClass) {

    //
    this.objectClass = objectClass;
    this.propertyMappings = Collections.unmodifiableSet(new HashSet<PropertyMapping>(propertyMappings));
    this.methodMappings = Collections.unmodifiableSet(new HashSet<MethodMapping>(methodMappings));
    this.onDuplicate = onDuplicate;
    this.formatterClass = formatterClass;
  }

  public ClassTypeInfo getObjectClass() {
    return objectClass;
  }

  public Class<? extends ObjectFormatter> getFormatterClass() {
    return formatterClass;
  }

  public Set<PropertyMapping> getPropertyMappings() {
    return propertyMappings;
  }

  public Set<MethodMapping> getMethodMappings() {
    return methodMappings;
  }

  public NameConflictResolution getOnDuplicate() {
    return onDuplicate;
  }

  @Override
  public int hashCode() {
    return objectClass.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof TypeMapping) {
      TypeMapping that = (TypeMapping)obj;
      return objectClass.equals(that.objectClass);
    }
    return false;
  }
}