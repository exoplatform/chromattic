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

package org.chromattic.core.bean;

import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class SimpleType<T> {

  /** . */
  public static final SimpleType<String> STRING = new SimpleType<String>(String.class);

  /** . */
  public static final SimpleType<String> PATH = new SimpleType<String>(String.class);

  /** . */
  public static final SimpleType<Integer> INT = new SimpleType<Integer>(Integer.class);

  /** . */
  public static final SimpleType<Boolean> BOOLEAN = new SimpleType<Boolean>(Boolean.class);

  /** . */
  public static final SimpleType<Long> LONG = new SimpleType<Long>(Long.class);

  /** . */
  public static final SimpleType<Date> DATE = new SimpleType<Date>(Date.class);

  /** . */
  public static final SimpleType<Double> DOUBLE = new SimpleType<Double>(Double.class);

  /** . */
  public static final SimpleType<Float> FLOAT = new SimpleType<Float>(Float.class);

  /** . */
  public static final SimpleType<byte[]> BINARY = new SimpleType<byte[]>(byte[].class);

  /** . */
  private final Class<T> javaClass;

  private SimpleType(Class<T> javaClass) {
    this.javaClass = javaClass;
  }
}
