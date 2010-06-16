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

package org.chromattic.metamodel.bean2;

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueInfo {

  /** The property type as declared originally. */
  private final TypeInfo declaredType;

  /** The property class type. */
  private final ClassTypeInfo classType;

  protected ValueInfo(TypeInfo declaredType, ClassTypeInfo classType) {
    if (declaredType == null) {
      throw new NullPointerException("No null declared type accepted");
    }
    if (classType == null) {
      throw new NullPointerException("No null class type accepted for declared type " + declaredType);
    }

    //
    this.declaredType = declaredType;
    this.classType = classType;
  }

  public TypeInfo getDeclaredType() {
    return declaredType;
  }

  public ClassTypeInfo getClassType() {
    return classType;
  }
}
