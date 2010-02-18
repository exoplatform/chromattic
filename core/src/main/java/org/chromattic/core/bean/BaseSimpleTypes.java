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

import org.chromattic.core.bean.SimpleTypeKind;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BaseSimpleTypes {

  public static SimpleTypeKind.STRING<String> STRING = new SimpleTypeKind.STRING<String>() {
  };

  public static SimpleTypeKind.PATH<String> PATH = new SimpleTypeKind.PATH<String>() {
  };

  public static SimpleTypeKind.LONG<Integer> INT = new SimpleTypeKind.LONG<Integer>() {
  };

  public static SimpleTypeKind.BOOLEAN<Boolean> BOOLEAN = new SimpleTypeKind.BOOLEAN<Boolean>() {
  };

  public static SimpleTypeKind.LONG<Long> LONG = new SimpleTypeKind.LONG<Long>() {
  };

  public static SimpleTypeKind.DATE<Date> DATE = new SimpleTypeKind.DATE<Date>() {
  };

  public static SimpleTypeKind.DOUBLE<Double> DOUBLE = new SimpleTypeKind.DOUBLE<Double>() {
  };

  public static SimpleTypeKind.DOUBLE<Float> FLOAT = new SimpleTypeKind.DOUBLE<Float>() {
  };

  public static SimpleTypeKind.STREAM<InputStream> STREAM = new SimpleTypeKind.STREAM<InputStream>() {
  };

  public static Map<Class<?>, SimpleTypeKind<?, ?>> TYPES;

  static {
    Map<Class<?>, SimpleTypeKind<?, ?>> baseTypes = new HashMap<Class<?>, SimpleTypeKind<?, ?>>();
    baseTypes.put(String.class, STRING);
    baseTypes.put(Integer.class, INT);
    baseTypes.put(Boolean.class, BOOLEAN);
    baseTypes.put(Long.class, LONG);
    baseTypes.put(Date.class, DATE);
    baseTypes.put(Double.class, DOUBLE);
    baseTypes.put(Float.class, FLOAT);
    baseTypes.put(InputStream.class, STREAM);

    //
    TYPES = Collections.unmodifiableMap(baseTypes);
  }
}
