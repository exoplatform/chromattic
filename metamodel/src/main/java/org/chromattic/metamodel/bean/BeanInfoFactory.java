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

import org.reflext.api.*;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfoFactory {

  /** . */
  private PropertyQualifierResolver qualifier;

  public BeanInfoFactory() {
  }

  public BeanInfo build(ClassTypeInfo typeInfo) {

    this.qualifier = new PropertyQualifierResolver(typeInfo);

    Map<String, PropertyQualifier> properties = buildProperties(typeInfo);
    return new BeanInfo(typeInfo, properties);
  }

  private Map<String, PropertyQualifier> buildProperties(ClassTypeInfo type) {
    MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL, true);
    Map<String, MethodInfo> getterMap = introspector.getGetterMap(type);
    Map<String, Set<MethodInfo>> setterMap = introspector.getSetterMap(type);

    //
    Map<String, PropertyQualifier> properties = new HashMap<String, PropertyQualifier>();

    //
    for (Map.Entry<String, MethodInfo> getterEntry : getterMap.entrySet()) {
      String name = getterEntry.getKey();
      MethodInfo getter = getterEntry.getValue();
      TypeInfo getterTypeInfo = getter.getReturnType();

      //
      Set<MethodInfo> setters = setterMap.get(name);
      PropertyQualifier qproperty = null;

      //
      if (setters != null) {
        for (MethodInfo setter : setters) {
          TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
          if (getterTypeInfo.equals(setterTypeInfo)) {
            TypeInfo resolvedTI = type.resolve(getterTypeInfo);
            PropertyInfo property = new PropertyInfo(name, resolvedTI, getter, setter);
            qproperty = qualifier.createPropertyInfo(property);
            break;
          }
        }
      }

      //
      if (qproperty == null) {
        TypeInfo resolvedTI = type.resolve(getterTypeInfo);
        PropertyInfo property = new PropertyInfo(name, resolvedTI, getter, null);
        qproperty = qualifier.createPropertyInfo(property);
      }

      //
      if (qproperty != null) {
        properties.put(name, qproperty);
      }
    }

    //
    setterMap.keySet().removeAll(properties.keySet());
    for (Map.Entry<String, Set<MethodInfo>> setterEntry : setterMap.entrySet()) {
      String name = setterEntry.getKey();
      for (MethodInfo setter : setterEntry.getValue()) {
        TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
        TypeInfo resolvedTI = type.resolve(setterTypeInfo);
        PropertyInfo property = new PropertyInfo(name, resolvedTI, null, setter);
        PropertyQualifier qproperty = qualifier.createPropertyInfo(property);
        if (qproperty != null) {
          properties.put(name, qproperty);
          break;
        }
      }
    }

    //
    return properties;
  }
}
