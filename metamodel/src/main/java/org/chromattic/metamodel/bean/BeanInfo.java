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

import org.reflext.api.ClassTypeInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.introspection.AnnotationIntrospector;
import org.reflext.api.introspection.AnnotationTarget;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfo {

  /** . */
  private final ClassTypeInfo typeInfo;

  /** . */
  private final Map<String, PropertyQualifier> properties;

  BeanInfo(ClassTypeInfo typeInfo, Map<String, PropertyQualifier> properties) {
    this.typeInfo = typeInfo;
    this.properties = properties;
  }

  public Set<String> getPropertyNames() {
    return properties.keySet();
  }

  public Collection<PropertyQualifier> getProperties() {
    return properties.values();
  }

  public ClassTypeInfo getTypeInfo() {
    return typeInfo;
  }

  public PropertyQualifier getProperty(String propertyName) {
    return properties.get(propertyName);
  }

  public <A extends Annotation> Collection<AnnotatedPropertyQualifier<A>> findAnnotatedProperties(Class<A> annotationClass) {
    List<AnnotatedPropertyQualifier<A>> matched = new ArrayList<AnnotatedPropertyQualifier<A>>();
    for (PropertyQualifier<?> property : properties.values()) {
      AnnotatedPropertyQualifier<A> propertyAnnotation = property.getAnnotated(annotationClass);
      if (propertyAnnotation != null) {
        matched.add(propertyAnnotation);
      }
    }
    return matched;
  }

  public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    if (annotationClass == null) {
      throw new NullPointerException();
    }
    AnnotationType<A, ?> annotationType = AnnotationType.get(annotationClass);
    AnnotationIntrospector<A> introspector = new AnnotationIntrospector<A>(annotationType);
    AnnotationTarget<ClassTypeInfo,A> annotationTarget = introspector.resolve(typeInfo);
    return annotationTarget != null ? annotationTarget.getAnnotation() : null;
  }
}
