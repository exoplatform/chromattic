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

package org.reflext.api.introspection;

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.MethodSignature;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationTarget;
import org.reflext.api.visit.HierarchyScope;
import org.reflext.api.visit.HierarchyVisitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnnotationIntrospector2<A> {

  /** . */
  private final AnnotationType<A, ?> annotationType;

  public AnnotationIntrospector2(AnnotationType<A, ?> annotationType) {
    if (annotationType == null) {
      throw new NullPointerException();
    }

    //
    this.annotationType = annotationType;
  }

  private class Blah implements HierarchyVisitor<Blah> {

    LinkedList<AnnotationTarget<ClassTypeInfo, A>> annotations;

    public boolean enter(ClassTypeInfo type) {
      A annotation = type.getDeclaredAnnotation(annotationType);
      if (annotation != null) {
        if (annotations == null) {
          annotations = new LinkedList<AnnotationTarget<ClassTypeInfo,A>>();
        }
        annotations.add(new AnnotationTarget<ClassTypeInfo,A>(type, annotation));
      }
      return true;
    }

    public void leave(ClassTypeInfo type) {
    }
  }

  public AnnotationTarget<ClassTypeInfo, A> resolve(ClassTypeInfo annotatedType) {
    Blah blah = new Blah();
    annotatedType.accept(HierarchyScope.ALL.<Blah>get(), blah);
    return blah.annotations != null ? blah.annotations.getFirst() : null;
  }

  public List<AnnotationTarget<ClassTypeInfo, A>> resolveAll(ClassTypeInfo annotatedType) {
    Blah blah = new Blah();
    annotatedType.accept(HierarchyScope.ALL.<Blah>get(), blah);
    return blah.annotations != null ? Collections.unmodifiableList(blah.annotations) : Collections.<AnnotationTarget<ClassTypeInfo, A>>emptyList();
  }

  private class Bluh implements HierarchyVisitor<Bluh> {

    /** . */
    private final MethodSignature methodSignature;

    private Bluh(MethodSignature methodSignature) {
      this.methodSignature = methodSignature;
    }

    LinkedList<AnnotationTarget<MethodInfo, A>> annotations;

    public boolean enter(ClassTypeInfo type) {
      MethodInfo m = type.getDeclaredMethod(methodSignature);
      if (m != null) {
        A annotation = m.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
          if (annotations == null) {
            annotations = new LinkedList<AnnotationTarget<MethodInfo,A>>();
          }
          annotations.add(new AnnotationTarget<MethodInfo,A>(m, annotation));
        }
      }
      return true;
    }

    public void leave(ClassTypeInfo type) {
    }
  }

  public AnnotationTarget<MethodInfo, A> resolve(MethodInfo method) {
    return resolve(method.getOwner(), method.getSignature());
  }

  public AnnotationTarget<MethodInfo, A> resolve(ClassTypeInfo declaringType, MethodSignature methodSignature) {
    Bluh bluh = new Bluh(methodSignature);
    declaringType.accept(HierarchyScope.ALL.<Bluh>get(), bluh);
    return bluh.annotations != null ? bluh.annotations.getFirst() : null;
  }

  public List<AnnotationTarget<MethodInfo, A>> resolveAll(ClassTypeInfo declaringType, MethodSignature methodSignature) {
    Bluh bluh = new Bluh(methodSignature);
    declaringType.accept(HierarchyScope.ALL.<Bluh>get(), bluh);
    return bluh.annotations != null ? Collections.unmodifiableList(bluh.annotations) : Collections.<AnnotationTarget<MethodInfo, A>>emptyList();
  }
}
