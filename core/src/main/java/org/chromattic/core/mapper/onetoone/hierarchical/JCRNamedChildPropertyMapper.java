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

import org.chromattic.core.DomainSession;
import org.chromattic.core.EntityContext;
import org.chromattic.core.NameKind;
import org.chromattic.core.ThrowableFactory;
import org.chromattic.core.mapper.JCRChildNodePropertyMapper;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRNamedChildPropertyMapper extends JCRChildNodePropertyMapper<PropertyInfo<BeanValueInfo, ValueKind.Single>> {

  /** . */
  private final String relatedName;

  /** . */
  private final String relatedPrefix;

  public JCRNamedChildPropertyMapper(RelationshipMapping.OneToOne.Hierarchic info) throws ClassNotFoundException {
    super(info);

    //
    this.relatedName = info.getLocalName();
    this.relatedPrefix = info.getPrefix();
  }

  @Override
  public Object get(EntityContext context) throws Throwable {
    EntityContext parentCtx = context.getParent();

    //
    if (parentCtx != null) {

      // Decode name
      String externalRelatedName = parentCtx.decodeName(relatedName, NameKind.OBJECT);

      //
      EntityContext parentChildWithRelatedNameCtx = parentCtx.getChild(relatedPrefix, externalRelatedName);

      // Find out if we are mapped on this parent by the related name
      if (parentChildWithRelatedNameCtx == context) {
        Class<?> relatedClass =  getRelatedClass();
        return parentCtx.adapt(relatedClass);
      }
    }

    //
    return null;
  }

  @Override
  public void set(EntityContext context, Object parent) throws Throwable {
    if (parent == null) {
      context.remove();
    } else {
      DomainSession session = context.getSession();

      // Get parent context
      EntityContext parentCtx = session.unwrapEntity(parent);

      // Decode name
      String externalRelatedName = parentCtx.decodeName(relatedName, NameKind.OBJECT);

      //
      parentCtx.addChild(ThrowableFactory.TODO, ThrowableFactory.ISE, relatedPrefix, externalRelatedName, context);
    }
  }
}