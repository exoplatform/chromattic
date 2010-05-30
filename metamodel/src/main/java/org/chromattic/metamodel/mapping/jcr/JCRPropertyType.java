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

package org.chromattic.metamodel.mapping.jcr;

import javax.jcr.PropertyType;
import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 * @param <T> the java type modeling the JCR type
 */
public class JCRPropertyType<T> {

  /** . */
  public static final JCRPropertyType<String> STRING = new JCRPropertyType<String>(String.class, PropertyType.STRING);

  /** . */
  public static final JCRPropertyType<String> PATH = new JCRPropertyType<String>(String.class, PropertyType.PATH);

  /** . */
  public static final JCRPropertyType<String> NAME = new JCRPropertyType<String>(String.class, PropertyType.NAME);

  /** . */
  public static final JCRPropertyType<Long> LONG = new JCRPropertyType<Long>(Long.class, PropertyType.LONG);

  /** . */
  public static final JCRPropertyType<Double> DOUBLE = new JCRPropertyType<Double>(Double.class, PropertyType.DOUBLE); 

  /** . */
  public static final JCRPropertyType<Boolean> BOOLEAN = new JCRPropertyType<Boolean>(Boolean.class, PropertyType.BOOLEAN);

  /** . */
  public static final JCRPropertyType<InputStream> BINARY = new JCRPropertyType<InputStream>(InputStream.class, PropertyType.BINARY);

  /** . */
  public static final JCRPropertyType<Date> DATE = new JCRPropertyType<Date>(Date.class, PropertyType.DATE);

  private static final JCRPropertyType<?>[] ALL = {
    STRING,
    PATH,
    NAME,
    LONG,
    DOUBLE,
    BOOLEAN,
    BINARY,
    DATE
  };

  public static JCRPropertyType<?> get(int code) {
    for (JCRPropertyType<?> pt : ALL) {
      if (pt.code == code) {
        return pt;
      }
    }
    return null;
  }

  /** . */
  private final Class<T> javaType;

  /** . */
  private final int code;

  private JCRPropertyType(Class<T> javaType, int code) {
    this.javaType = javaType;
    this.code = code;
  }

  public Class<T> getJavaType() {
    return javaType;
  }

  public int getCode() {
    return code;
  }
}
