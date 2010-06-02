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

import org.reflext.api.ClassTypeInfo;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnnotatedPropertyQualifier<A extends Annotation> extends Annotated<A> {

  /** . */
  private final ClassTypeInfo owner;

  /** . */
  private final PropertyQualifier<?> property;

  public AnnotatedPropertyQualifier(A annotation, ClassTypeInfo owner, PropertyQualifier<?> property) {
    super(annotation);

    //
    this.owner = owner;
    this.property = property;
  }

  public ClassTypeInfo getOwner() {
    return owner;
  }

  public PropertyQualifier<?> getProperty() {
    return property;
  }
}
