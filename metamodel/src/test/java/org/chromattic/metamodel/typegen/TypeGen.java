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

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeGen {

  /** . */
  private final TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

  /** . */
  private final NodeTypeBuilder builder = new NodeTypeBuilder();

  public ClassTypeInfo addType(Class<?> type) {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(type);
    builder.addType(typeInfo);
    return typeInfo;
  }

  public NodeType getNodeType(ClassTypeInfo typeInfo) {
    return builder.getNodeType(typeInfo);
  }

  public void generate() {
    builder.generate();
    try {
      StringWriter sw = new StringWriter();
      builder.writeTo(sw);
      System.out.println("sw = " + sw);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
