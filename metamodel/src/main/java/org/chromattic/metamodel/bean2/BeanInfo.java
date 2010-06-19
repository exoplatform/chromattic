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

package org.chromattic.metamodel.bean2;

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.AnnotationTarget;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfo {

  /** . */
  BeanInfo parent;

  /** . */
  final ClassTypeInfo classType;

  /** . */
  final Map<String, PropertyInfo<?>> properties;

  /** . */
  final Map<String, PropertyInfo<?>> unmodifiableProperties;

  public BeanInfo(ClassTypeInfo classType) {
    this.classType = classType;
    this.properties = new HashMap<String, PropertyInfo<?>>();
    this.unmodifiableProperties = Collections.unmodifiableMap(properties);
  }

  public BeanInfo getParent() {
    return parent;
  }

  public ClassTypeInfo getClassType() {
    return classType;
  }

  public PropertyInfo<?> getProperty(String name) {
    return properties.get(name);
  }

  public Map<String, PropertyInfo<?>> getProperties() {
    return properties;
  }

  public Collection<? extends Annotation> getAnnotations(Class<? extends Annotation>... annotationClassTypes) {
    List<Annotation> props = new ArrayList<Annotation>();
    for (Class<? extends Annotation> annotationClassType : annotationClassTypes) {
      Annotation annotation = getAnnotation(annotationClassType);
      if (annotation != null) {
        props.add(annotation);
      }
    }
    return props;
  }

  public ClassTypeInfo resolveToClass(TypeInfo type) {
    return Utils.resolveToClassType(classType, type);
  }

  public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    return getAnnotation(AnnotationType.get(annotationClass));
  }

  public <A> A getAnnotation(AnnotationType<A, ?> annotationType) {
    if (annotationType == null) {
      throw new NullPointerException();
    }
    AnnotationIntrospector<A> introspector = new AnnotationIntrospector<A>(annotationType);
    AnnotationTarget<ClassTypeInfo,A> annotationTarget = introspector.resolve(classType);
    return annotationTarget != null ? annotationTarget.getAnnotation() : null;
  }

  @Override
  public String toString() {
    return "BeanInfo[name=" + classType.getName() + "]";
  }
}
