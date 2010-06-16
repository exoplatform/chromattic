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

package org.chromattic.core.mapper2.onetomany.reference;

import org.chromattic.api.RelationshipType;
import org.chromattic.core.EntityContext;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.mapper2.JCRNodeCollectionPropertyMapper;
import org.chromattic.metamodel.bean2.BeanValueInfo;
import org.chromattic.metamodel.bean2.MultiValuedPropertyInfo;
import org.chromattic.metamodel.mapping2.RelationshipMapping;

import java.util.EnumMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JCRReferentCollectionPropertyMapper extends JCRNodeCollectionPropertyMapper<MultiValuedPropertyInfo<BeanValueInfo>, EntityContext> {

  /** . */
  final static EnumMap<RelationshipType, LinkType> relationshipToLinkMapping;

  static {
    EnumMap<RelationshipType, LinkType> tmp = new EnumMap<RelationshipType, LinkType>(RelationshipType.class);
    tmp.put(RelationshipType.REFERENCE, LinkType.REFERENCE);
    tmp.put(RelationshipType.PATH, LinkType.PATH);
    relationshipToLinkMapping = tmp;
  }

  /** . */
  final String propertyName;

  /** . */
  final LinkType linkType;

  public JCRReferentCollectionPropertyMapper(
    RelationshipMapping.OneToMany.Reference info) throws ClassNotFoundException {
    super(EntityContext.class, info);

    //
    this.propertyName = info.getMappedBy();
    this.linkType = relationshipToLinkMapping.get(info.getType());
  }

  @Override
  public Object get(final EntityContext context) throws Throwable {
    return new ReferentCollection(context, this);
  }
}