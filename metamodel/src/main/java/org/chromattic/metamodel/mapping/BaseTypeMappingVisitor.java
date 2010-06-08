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

package org.chromattic.metamodel.mapping;

import org.chromattic.api.RelationshipType;
import org.chromattic.metamodel.bean.value.MultiValueInfo;
import org.chromattic.metamodel.bean.value.TypeKind;
import org.chromattic.metamodel.bean.value.ValueInfo;
import org.chromattic.metamodel.mapping.jcr.JCRMemberMapping;
import org.chromattic.metamodel.mapping.jcr.JCRNodeAttributeMapping;
import org.chromattic.metamodel.mapping.jcr.JCRPropertyMapping;
import org.chromattic.metamodel.mapping.value.*;
import org.chromattic.metamodel.bean.*;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BaseTypeMappingVisitor {

  /** . */
  private final TypeMappingDomain builder;

  public BaseTypeMappingVisitor() {
    builder = new TypeMappingDomain(false);
  }

  public void addType(ClassTypeInfo cti) {
    builder.add(cti);
  }

  protected NodeTypeMapping getMapping(ClassTypeInfo type) {
    return builder.get(type);
  }

  protected void start() {}

  protected void startMapping(NodeTypeMapping mapping) {}

  protected <V> void propertyMapping(
    ClassTypeInfo definer,
    JCRPropertyMapping propertyMapping,
    boolean multiple) {}

  protected void propertyMapMapping(ClassTypeInfo definer) {}

  protected void oneToManyByReference(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {}

  protected void oneToManyByPath(ClassTypeInfo definer, String relatedName, NodeTypeMapping relatedMapping) {}

  protected void oneToManyHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {}

  protected void manyToOneByReference(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping) {}

  protected void manyToOneByPath(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping) {}

  protected void manyToOneHierarchic(ClassTypeInfo definer, NodeTypeMapping relatedMapping) {}

  protected void oneToOneHierarchic(ClassTypeInfo definer, String name, NodeTypeMapping relatedMapping, int mode) {}

  protected void oneToOneEmbedded(ClassTypeInfo definer, NodeTypeMapping relatedMapping, boolean owner) {}

  protected void endMapping() {}

  protected void end() {}

  public void generate() {

    start();

    for (NodeTypeMapping mapping : builder.build()) {

      startMapping(mapping);

      //
      for (PropertyMapping<? extends ValueMapping> propertyMapping : mapping.getPropertyMappings()) {

        ValueMapping valueMapping = propertyMapping.getValueMapping();

        ClassTypeInfo definer = valueMapping.getDefiner().getType();
        if (valueMapping instanceof SimpleMapping) {
          SimpleMapping<?> simpleMapping = (SimpleMapping)valueMapping;
          PropertyQualifier<? extends ValueInfo> propertyInfo = propertyMapping.getInfo();
          JCRMemberMapping memberMapping = simpleMapping.getJCRMember();
          ValueInfo valueInfo = propertyInfo.getValue();
          if (memberMapping instanceof JCRPropertyMapping) {
            boolean multiple;
            if (valueInfo instanceof MultiValueInfo) {
              valueInfo = ((MultiValueInfo<?>)valueInfo).getElement();
              multiple = true;
            } else {
              multiple = false;
            }
            if (valueInfo.getKind() == TypeKind.SIMPLE) {
              propertyMapping(
                definer,
                (JCRPropertyMapping)memberMapping,
                multiple);
            } else {
              // WTF ?
              throw new AssertionError();
            }
          } else if (memberMapping instanceof JCRNodeAttributeMapping) {
            if (valueInfo.getKind() == TypeKind.SIMPLE) {
              TypeInfo simpleType = valueInfo.getTypeInfo();
              if (simpleType instanceof ClassTypeInfo && ((ClassTypeInfo)simpleType).getName().equals(String.class.getName())) {
                // OK
              } else {
                throw new AssertionError(mapping.getType().toString() + " wrong simple type "+ simpleType);
              }
            } else {
              throw new AssertionError();
            }
          } else {
            throw new AssertionError(mapping.getType());
          }
        } else if (valueMapping instanceof RelationshipMapping) {
          RelationshipMapping<?, ?> relationshipMapping = (RelationshipMapping<?, ?>)valueMapping;
          NodeTypeMapping relatedMapping = relationshipMapping.getRelatedMapping();
          RelationshipType type = relationshipMapping.getType();
          if (valueMapping instanceof AbstractOneToManyMapping<?, ?>) {
            if (valueMapping instanceof NamedOneToManyMapping) {
              NamedOneToManyMapping namedOneToManyMapping = (NamedOneToManyMapping)valueMapping;
              switch (type) {
                case REFERENCE:
                  oneToManyByReference(definer, namedOneToManyMapping.getName(), relatedMapping);
                  break;
                case PATH:
                  oneToManyByPath(definer, namedOneToManyMapping.getName(), relatedMapping);
                  break;
                default:
                  throw new AssertionError();
              }
            } else {
              switch (type) {
                case HIERARCHIC:
                  oneToManyHierarchic(definer, relationshipMapping.getRelatedMapping());
                  break;
                default:
                  throw new AssertionError();
              }
            }
          } else if (valueMapping instanceof AbstractManyToOneMapping<?, ?>) {
            if (valueMapping instanceof NamedManyToOneMapping) {
              NamedManyToOneMapping namedManyToOneMapping = (NamedManyToOneMapping)valueMapping;
              String name = namedManyToOneMapping.getRelatedName();
              switch (type) {
                case REFERENCE:
                  manyToOneByReference(definer, name, relatedMapping);
                  break;
                case PATH:
                  manyToOneByPath(definer, name, relatedMapping);
                  break;
                default:
                  throw new AssertionError();
              }
            } else {
              switch (type) {
                case HIERARCHIC:
                  manyToOneHierarchic(definer, relatedMapping);
                  break;
                default:
                  throw new AssertionError();
              }
            }
          } else if (valueMapping instanceof AbstractOneToOneMapping<?>) {
            if (valueMapping instanceof NamedOneToOneMapping) {
              NamedOneToOneMapping namedOneToOneMapping = (NamedOneToOneMapping)valueMapping;
              String name = namedOneToOneMapping.getName();
              switch (type) {
                case HIERARCHIC:
                  oneToOneHierarchic(definer, name, relationshipMapping.getRelatedMapping(), namedOneToOneMapping.getMode());
                  break;
                default:
                  throw new AssertionError();
              }
            } else {
              switch (type) {
                case EMBEDDED:
                  OneToOneMapping oneToOneMapping = (OneToOneMapping)valueMapping;
                  oneToOneEmbedded(definer, relationshipMapping.getRelatedMapping(), oneToOneMapping.isOwning());
                  break;
                default:
                  throw new AssertionError();
              }
            }
          } else {
            throw new AssertionError();
          }
        } else if (valueMapping instanceof PropertyMapMapping) {
          propertyMapMapping(definer);
        } else {
          // WTF ?
          throw new AssertionError();
        }
      }

      //
      endMapping();
    }

    end();
  }
}