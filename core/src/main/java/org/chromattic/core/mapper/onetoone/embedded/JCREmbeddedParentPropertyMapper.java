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
package org.chromattic.core.mapper.onetoone.embedded;

import org.chromattic.core.EmbeddedContext;
import org.chromattic.core.EntityContext;
import org.chromattic.core.mapper.RelatedPropertyMapper;
import org.chromattic.metamodel.bean.BeanValueInfo;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.ValueKind;
import org.chromattic.metamodel.mapping.RelationshipMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCREmbeddedParentPropertyMapper extends
    RelatedPropertyMapper<PropertyInfo<BeanValueInfo, ValueKind.Single>, BeanValueInfo, EntityContext, ValueKind.Single> {

  /** . */
  private final Class relatedClass;

  public JCREmbeddedParentPropertyMapper(RelationshipMapping.OneToOne.Embedded info) throws ClassNotFoundException {
    super(EntityContext.class, info);

    // We use the classloader from the bean
    Class<?> clazz = (Class<?>)info.getOwner().getBean().getClassType().unwrap();
    ClassLoader cl = clazz.getClassLoader();

    //
    this.relatedClass = cl.loadClass(info.getValue().getClassType().getName());
  }

  @Override
  public Class<?> getRelatedClass() {
    return relatedClass;
  }

  @Override
  public Object get(EntityContext context) throws Throwable {
    EmbeddedContext mixinCtx = context.getEmbedded(relatedClass);
    return mixinCtx != null ? mixinCtx.getObject() : null;
  }

  @Override
  public void set(EntityContext context, Object value) throws Throwable {
    if (value == null) {
      context.removeMixin(relatedClass);
    } else {
      if (!relatedClass.isInstance(value)) {
        throw new ClassCastException();
      }
      EmbeddedContext mixinCtx = context.getSession().unwrapMixin(value);
      context.addMixin(mixinCtx);
    }
  }
}