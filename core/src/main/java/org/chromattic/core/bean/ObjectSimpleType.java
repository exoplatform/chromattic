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

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectSimpleType<T> extends SimpleType<T> {

  /** . */
  public static final ObjectSimpleType<String> STRING = new ObjectSimpleType<String>(BaseSimpleTypes.STRING, String.class);

  /** . */
  public static final ObjectSimpleType<String> PATH = new ObjectSimpleType<String>(BaseSimpleTypes.PATH, String.class);

  /** . */
  public static final ObjectSimpleType<Date> DATE = new ObjectSimpleType<Date>(BaseSimpleTypes.DATE, Date.class);

  /** . */
  public static final ObjectSimpleType<InputStream> BINARY = new ObjectSimpleType<InputStream>(BaseSimpleTypes.BINARY, InputStream.class);

  /** . */
  public static final ObjectSimpleType<Integer> INT = new ObjectSimpleType<Integer>(BaseSimpleTypes.INT, Integer.class);

  /** . */
  public static final ObjectSimpleType<Boolean> BOOLEAN = new ObjectSimpleType<Boolean>(BaseSimpleTypes.BOOLEAN, Boolean.class);

  /** . */
  public static final ObjectSimpleType<Long> LONG = new ObjectSimpleType<Long>(BaseSimpleTypes.LONG, Long.class);

  /** . */
  public static final ObjectSimpleType<Double> DOUBLE = new ObjectSimpleType<Double>(BaseSimpleTypes.DOUBLE, Double.class);

  /** . */
  public static final ObjectSimpleType<Float> FLOAT = new ObjectSimpleType<Float>(BaseSimpleTypes.FLOAT, Float.class);

  public ObjectSimpleType(BaseSimpleTypes kind, Class<T> javaType) {
    super(kind, javaType, javaType);
  }

  @Override
  public boolean isPrimitive() {
    return false;
  }
}
