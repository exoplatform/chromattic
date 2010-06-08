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
import org.reflext.api.ParameterizedTypeInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.annotation.AnnotationType;
import org.reflext.api.visit.HierarchyScope;
import org.reflext.api.visit.HierarchyVisitor;
import org.reflext.api.visit.HierarchyVisitorStrategy;

import java.beans.Introspector;
import java.util.*;

/**
 * An introspector for methods of various types. The introspector execution is parameterized by the hierarchy scope
 * that is used to navigate the class and its super classes and implemented interfaces. The introspector execution
 * is parameterized by the remove overrides boolean that tells to keep or not the overriden methods that are redefined
 * in a class.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MethodIntrospector2 {

  /** . */
  private final HierarchyVisitorStrategy strategy;

  /** . */
  private final boolean removeOverrides;

  public MethodIntrospector2(HierarchyVisitorStrategy strategy, boolean removeOverrides) throws NullPointerException {
    if (strategy == null) {
      throw new NullPointerException();
    }

    // OK I think
    this.strategy = strategy;
    this.removeOverrides = removeOverrides;
  }

  /**
   * Creates an introspector with the specified hierarchy scope and the specified removeOverrides parameter.
   *
   * @param hierarchyScope the hierarchy scope
   * @param removeOverrides the remove overrides
   * @throws NullPointerException if the hierarchy scope is null
   */
  public MethodIntrospector2(HierarchyScope hierarchyScope, boolean removeOverrides) throws NullPointerException {
    if (hierarchyScope == null) {
      throw new NullPointerException();
    }

    //
    this.strategy = hierarchyScope.<AbstractScopedHierarchyVisitor>get();
    this.removeOverrides = removeOverrides;
  }

  /**
   * Builds a new introspector with the specified hierarchy scope and a removeOverrides parameter set to false.
   *
   * @param hierarchyScope the hierarchy scope
   * @throws NullPointerException if the hierarchy scope is null
   */
  public MethodIntrospector2(HierarchyScope hierarchyScope) throws NullPointerException {
    this(hierarchyScope, false);
  }

  public <A> Collection<AnnotationTarget<MethodInfo, A>> resolveMethods(ClassTypeInfo cti, AnnotationType<A, ?> annotationClass) {
    ArrayList<AnnotationTarget<MethodInfo, A>> methods = new ArrayList<AnnotationTarget<MethodInfo, A>>();
    org.reflext.api.introspection.AnnotationIntrospector<A> introspector = new AnnotationIntrospector<A>(annotationClass);
    for (MethodInfo method : getMethods(cti)) {
      A annotation = introspector.resolve(method);
      if (annotation != null) {
        methods.add(new AnnotationTarget<MethodInfo,A>(method, annotation));
      }
    }
    return methods;
  }

  /**
   * Returns a map of all method info getters on the specified class type info.
   *
   * @param classTypeInfo the class type info
   * @return an iterable of the method info getters
   * @throws NullPointerException if the specified class type info is null
   */
  public Map<String, MethodInfo> getGetterMap(ClassTypeInfo classTypeInfo) throws NullPointerException {
    if (classTypeInfo == null) {
      throw new NullPointerException();
    }
    Map<String, MethodInfo> getterMap = new HashMap<String, MethodInfo>();
    for (MethodInfo getter : getGetters(classTypeInfo)) {
      String getterName = getter.getName();
      String name;
      if (getterName.startsWith("get")) {
        name = Introspector.decapitalize(getterName.substring(3));
      } else {
        name = Introspector.decapitalize(getterName.substring(2));
      }
      getterMap.put(name, getter);
    }
    return getterMap;
  }

  /**
   * Find all method info getters on the specified class type info.
   *
   * @param classTypeInfo the class type info
   * @return an iterable of the method info getters
   * @throws NullPointerException if the specified class type info is null
   */
  public Iterable<MethodInfo> getGetters(ClassTypeInfo classTypeInfo) throws NullPointerException {
    if (classTypeInfo == null) {
      throw new NullPointerException();
    }
    final MethodContainer getters = new MethodContainer();
    HierarchyVisitor visitor = new AbstractScopedHierarchyVisitor(classTypeInfo) {
      public void leave(ClassTypeInfo type) {
        if (!type.getName().equals(Object.class.getName())) {
          for (MethodInfo method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            if (
              ((
                methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) ||
                (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(2)))) &&
              method.getParameterTypes().size() == 0) {
              getters.add(method);
            }
          }
        }
      }
    };

    //
    classTypeInfo.accept(strategy, visitor);

    //
    return getters;
  }

  /**
   * Returns a map of all method info setters on the specified class type info.
   *
   * @param classTypeInfo the class type info
   * @return an iterable of the method info setters
   * @throws NullPointerException if the specified class type info is null
   */
  public Map<String, Set<MethodInfo>> getSetterMap(ClassTypeInfo classTypeInfo) throws NullPointerException {
    Map<String, Set<MethodInfo>> setterMap = new HashMap<String, Set<MethodInfo>>();
    for (MethodInfo setter : getSetters(classTypeInfo)) {
      String name = Introspector.decapitalize(setter.getName().substring(3));
      Set<MethodInfo> setters = setterMap.get(name);
      if (setters == null) {
        setters = new HashSet<MethodInfo>();
        setterMap.put(name, setters);
      }
      setters.add(setter);
    }
    return setterMap;
  }

  /**
   * Find all method info setters on the specified class type info.
   *
   * @param classTypeInfo the class type info
   * @return an iterable of the method info setters
   * @throws NullPointerException if the specified class type info is null
   */
  public Iterable<MethodInfo> getSetters(ClassTypeInfo classTypeInfo) {
    if (classTypeInfo == null) {
      throw new NullPointerException();
    }
    final MethodContainer setters = new MethodContainer();
    HierarchyVisitor visitor = new AbstractScopedHierarchyVisitor(classTypeInfo) {
      public void leave(ClassTypeInfo type) {
        if (!type.getName().equals(Object.class.getName())) {
          for (MethodInfo method : type.getDeclaredMethods()) {
            String methodName = method.getName();
            if (
              methodName.startsWith("set") &&
              methodName.length() > 3 &&
              Character.isUpperCase(methodName.charAt(3)) &&
              method.getParameterTypes().size() == 1) {
              setters.add(method);
            }
          }
        }
      }
    };

    //
    classTypeInfo.accept(strategy, visitor);

    //
    return setters;
  }

  /**
   * Returns all method on the specified type info.
   *
   * @param typeInfo the type info
   * @return all the methods
   * @throws NullPointerException if the specified type info is null
   */
  public Set<MethodInfo> getMethods(TypeInfo typeInfo) throws NullPointerException {
    if (typeInfo == null) {
      throw new NullPointerException();
    }
    MethodContainer container;
    if (removeOverrides) {
      container = new MethodContainer((ClassTypeInfo)typeInfo);
    } else {
      container = new MethodContainer();
    }
    findMethods(typeInfo, container);
    return container.toCollection();
  }

  private void findMethods(TypeInfo ti, final MethodContainer container) {
    if (ti instanceof ClassTypeInfo) {
//      findMethods((ClassTypeInfo)ti, container);

      strategy.visit(ti, new HierarchyVisitor() {
        public boolean enter(ClassTypeInfo type) {
          for (MethodInfo declaredMethod : type.getDeclaredMethods()) {
            container.add(declaredMethod);
          }
          return true;
        }

        public void leave(ClassTypeInfo type) {

        }
      });

    } else if (ti instanceof ParameterizedTypeInfo) {
      findMethods(((ParameterizedTypeInfo)ti).getRawType(), container);
    } else {
      throw new UnsupportedOperationException("Cannot get methods from type " + ti);
    }
  }

/*
  private void findMethods(ClassTypeInfo clazz, MethodContainer container) {
    TypeInfo superType = clazz.getSuperType();
    if (superType == null || (superType instanceof ClassTypeInfo && ((ClassTypeInfo)superType).getName().equals(Object.class.getName()))) {
      //
    } else {
      findMethods(superType, container);
    }

    //
    for (TypeInfo interfaceType : clazz.getInterfaces()) {
      findMethods(interfaceType, container);
    }

    //
    for (MethodInfo declaredMethod : clazz.getDeclaredMethods()) {
      container.add(declaredMethod);
    }
  }
*/
}
