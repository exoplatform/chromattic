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

package org.chromattic.core.generator;

import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.mapping.*;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.spi.jcr.NodeTypeVisitor;
import org.reflext.api.ClassTypeInfo;

import javax.jcr.PropertyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeBuilder extends BaseTypeMappingVisitor {

  /** . */
  private final NodeTypeVisitor builder;

  private SetMap<String, ClassTypeInfo> childNodeDefinitions;

  public NodeTypeBuilder(NodeTypeVisitor builder) {
    this.builder = builder;
  }

  @Override
  protected void startMapping(NodeTypeMapping mapping) {
    childNodeDefinitions = new SetMap<String, ClassTypeInfo>();
    builder.startType(mapping.getTypeName(), mapping instanceof PrimaryTypeMapping);
  }

  @Override
  protected void propertyMapping(JCRPropertyMapping propertyMapping, PropertyInfo<SimpleValueInfo> propertyInfo) {

/*
    int propertyType;
    SimpleValueInfo simpleValueInfo = propertyInfo.getValue();
    SimpleType stk = simpleValueInfo.getSimpleType();
    if (stk instanceof SimpleTypeKind.STRING) {
      propertyType = PropertyType.STRING;
    } else if (stk instanceof SimpleTypeKind.LONG) {
      propertyType = PropertyType.LONG;
    } else if (stk instanceof SimpleTypeKind.PATH) {
      propertyType = PropertyType.PATH;
    } else if (stk instanceof SimpleTypeKind.DATE) {
      propertyType = PropertyType.DATE;
    } else if (stk instanceof SimpleTypeKind.BOOLEAN) {
      propertyType = PropertyType.BOOLEAN;
    } else if (stk instanceof SimpleTypeKind.DOUBLE) {
      propertyType = PropertyType.DOUBLE;
    } else if (stk instanceof SimpleTypeKind.STREAM) {
      propertyType = PropertyType.BINARY;
    } else {
      throw new AssertionError();
    }

    //
    builder.addProperty(propertyMapping.getName(), propertyInfo instanceof MultiValuedPropertyInfo, propertyType);
*/
    throw new AssertionError("investigate");
  }

  @Override
  protected void propertyMapMapping() {
    builder.addProperty("*", false, PropertyType.UNDEFINED);
  }

  @Override
  protected void oneToManyByReference(String relatedName) {
  }

  @Override
  protected void oneToManyByPath(String relatedName) {
  }

  @Override
  protected void oneToManyHierarchic(ClassTypeInfo relatedType) {
    childNodeDefinitions.get("*").add(relatedType);
  }

  @Override
  protected void manyToOneByReference(String name, ClassTypeInfo relatedType) {
    builder.addProperty(name, false, PropertyType.REFERENCE);
  }

  @Override
  protected void manyToOneByPath(String name, ClassTypeInfo relatedType) {
    builder.addProperty(name, false, PropertyType.PATH);
  }

  @Override
  protected void manyToOneHierarchic(ClassTypeInfo relatedType) {
  }

  @Override
  protected void oneToOneHierarchic(String name, ClassTypeInfo relatedType) {
    childNodeDefinitions.get(name).add(relatedType);
  }

  @Override
  protected void endMapping() {
    // Now process child node definitions
    for (String childName : childNodeDefinitions.keySet()) {

      // Try to find the common ancestor type of all types
      ClassTypeInfo ancestorType = null;
      foo:
      for (ClassTypeInfo relatedType1 : childNodeDefinitions.peek(childName)) {
        for (ClassTypeInfo relatedType2 : childNodeDefinitions.peek(childName)) {
          if (!relatedType1.isAssignableFrom(relatedType2)) {
            continue foo;
          }
        }
        ancestorType = relatedType1;
        break;
      }

      //
      String typeName;
      if (ancestorType == null) {
        typeName = "nt:base";
      } else {
        NodeTypeMapping ancestorMapping = getMapping(ancestorType);
        if (ancestorMapping == null) {
          typeName = "nt:base";
        } else {
          typeName = ancestorMapping.getTypeName();
        }
      }

      //
      builder.addChildNodeDefinition(childName, typeName);
    }

    //
    builder.endType();
  }
}
