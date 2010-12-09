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
 * A property literal.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @param <O> the owner type
 * @param <P> the property java type
 */
public class PropertyLiteral<O, P> {

  /** . */
  private final Class<O> owner;

  /** . */
  private final String name;

  /** . */
  private final Class<P> javaType;

  /**
   * Build a new property literal.
   *
   * @param owner the property owner
   * @param name the property name
   * @param javaType the property java type
   * @throws NullPointerException if any argument is null
   */
  public PropertyLiteral(
    Class<O> owner,
    String name,
    Class<P> javaType) throws NullPointerException {
    if (owner == null) {
      throw new NullPointerException("No null owner type accepted");
    }
    if (name == null) {
      throw new NullPointerException("No null name accepted");
    }
    if (javaType == null) {
      throw new NullPointerException("No null java type accepted");
    }

    //
    this.owner = owner;
    this.name = name;
    this.javaType = javaType;
  }

  public Class<O> getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public Class<P> getJavaType() {
    return javaType;
  }

  @Override
  public String toString() {
    return "PropertyLiteral[owner=" + owner.getName() + ",name=" + name + ",type=" + javaType.getName() + "]";
  }
}
