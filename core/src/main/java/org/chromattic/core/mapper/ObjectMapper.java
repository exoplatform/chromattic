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
import org.chromattic.core.MethodInvoker;
import org.chromattic.core.ObjectContext;
import org.chromattic.metamodel.bean.PropertyQualifier;
import org.chromattic.metamodel.mapping.NodeTypeKind;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.ProxyFactory;
import org.chromattic.api.NameConflictResolution;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectMapper<C extends ObjectContext> implements MethodInvoker<C> {

  /** . */
  protected final Class<?> objectClass;

  /** . */
  private final String nodeTypeName;

  /** . */
  final Set<MethodMapper<C>> methodMappers;

  /** . */
  final Set<PropertyMapper<?, C>> propertyMappers;

  /** . */
  private final ProxyFactory factory;

  /** . */
  private final Map<Method, MethodInvoker<C>> dispatchers;

  /** The optional formatter for this object. */
  private final ObjectFormatter formatter;

  /** . */
  private final NameConflictResolution onDuplicate;

  /** . */
  private final NodeTypeKind kind;

  public ObjectMapper(
    Class<?> objectClass,
    Set<PropertyMapper<?, C>> propertyMappers,
    Set<MethodMapper<C>> methodMappers,
    NameConflictResolution onDuplicate,
    ObjectFormatter formatter,
    Instrumentor instrumentor,
    String typeName,
    NodeTypeKind kind) {

    // Build the dispatcher map
    Map<Method, MethodInvoker<C>> dispatchers = new HashMap<Method, MethodInvoker<C>>();
    for (PropertyMapper<?, C> propertyMapper : propertyMappers) {
      PropertyQualifier<?> info = propertyMapper.getInfo();
      MethodInfo getter = info.getProperty().getGetter();
      if (getter != null) {
        dispatchers.put((Method)getter.getMethod(), propertyMapper);
      }
      MethodInfo setter = info.getProperty().getSetter();
      if (setter != null) {
        dispatchers.put((Method)setter.getMethod(), propertyMapper);
      }
    }
    for (MethodMapper<C> methodMapper : methodMappers) {
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
    this.kind = kind;
  }

  public Object invoke(C ctx, Method method, Object[] args) throws Throwable {
    MethodInvoker<C> invoker = dispatchers.get(method);
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

  public NodeTypeKind getKind() {
    return kind;
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

  public Set<MethodMapper<C>> getMethodMappers() {
    return methodMappers;
  }

  public Set<PropertyMapper<?, C>> getPropertyMappers() {
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
    return "EntityMapper[class=" + objectClass + ",typeName=" + nodeTypeName + "]";
  }
}