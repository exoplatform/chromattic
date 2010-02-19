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

package org.chromattic.metamodel.typegen;

import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.bean.MultiValuedPropertyInfo;
import org.chromattic.metamodel.bean.SimpleType;
import org.chromattic.metamodel.mapping.*;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.bean.PropertyInfo;
import org.chromattic.metamodel.bean.SimpleValueInfo;
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

  public void start() {
    builder.start();
  }

  @Override
  protected void startMapping(NodeTypeMapping mapping) {
    childNodeDefinitions = new SetMap<String, ClassTypeInfo>();
    builder.startType(mapping.getTypeName(), mapping instanceof PrimaryTypeMapping);
  }

  @Override
  protected void propertyMapping(JCRPropertyMapping propertyMapping, PropertyInfo<SimpleValueInfo> propertyInfo) {

    int propertyType;
    SimpleValueInfo simpleValueInfo = propertyInfo.getValue();
    SimpleType stk = simpleValueInfo.getSimpleType();
    if (stk == SimpleType.STRING) {
      propertyType = PropertyType.STRING;
    } else if (stk == SimpleType.LONG || stk ==SimpleType.PRIMITIVE_LONG) {
      propertyType = PropertyType.LONG;
    } else if (stk == SimpleType.PATH) {
      propertyType = PropertyType.PATH;
    } else if (stk == SimpleType.DATE) {
      propertyType = PropertyType.DATE;
    } else if (stk == SimpleType.BOOLEAN || stk ==SimpleType.PRIMITIVE_BOOLEAN) {
      propertyType = PropertyType.BOOLEAN;
    } else if (stk == SimpleType.INTEGER || stk ==SimpleType.PRIMITIVE_INTEGER) {
      propertyType = PropertyType.LONG;
    } else if (stk == SimpleType.FLOAT || stk ==SimpleType.PRIMITIVE_FLOAT) {
      propertyType = PropertyType.DOUBLE;
    } else if (stk == SimpleType.DOUBLE || stk ==SimpleType.PRIMITIVE_DOUBLE) {
      propertyType = PropertyType.DOUBLE;
    } else if (stk == SimpleType.STREAM) {
      propertyType = PropertyType.BINARY;
    } else if (stk instanceof SimpleType.Enumerated) {
      propertyType = PropertyType.STRING;
    } else {
      throw new AssertionError();
    }

    //
    builder.addProperty(propertyMapping.getName(), propertyInfo instanceof MultiValuedPropertyInfo, propertyType);
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
  protected void oneToOneHierarchic(String name, ClassTypeInfo relatedType, boolean owner) {
    if (owner) {
      childNodeDefinitions.get(name).add(relatedType);
    }
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

  public void end() {
    builder.end();
  }
}
