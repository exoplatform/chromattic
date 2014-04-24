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

package org.chromattic.core.mapper.onetoone.hierarchical;

import org.chromattic.common.logging.Logger;
import org.chromattic.core.EntityContext;
import org.chromattic.core.NameKind;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.ThrowableFactory;
import org.chromattic.core.mapper.JCRNodePropertyMapper;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRNamedChildParentPropertyMapper<O extends ObjectContext<O>> extends JCRNodePropertyMapper<PropertyInfo<BeanValueInfo, ValueKind.Single>, BeanValueInfo, O> {

  /** . */
  private String relatedName;

  /** . */
  private final String relatedPrefix;

  /** . */
  private final Logger log = Logger.getLogger(JCRNamedChildParentPropertyMapper.class);

  public JCRNamedChildParentPropertyMapper(
      Class<O> contextType,
      RelationshipMapping.OneToOne.Hierarchic info) throws ClassNotFoundException {
    super(contextType, info);

    //
    this.relatedName = info.getLocalName();
    this.relatedPrefix = info.getPrefix();
  }

  @Override
  public Object get(O ctx) throws Throwable {
    // Decode name
    EntityContext entityCtx = ctx.getEntity();

    //
    String externalRelatedName = entityCtx.decodeName(relatedName);

    //
    EntityContext childCtx = entityCtx.getChild(relatedPrefix, externalRelatedName);
    if (childCtx != null) {
      Object o = childCtx.getObject();
      Class<?> relatedClass = getRelatedClass();
      if (relatedClass.isInstance(o)) {
        return o;
      } else {
        throw new ClassCastException();
      }
    } else {
      return null;
    }
  }

  @Override
  public void set(O context, Object child) throws Throwable {
    EntityContext entity = context.getEntity();

    // Decode name
    String externalRelatedName = entity.decodeName(relatedName);

    if (child != null) {
      EntityContext entityCtx = entity.getSession().unwrapEntity(child);
      entity.addChild(ThrowableFactory.TODO, ThrowableFactory.ISE, relatedPrefix, externalRelatedName, entityCtx);
    } else {
      entity.removeChild(relatedPrefix, externalRelatedName);
    }
  }
}