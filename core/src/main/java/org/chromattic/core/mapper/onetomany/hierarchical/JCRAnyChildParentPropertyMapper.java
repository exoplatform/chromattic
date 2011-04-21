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

package org.chromattic.core.mapper.onetomany.hierarchical;

import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.JCRNodeCollectionPropertyMapper;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRAnyChildParentPropertyMapper<O extends ObjectContext<O>, K extends ValueKind.Multi> extends
    JCRNodeCollectionPropertyMapper<MultiValuedPropertyInfo<BeanValueInfo, K>, O, K> {

  /** . */
  private final AnyChildMultiValueMapper<K> valueMapper;

  /** . */
  private final String prefix;

  public JCRAnyChildParentPropertyMapper(
    Class<O> contextType,
    RelationshipMapping.OneToMany.Hierarchic<K> info,
    AnyChildMultiValueMapper<K> valueMapper) throws ClassNotFoundException {
    super(contextType, info);

    //
    this.valueMapper = valueMapper;
    this.prefix = info.getPrefix();
  }

  // Maybe use generic type here of the multivalue kind
  @Override
  public Object get(O context) throws Throwable {
    return valueMapper.createValue(context.getEntity(), prefix, getRelatedClass());
  }
}