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

import org.reflext.api.MethodInfo;
import org.chromattic.metamodel.bean.AccessMode;
import org.reflext.api.introspection.AnnotationIntrospector;

import java.lang.annotation.Annotation;


/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PropertyInfo<V extends ValueInfo> {

  /** . */
  private final String name;

  /** . */
  private final MethodInfo getter;

  /** . */
  private final MethodInfo setter;

  /** . */
  private final V value;

  public PropertyInfo(String name, V value, MethodInfo getter, MethodInfo setter) {
    this.name = name;
    this.value = value;
    this.getter = getter;
    this.setter = setter;
  }

  public String getName() {
    return name;
  }

  public V getValue() {
    return value;
  }

  public AccessMode getAccessMode() {
    if (getter == null) {
      if (setter == null) {
        throw new AssertionError("wtf");
      } else {
        return AccessMode.WRITE_ONLY;
      }
    } else {
      if (setter == null) {
        return AccessMode.READ_ONLY;
      } else {
        return AccessMode.READ_WRITE;
      }
    }
  }

  public MethodInfo getGetter() {
    return getter;
  }

  public MethodInfo getSetter() {
    return setter;
  }

  public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
    if (annotationClass == null) {
      throw new NullPointerException();
    }

    //
    A annotation = null;

    //
    if (getter != null) {
      annotation = new AnnotationIntrospector<A>(annotationClass).resolve(getter);
    }

    //
    if (setter != null) {
      A setterAnnotation = new AnnotationIntrospector<A>(annotationClass).resolve(setter);
      if (setterAnnotation != null) {
        if (annotation != null) {
          throw new IllegalStateException("The same annotation " + annotation + " is present on a getter " +
            getter + " and setter" + setter);
        }
        annotation = setterAnnotation;
      }
    }

    //
    return annotation;
  }

  @Override
  public String toString() {
    return "Property[name=" + name + "]";
  }
}
