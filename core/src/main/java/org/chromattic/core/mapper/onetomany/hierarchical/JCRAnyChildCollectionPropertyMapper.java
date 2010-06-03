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

import org.chromattic.core.DomainSession;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.mapper.JCRChildNodePropertyMapper;
import org.chromattic.core.EntityContext;
import org.chromattic.metamodel.bean.PropertyQualifier;
import org.chromattic.metamodel.bean.value.BeanValueInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRAnyChildCollectionPropertyMapper<O extends ObjectContext> extends JCRChildNodePropertyMapper {

  public JCRAnyChildCollectionPropertyMapper(PropertyQualifier<BeanValueInfo> info) throws ClassNotFoundException {
    super(info);
  }

  @Override
  public Object get(EntityContext context) throws Throwable {
    EntityContext parentCtx = context.getParent();
    if (parentCtx != null) {
      Class<?> relatedClass =  getRelatedClass();
      return parentCtx.adapt(relatedClass);
    }
    return null;
  }

  @Override
  public void set(EntityContext context, Object parent) throws Throwable {
    if (parent == null) {
      // context.remove();
      throw new UnsupportedOperationException("todo remove parent");
    } else {
      DomainSession session = context.getSession();
      EntityContext parentContext = session.unwrapEntity(parent);
      parentContext.addChild(context);
    }
  }
}
