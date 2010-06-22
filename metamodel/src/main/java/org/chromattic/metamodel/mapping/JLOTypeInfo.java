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

import org.reflext.api.*;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.relationship.TypeRelationship;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class JLOTypeInfo implements ClassTypeInfo {

  /** . */
  private static final JLOTypeInfo instance = new JLOTypeInfo();

  private JLOTypeInfo() {
  }

  public static JLOTypeInfo get() {
    return instance;
  }

  public boolean isReified() {
    return true;
  }

  public String getName() {
    return Object.class.getName();
  }

  public String getSimpleName() {
    return Object.class.getSimpleName();
  }

  public String getPackageName() {
    return Object.class.getPackage().getName();
  }

  public ClassKind getKind() {
    return ClassKind.CLASS;
  }

  public Iterable<TypeInfo> getInterfaces() {
    return Collections.emptyList();
  }

  public ClassTypeInfo getSuperClass() {
    return null;
  }

  public TypeInfo getSuperType() {
    return null;
  }

  public TypeInfo resolve(TypeInfo type) {
    throw new UnsupportedOperationException();
  }

  public List<MethodInfo> getDeclaredMethods() {
    return Collections.emptyList();
  }

  public MethodInfo getDeclaredMethod(MethodSignature methodSignature) {
    return null;
  }

  public <A> A getDeclaredAnnotation(AnnotationType<A, ?> annotationType) {
    return null;
  }

  public boolean isAssignableFrom(ClassTypeInfo that) {
    return false;
  }

  public Object unwrap() {
    return Object.class;
  }

  public List<TypeVariableInfo> getTypeParameters() {
    return Collections.emptyList();
  }

  public <V extends Visitor<V, S>, S extends VisitorStrategy<V, S>> void accept(S strategy, V visitor) {
    strategy.visit(this, visitor);
  }

  public boolean isSubType(TypeInfo typeInfo) {
    return TypeRelationship.SUB_TYPE.isSatisfied(this, typeInfo);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof ClassTypeInfo) {
      ClassTypeInfo that = (ClassTypeInfo)obj;
      String thatName = that.getName();
      return Object.class.getName().equals(thatName);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Object.class.getName().hashCode();
  }
}
