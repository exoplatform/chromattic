/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.mapping2;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingVisitor {
  
  public void startBean(BeanMapping mapping) { }

  public void singleValueMapping(ValueMapping.Single mapping) { }

  public void multiValueMapping(ValueMapping.Multi mapping) { }

  public void propertiesMapping(PropertiesMapping<?> mapping) { }

  public void attributeMapping(AttributeMapping mapping) { }

  public void oneToOneHierarchic(RelationshipMapping.OneToOne.Hierarchic mapping) { }

  public void oneToManyHierarchic(RelationshipMapping.OneToMany.Hierarchic mapping) { }

  public void manyToOneHierarchic(RelationshipMapping.ManyToOne.Hierarchic mapping) { }

  public void oneToManyReference(RelationshipMapping.OneToMany.Reference mapping) { }

  public void manyToOneReference(RelationshipMapping.ManyToOne.Reference mapping) { }

  public void oneToOneEmbedded(RelationshipMapping.OneToOne.Embedded mapping) { }

  public void visit(CreateMapping mapping) { }

  public void visit(DestroyMapping mapping) { }

  public void visit(FindByIdMapping mapping) { }

  public void endBean() { }

}
