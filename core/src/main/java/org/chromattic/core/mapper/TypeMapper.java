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

import org.chromattic.core.ObjectContext;
import org.chromattic.core.jcr.NodeDef;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.ProxyFactory;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeMapper {

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

  public TypeMapper(
    Class<?> objectClass,
    Set<PropertyMapper> propertyMappers,
    Set<MethodMapper> methodMappers,
    NodeDef nodeDef,
    Instrumentor instrumentor) {

    //
    this.objectClass = objectClass;
    this.methodMappers = methodMappers;
    this.nodeDef = nodeDef;
    this.propertyMappers = propertyMappers;
    this.factory = instrumentor.getProxyClass(objectClass);
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

  @Override
  public String toString() {
    return "TypeMapper[class=" + objectClass + ",nodeType=" + nodeDef + "]";
  }
}