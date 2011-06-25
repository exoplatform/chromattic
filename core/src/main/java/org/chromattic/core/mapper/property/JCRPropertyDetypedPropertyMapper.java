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

package org.chromattic.core.mapper.property;

import org.chromattic.core.EntityContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.PropertiesMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyDetypedPropertyMapper<V extends ValueInfo, O extends ObjectContext<O>>
  extends PropertyMapper<PropertyInfo<V, ValueKind.Map>, V, O, ValueKind.Map> {

  /** . */
  final String namePattern;

  /** . */
  final String namePrefix;

  /** . */
  final ValueKind valueKind;

  public JCRPropertyDetypedPropertyMapper(Class<O> contextType, PropertiesMapping<V> info) {
    super(contextType, info);

    //
    String prefix = info.getPrefix();
    String namePrefix;
    String namePattern;
    if (prefix != null && prefix.length() > 0) {
      namePrefix = prefix + ":";
      namePattern = prefix + ":*";
    } else {
      namePrefix = null;
      namePattern = null;
    }

    //
    this.namePattern = namePattern;
    this.namePrefix = namePrefix;
    this.valueKind = info.getValueKind();
  }

  @Override
  public Object get(O context) throws Throwable {
    EntityContext entity = context.getEntity();
    Object collection = entity.getAttribute(this);
    if (collection == null) {
      collection = new PropertyMap(this, entity);
      entity.setAttribute(this, collection);
    }
    return collection;
  }
}