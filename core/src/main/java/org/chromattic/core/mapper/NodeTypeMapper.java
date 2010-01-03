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

import org.chromattic.api.format.ObjectFormatter;
import org.chromattic.core.EntityContext;
import org.chromattic.core.MethodInvoker;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.bean.PropertyInfo;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.ProxyFactory;
import org.chromattic.api.NameConflictResolution;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class NodeTypeMapper<C extends ObjectContext> implements MethodInvoker {

  /** . */
  protected final Class<?> objectClass;

  /** . */
  private final String nodeTypeName;

  /** . */
  final Set<MethodMapper> methodMappers;

  /** . */
  final Set<PropertyMapper> propertyMappers;

  /** . */
  private final ProxyFactory factory;

  /** . */
  private final Map<Method, MethodInvoker> dispatchers;

  /** The optional formatter for this object. */
  private final ObjectFormatter formatter;

  /** . */
  private final NameConflictResolution onDuplicate;

  public NodeTypeMapper(
    Class<?> objectClass,
    Set<PropertyMapper> propertyMappers,
    Set<MethodMapper> methodMappers,
    NameConflictResolution onDuplicate,
    ObjectFormatter formatter,
    Instrumentor instrumentor,
    String typeName) {

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
    this.formatter = formatter;
    this.onDuplicate = onDuplicate;
    this.propertyMappers = propertyMappers;
    this.factory = instrumentor.getProxyClass(objectClass);
    this.nodeTypeName = typeName;
  }

   public Object invoke(EntityContext ctx, Method method, Object[] args) throws Throwable {
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

  public String getNodeTypeName() {
    return nodeTypeName;
  }

  public ObjectFormatter getFormatter() {
    return formatter;
  }

  public Object createObject(C context) {
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

  public NameConflictResolution getOnDuplicate() {
    return onDuplicate;
  }

  @Override
  public String toString() {
    return "NodeTypeMapper[class=" + objectClass + ",typeName=" + nodeTypeName + "]";
  }
}