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

package org.chromattic.core.vt;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class StringEnumValueType<E extends Enum<E>> extends BaseValueType.STRING<E> {

  /** . */
  private final Class<E> externalType;

  public StringEnumValueType(List<E> defaultValues, Class<E> externalType) {
    super(defaultValues, externalType, externalType);

    //
    this.externalType = externalType;
  }

  @Override
  public E toExternal(String internal) {
    try {
      return Enum.valueOf(externalType, internal);
    }
    catch (IllegalArgumentException e) {
      throw new IllegalStateException("Enum value cannot be determined from the stored value", e);
    }
  }

  @Override
  public String toInternal(E external) {
    return external.name();
  }
}
