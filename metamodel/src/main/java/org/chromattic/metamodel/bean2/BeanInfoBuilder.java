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
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyVisitor;
import org.reflext.api.visit.HierarchyVisitorStrategy;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfoBuilder {


  public Map<ClassTypeInfo, BeanInfo> build(Set<ClassTypeInfo> classTypes) {
    Context ctx = new Context(classTypes);
    ctx.build();
    return ctx.beans;
  }

  private static class Context {

    private class BeanHierarchyVisitorStrategy<V extends HierarchyVisitor<V>> extends HierarchyVisitorStrategy<V> {

      /** . */
      private final ClassTypeInfo current;

      private BeanHierarchyVisitorStrategy(ClassTypeInfo current) {
        this.current = current;
      }

      @Override
      protected boolean accept(ClassTypeInfo type) {
        return type == current || !classTypes.contains(type);
      }
    }

    /** The types to build. */
    private final Set<ClassTypeInfo> classTypes;

    /** The beans being built in a method call to the builder. */
    private final Map<ClassTypeInfo, BeanInfo> beans;

    private Context(Set<ClassTypeInfo> classTypes) {
      this.classTypes = classTypes;
      this.beans = new HashMap<ClassTypeInfo, BeanInfo>();
    }

    void build() {
      if (classTypes.isEmpty()) {
        return;
      }
      while (true) {
        Iterator<ClassTypeInfo> iterator = classTypes.iterator();
        if (iterator.hasNext()) {
          ClassTypeInfo cti = iterator.next();
          BeanInfo bean = resolve(cti);
        } else {
          break;
        }
      }
    }

    /**
     * Resolve the bean object from the specified class type. The returned bean
     * won't be qualified, i.e it won't its state correct. The goal is just to
     * ensure that we have a unique bean instance per class type.
     *
     * @param classType the bean class type
     * @return the corresponding bean instance
     */
    BeanInfo resolve(ClassTypeInfo classType) {
      BeanInfo bean = beans.get(classType);
      if (bean == null) {
        if (classTypes.remove(classType)) {
          bean = new BeanInfo(classType);
          beans.put(classType, bean);
          build(bean);
        }
      }
      return bean;
    }

    void build(BeanInfo bean) {
      for (ClassTypeInfo ancestorClassType = bean.classType.getSuperClass();ancestorClassType != null;ancestorClassType = ancestorClassType.getSuperClass()) {

        // Resolve the ancestor class type
        BeanInfo ancestorBean = resolve(ancestorClassType);

        // If the ancestor resolves as a bean then it becomes the parent bean and we are done
        if (ancestorBean != null) {
          bean.parent = ancestorBean;
          break;
        }
      }

      //
      buildProperties(bean);
    }

    private PropertyInfo resolveProperty(BeanInfo bean, String propertyName) {

      // We may have null in case we were dealing with java.lang.Object for instance
      if (bean == null) {
        return null;
      }

      //
      if (bean.properties == null) {
        // Defensive: it means we are looking for a bean in an incorrect state
        throw new AssertionError();
      }

      //
      PropertyInfo property = bean.properties.get(propertyName);

      // Try in the parent
      if (property == null) {
        property = resolveProperty(bean.parent, propertyName);
      }

      //
      return property;
    }

    /**
     * Build properties of a bean.
     *
     * @param bean the bean to build properties.
     */
    private void buildProperties(BeanInfo bean) {

      class ToBuild {
        final TypeInfo type;
        final MethodInfo getter;
        final MethodInfo setter;
        ToBuild(TypeInfo type, MethodInfo getter, MethodInfo setter) {
          this.type = type;
          this.getter = getter;
          this.setter = setter;
        }
      }

      BeanHierarchyVisitorStrategy strategy = new BeanHierarchyVisitorStrategy(bean.classType);
      MethodIntrospector introspector = new MethodIntrospector(strategy, true);
      Map<String, MethodInfo> getterMap = introspector.getGetterMap(bean.classType);
      Map<String, Set<MethodInfo>> setterMap = introspector.getSetterMap(bean.classType);

      // Gather all properties on the bean
      Map<String, ToBuild> toBuilds = new HashMap<String,ToBuild>();
      for (Map.Entry<String, MethodInfo> getterEntry : getterMap.entrySet()) {
        String name = getterEntry.getKey();
        MethodInfo getter = getterEntry.getValue();
        TypeInfo getterTypeInfo = getter.getReturnType();

        //
        ToBuild toBuild = null;
        Set<MethodInfo> setters = setterMap.get(name);
        if (setters != null) {
          for (MethodInfo setter : setters) {
            TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
            if (getterTypeInfo.equals(setterTypeInfo)) {
              toBuild = new ToBuild(getterTypeInfo, getter, setter);
              break;
            }
          }
        }

        //
        if (toBuild == null) {
          toBuild = new ToBuild(getterTypeInfo, getter, null);
        }

        //
        if (toBuild != null) {
          toBuilds.put(name, toBuild);
        }
      }

      //
      setterMap.keySet().removeAll(toBuilds.keySet());
      for (Map.Entry<String, Set<MethodInfo>> setterEntry : setterMap.entrySet()) {
        String name = setterEntry.getKey();
        for (MethodInfo setter : setterEntry.getValue()) {
          TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
          toBuilds.put(name, new ToBuild(setterTypeInfo, null, setter));
        }
      }

      // Now we have all the info to build each property correctly
      Map<String, PropertyInfo> properties = new HashMap<String, PropertyInfo>();
      for (Map.Entry<String, ToBuild> tobuild : toBuilds.entrySet()) {
        TypeInfo resolvedTI = bean.classType.resolve(tobuild.getValue().type);
        ClassTypeInfo resolvedClassTI = Utils.resolveToClassType(bean.classType, resolvedTI);
        PropertyInfo parentProperty = resolveProperty(bean.parent, tobuild.getKey());
        PropertyInfo property;
        BeanInfo related = resolve(resolvedClassTI);
        if (related != null) {
          property = new BeanPropertyInfo(
              bean,
              parentProperty,
              tobuild.getKey(),
              resolvedTI,
              tobuild.getValue().getter,
              tobuild.getValue().setter,
              related);

        } else {
          property = new SimplePropertyInfo(
              bean,
              parentProperty,
              tobuild.getKey(),
              resolvedTI,
              tobuild.getValue().getter,
              tobuild.getValue().setter);
        }
        properties.put(property.getName(), property);
      }

      // Update properties
      bean.properties = Collections.unmodifiableMap(properties);
    }
  }
}
