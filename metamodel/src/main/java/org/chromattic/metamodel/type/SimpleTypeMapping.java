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

package org.chromattic.metamodel.type;

import org.chromattic.metatype.DataType;
import org.chromattic.spi.type.SimpleTypeProvider;

/**
 * The mapping between a JCR property type and a simple type. A simple type is any class
 * that can be converted back and forth to a particular JCR type.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface SimpleTypeMapping {

  /**
   * Returns the property meta type.
   *
   * @return the property meta type
   */
  DataType<?> getPropertyMetaType();

  /**
   * Create a simple type provider for this mapping. Note that this operation is only guaranted during the runtime
   * phase as it requires to load the {@code org.chromattic.spi.type.SimpleTypeProvider} class.
   *
   * @return the simple type provider
   */
  SimpleTypeProvider<?, ?> create();

}
