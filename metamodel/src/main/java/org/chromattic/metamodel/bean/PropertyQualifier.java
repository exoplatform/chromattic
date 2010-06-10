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

package org.chromattic.metamodel.bean;

import org.chromattic.metamodel.bean.value.ValueInfo;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.AnnotationTarget;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyQualifier<V extends ValueInfo> {

  /** . */
  private final PropertyRole role;

  /** . */
  private final PropertyInfo property;

  /** . */
  private final V value;

  public PropertyQualifier(PropertyRole role, PropertyInfo property, V value) {
    this.role = role;
    this.property = property;
    this.value = value;
  }

  public PropertyRole getRole() {
    return role;
  }

  public PropertyInfo getProperty() {
    return property;
  }

  public V getValue() {
    return value;
  }

  public Collection<AnnotatedPropertyQualifier<?>> getAnnotateds(Class<? extends Annotation>... annotationClasses) {
    List<AnnotatedPropertyQualifier<?>> props = new ArrayList<AnnotatedPropertyQualifier<?>>();
    for (Class<? extends Annotation> annotationClass : annotationClasses) {
      AnnotatedPropertyQualifier<?> annotation = getAnnotated(annotationClass);
      if (annotation != null) {
        props.add(annotation);
      }
    }
    return props;
  }

  public <A extends Annotation> AnnotatedPropertyQualifier<A> getAnnotated(Class<A> annotationClass) {
    if (annotationClass == null) {
      throw new NullPointerException();
    }

    //
    AnnotationTarget<MethodInfo, A> annotation = null;

    //
    AnnotationType<A, ?> annotationType = AnnotationType.get(annotationClass);

    //
    if (property.getGetter() != null) {
      annotation = new AnnotationIntrospector<A>(annotationType).resolve(property.getGetter());
    }

    //
    if (property.getSetter() != null) {
      AnnotationTarget<MethodInfo, A> setterAnnotation = new AnnotationIntrospector<A>(annotationType).resolve(property.getSetter());
      if (setterAnnotation != null) {
        if (annotation != null) {
          throw new IllegalStateException("The same annotation " + annotation + " is present on a getter " +
            property.getGetter() + " and setter" + property.getSetter());
        }
        annotation = setterAnnotation;
      }
    }

    //
    if (annotation != null) {
      return new AnnotatedPropertyQualifier<A>(annotation.getAnnotation(), annotation.getTarget().getOwner(), this);
    } else {
      return null;
    }
  }
}
