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

package org.chromattic.core.vt2;

import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.metamodel.type.PropertyTypeResolver;
import org.chromattic.spi.type.ValueType;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTypeFactory {

  public static <I> ValueType<I, ?> create(TypeInfo type, JCRPropertyType<I> jcrType) {
    PropertyTypeResolver resolver = new PropertyTypeResolver();

    //
    ValueType vt = resolver.resolveValueType(type);

    //
    if (!vt.getInternalType().equals(jcrType.getJavaType())) {
      throw new AssertionError("todo with type " + type + " / property type" + vt);
    }

    //
    return (ValueType<I, ?>)vt;
  }
}
