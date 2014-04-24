/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.chromattic.core.mapper.property;

import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.bean.ValueKind.Single;
import org.chromattic.metamodel.mapping.PropertyMapping;

/**
 * @author <a href="mailto:nfilotto@exoplatform.com">Nicolas Filotto</a>
 * @version $Id$
 *
 */
public abstract class JCRPropertyMapper<O extends ObjectContext<O>, E, I, K extends ValueKind> extends
  PropertyMapper<PropertyInfo<SimpleValueInfo<K>, ValueKind.Single>, SimpleValueInfo<K>, O, ValueKind.Single> {

  public JCRPropertyMapper(Class<O> contextType,
    PropertyMapping<PropertyInfo<SimpleValueInfo<K>, Single>, SimpleValueInfo<K>, Single> info) {
    super(contextType, info);
  }

  /**
   * Gives the name of the corresponding property to load
   */
  public abstract String getJCRPropertyName();

  /**
   * Gives the related value definition
   */
  public abstract ValueDefinition<I, E> getValueDefinition();
}
