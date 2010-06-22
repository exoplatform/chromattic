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

import org.reflext.api.TypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueInfo {

  /** The property type as declared originally. */
  private final TypeInfo declaredType;

  /** The effective property type. */
  private final TypeInfo effectiveType;

  protected ValueInfo(TypeInfo declaredType, TypeInfo effectiveType) {
    if (declaredType == null) {
      throw new NullPointerException("No null declared type accepted");
    }
    if (effectiveType == null) {
      throw new NullPointerException("No null effective type accepted");
    }

    //
    this.declaredType = declaredType;
    this.effectiveType = effectiveType;
  }

  public TypeInfo getEffectiveType() {
    return effectiveType;
  }

  public TypeInfo getDeclaredType() {
    return declaredType;
  }
}
