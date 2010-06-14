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

import org.chromattic.metamodel.bean2.BeanValueInfo;
import org.chromattic.metamodel.bean2.PropertyInfo;
import org.chromattic.metamodel.bean2.SimpleValueInfo;
import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingVisitor {
  
  public void start() { }

  public void startMapping(BeanMapping mapping) { }

  public void valueMapping(PropertyInfo<SimpleValueInfo> property, PropertyDefinitionMapping definition) { }

  public void oneToOneHierarchic(PropertyInfo<BeanValueInfo> property, String mappedBy, boolean owner) { }


/*


  public void propertyMapMapping(ClassTypeInfo definer, PropertyMetaType metaType, boolean skip) { }

  public void oneToManyByReference(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping, boolean skip) { }

  public void oneToManyByPath(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping, boolean skip) { }

  public void oneToManyHierarchic(NodeTypeMapping definer, String propertyName, NodeTypeMapping relatedMapping) { }

  public void manyToOneByReference(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping, boolean skip) { }

  public void manyToOneByPath(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping, boolean skip) { }

  public void manyToOneHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) { }

  public void oneToOneHierarchic(NodeTypeMapping definerMapping, String name, NodeTypeMapping relatedMapping, boolean owning, Set<AttributeOption> attributes, String propertyName) { }

  public void oneToOneEmbedded(ClassTypeInfo definer, NodeTypeMapping relatedMapping, boolean owner) { }
*/
  public void endMapping() { }

  public void end() { }
  
}
