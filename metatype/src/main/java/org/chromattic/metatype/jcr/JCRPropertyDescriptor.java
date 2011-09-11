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

import org.chromattic.metatype.PropertyDescriptor;
import org.chromattic.metatype.DataType;

public class JCRPropertyDescriptor<V> implements PropertyDescriptor<V> {

  public static <V> JCRPropertyDescriptor<V> create(String name, DataType<V> valueType, boolean singleValued) {
    return new JCRPropertyDescriptor<V>(name, valueType, singleValued);
  }

  /** . */
  private final String name;

  /** . */
  private final DataType<V> valueType;

  /** . */
  private final boolean singleValued;

  public JCRPropertyDescriptor(String name, DataType<V> valueType, boolean singleValued) {
    this.name = name;
    this.valueType = valueType;
    this.singleValued = singleValued;
  }

  public String getName() {
    return name;
  }

  public DataType<V> getValueType() {
    return valueType;
  }

  public boolean isMultiValued() {
    return !singleValued;
  }

  public boolean isSingleValued() {
    return singleValued;
  }
}
