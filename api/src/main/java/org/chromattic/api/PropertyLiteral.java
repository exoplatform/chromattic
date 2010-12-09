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

package org.chromattic.api;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyLiteral<O, T> {

  /** . */
  private final Class<O> owner;

  /** . */
  private final String name;

  /** . */
  private final Class<T> type;

  public PropertyLiteral(Class<O> owner, String name, Class<T> type) {
    if (owner == null) {
      throw new NullPointerException("No null owner type accepted");
    }
    if (name == null) {
      throw new NullPointerException("No null name accepted");
    }
    if (type == null) {
      throw new NullPointerException("No null type accepted");
    }

    //
    this.owner = owner;
    this.name = name;
    this.type = type;
  }

  public Class<O> getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public Class<T> getType() {
    return type;
  }

  @Override
  public String toString() {
    return "PropertyLiteral[owner=" + owner.getName() + ",name=" + name + ",type=" + type.getName() + "]";
  }
}
