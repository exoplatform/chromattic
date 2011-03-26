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

package org.chromattic.core.mapper;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import org.chromattic.core.ObjectContext;
import org.chromattic.core.MethodInvoker;
import org.chromattic.core.bean.PropertyInfo;
import org.chromattic.core.jcr.NodeDef;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.ProxyFactory;
import org.chromattic.api.NameConflictResolution;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMapper implements MethodInvoker {

  /** . */
  private final Class<?> objectClass;

  /** . */
  private final NodeDef nodeDef;

  /** . */
  final Set<MethodMapper> methodMappers;

  /** . */
  final Set<PropertyMapper> propertyMappers;

  /** . */
  private final ProxyFactory factory;

  /** . */
  private final Map<Method, MethodInvoker> dispatchers;

  /** . */
  private final NameConflictResolution onDuplicate;

  public TypeMapper(
    Class<?> objectClass,
    Set<PropertyMapper> propertyMappers,
    Set<MethodMapper> methodMappers,
    NodeDef nodeDef,
    NameConflictResolution onDuplicate,
    Instrumentor instrumentor) {

    // Build the dispatcher map
    Map<Method, MethodInvoker> dispatchers = new HashMap<Method, MethodInvoker>();
    for (PropertyMapper propertyMapper : propertyMappers) {
      PropertyInfo info = propertyMapper.getInfo();
      MethodInfo getter = info.getGetter();
      if (getter != null) {
        dispatchers.put((Method)getter.getMethod(), propertyMapper);
      }
      MethodInfo setter = info.getSetter();
      if (setter != null) {
        dispatchers.put((Method)setter.getMethod(), propertyMapper);
      }
    }
    for (MethodMapper methodMapper : methodMappers) {
      dispatchers.put(methodMapper.getMethod(), methodMapper);
    }

    //
    this.dispatchers = dispatchers;
    this.objectClass = objectClass;
    this.methodMappers = methodMappers;
    this.nodeDef = nodeDef;
    this.onDuplicate = onDuplicate;
    this.propertyMappers = propertyMappers;
    this.factory = instrumentor.getProxyClass(objectClass);
  }

  public Object invoke(ObjectContext ctx, Method method, Object[] args) throws Throwable {
    MethodInvoker invoker = dispatchers.get(method);
    if (invoker != null) {
      return invoker.invoke(ctx, method, args);
    } else {
      StringBuilder msg = new StringBuilder("Cannot invoke method ").append(method.getName()).append("(");
      Class[] parameterTypes = method.getParameterTypes();
      for (int i = 0;i < parameterTypes.length;i++) {
        if (i > 0) {
          msg.append(',');
        }
        msg.append(parameterTypes[i].getName());
      }
      msg.append(") with arguments (");
      for (int i = 0;i < args.length;i++) {
        if (i > 0) {
          msg.append(',');
        }
        msg.append(String.valueOf(args[i]));
      }
      msg.append(")");
      throw new AssertionError(msg);
    }
  }

  public Object createObject(ObjectContext context) {
    return factory.createProxy(context);
  }

  public Set<MethodMapper> getMethodMappers() {
    return methodMappers;
  }

  public Set<PropertyMapper> getPropertyMappers() {
    return propertyMappers;
  }

  public Class<?> getObjectClass() {
    return objectClass;
  }

  public NodeDef getNodeDef() {
    return nodeDef;
  }

  public NameConflictResolution getOnDuplicate() {
    return onDuplicate;
  }

  @Override
  public String toString() {
    return "TypeMapper[class=" + objectClass + ",nodeType=" + nodeDef + "]";
  }
}