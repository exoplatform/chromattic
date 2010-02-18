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

import org.chromattic.core.bean.SimpleValueInfo;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTypeFactory {

  public static <V> ValueType<V> create(final SimpleValueInfo<V> sv) {

    return new ValueType<V>() {
      @Override
      public List<V> getDefaultValue() {
        return sv.getDefaultValue();
      }

      @Override
      public boolean isPrimitive() {
        return sv.getSimpleType().isPrimitive();
      }

      @Override
      public V get(Value value) throws RepositoryException {
        return ValueMapper.instance.get(value, sv.getSimpleType());
      }

      @Override
      public Value get(ValueFactory valueFactory, V o) throws ValueFormatException {
        return ValueMapper.instance.get(valueFactory, o, sv.getSimpleType());
      }

      @Override
      public Class<V> getObjectType() {
        return sv.getSimpleType().getObjectType();
      }

      @Override
      public Class<?> getRealType() {
        return sv.getSimpleType().getRealType();
      }
    };

  }

}
