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

import org.chromattic.core.ListType;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.vt2.ValueDefinition;
import org.chromattic.core.vt2.ValueTypeFactory;
import org.chromattic.metamodel.bean.qualifiers.ListPropertyQualifier;
import org.chromattic.metamodel.bean.qualifiers.MultiValuedPropertyQualifier;
import org.chromattic.metamodel.bean.qualifiers.SimpleValueInfo;
import org.chromattic.metamodel.bean.qualifiers.ArrayPropertyQualifier;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyType;
import org.chromattic.spi.type.ValueType;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRPropertyListPropertyMapper<O extends ObjectContext, E, I> extends PropertyMapper<MultiValuedPropertyQualifier<SimpleValueInfo>, O> {

  /** . */
  private final String jcrPropertyName;

  /** . */
  private final ListType listType;

  /** . */
  private final SimpleValueInfo elementType;

  /** . */
  private final ValueDefinition<I, E> vt;

  public JCRPropertyListPropertyMapper(
    Class<O> contextType,
    MultiValuedPropertyQualifier<SimpleValueInfo> info,
    String jcrPropertyName,
    JCRPropertyType<I> propertyType,
    List<String> defaultValue) {
    super(contextType, info);

    //
    ListType listType;
    if (info instanceof ArrayPropertyQualifier) {
      listType = ListType.ARRAY;
    } else if (info instanceof ListPropertyQualifier) {
      listType = ListType.LIST;
    } else {
      throw new AssertionError();
    }

    // YES IT'S UGLY BUT FOR NOW IT'S OK
    ValueType<I, E> vt = (ValueType<I,E>)ValueTypeFactory.create(info.getValue().getTypeInfo(), propertyType);

    //
    this.listType = listType;
    this.jcrPropertyName = jcrPropertyName;
    this.elementType = info.getValue();
    this.vt = new ValueDefinition<I, E>((Class)info.getValue().getTypeInfo().getType(), propertyType, vt, defaultValue);
  }

  @Override
  public Object get(O context) throws Throwable {
    List<E> list = context.getPropertyValues(jcrPropertyName, vt, listType);
    return list == null ? null : listType.unwrap(vt, list);
  }

  @Override
  public void set(O context, Object value) throws Throwable {
    List<E> list = value == null ? null : listType.wrap(vt, value);
    context.setPropertyValues(jcrPropertyName, vt, listType, list);
  }
}
