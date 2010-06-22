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

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeGen {

  /** . */
  private final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private Map<ClassTypeInfo, NodeType> schema;

  /** . */
  private final Set<ClassTypeInfo> classTypes = new HashSet<ClassTypeInfo>();

  public ClassTypeInfo addType(Class<?> type) {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(type);
    classTypes.add(typeInfo);
    return typeInfo;
  }

  public NodeType getNodeType(ClassTypeInfo typeInfo) {
    return schema.get(typeInfo);
  }

  public void generate() {
    SchemaBuilder sb = new SchemaBuilder();
    schema = sb.build(classTypes);
  }
}
