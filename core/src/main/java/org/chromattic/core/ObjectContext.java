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

package org.chromattic.core;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.chromattic.api.Status;
import org.chromattic.common.logging.Logger;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.mapper.MethodMapper;
import org.chromattic.core.mapper.PropertyMapper;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.core.bean.PropertyInfo;
import org.chromattic.core.bean.SimpleValueInfo;
import org.chromattic.core.jcr.LinkType;
import org.reflext.api.MethodInfo;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectContext implements MethodHandler {

  /** . */
  private final Logger log = Logger.getLogger(ObjectContext.class);

  /** . */
  final TypeMapper mapper;

  /** . */
  final Object object;

  /** . */
  final PropertyMap properties;

  /** . */
  ContextState state;

  public ObjectContext(TypeMapper mapper) {
    this(mapper, null);
  }

  public ObjectContext(TypeMapper mapper, TransientContextState state) {
    this.state = null;
    this.mapper = mapper;
    this.object = mapper.createObject(this);
    this.state = state;
    this.properties = new PropertyMap(this);
  }

  public DomainSession getSession() {
    return state.getSession();
  }

  public Status getStatus() {
    return state.getStatus();
  }

  public Object getObject() {
    return object;
  }

  public String getAttribute(NodeAttributeType type) {
    switch (type) {
      case NAME:
        return state.getName();
      case ID:
        return state.getId();
      case PATH:
        return state.getPath();
      case WORKSPACE_NAME:
        return state.getSession().getJCRSession().getWorkspace().getName();
      default:
        throw new AssertionError();
    }
  }

  public void remove() {
    state.getSession().remove(this);
  }

  public <T> Iterator<T> getReferents(final String name, Class<T> filterClass, LinkType linkType) {
    return state.getSession().getReferents(this, name, filterClass, linkType);
  }

  public String getName() {
    return getAttribute(NodeAttributeType.NAME);
  }

  public String getId() {
    return getAttribute(NodeAttributeType.ID);
  }

  public String getPath() {
    return getAttribute(NodeAttributeType.PATH);
  }

  public void setName(String name) {
    state.setName(name);
  }

  public Object getReferenced(String name, LinkType linkType) {
    return state.getSession().getReferenced(this, name, linkType);
  }

  public void setReferenced(String name, Object referenced, LinkType linkType) {
    DomainSession session = state.getSession();
    ObjectContext referencedCtx = null;
    if (referenced != null) {
      referencedCtx = session.unwrap(referenced);
    }

    //
    session.setReferenced(this, name, referencedCtx, linkType);
  }

  public boolean addReference(String name, Object referent, LinkType linkType) {
    DomainSession session = state.getSession();
    ObjectContext referentCtx = session.unwrap(referent);
    return session.setReferenced(referentCtx, name, this, linkType);
  }

  public Map<String, Object> getPropertyMap() {
    return properties;
  }

  public Object getPropertyValue(String propertyName, SimpleValueInfo type) {
    return state.getPropertyValue(propertyName, type);
  }

  public <T> T getPropertyValues(String propertyName, SimpleValueInfo simpleType, ListType<T> listType) {
    return state.getPropertyValues(propertyName, simpleType, listType);
  }

  public void setPropertyValue(String propertyName, SimpleValueInfo type, Object o) {
    state.setPropertyValue(propertyName, type, o);
  }

  public <T> void setPropertyValues(String propertyName, SimpleValueInfo type, ListType<T> listType, T objects) {
    state.setPropertyValues(propertyName, type, listType, objects);
  }

  public void removeChild(String name) {
    if (getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException("Can only insert/remove a child of a persistent object");
    }

    //
    state.getSession().removeChild(this, name);
  }

  public void addChild(ObjectContext childCtx) {
    String name = childCtx.state.getName();
    addChild(name, childCtx);
  }

  public void addChild(Object child) {
    DomainSession session = state.getSession();
    ObjectContext childCtx = session.unwrap(child);
    addChild(childCtx);
  }

  public void addChild(String name, ObjectContext childCtx) {
    state.getSession().insert(this, name, childCtx);
  }

  public void addChild(String name, Object child) {
    DomainSession session = state.getSession();
    ObjectContext childCtx = session.unwrap(child);
    addChild(name, childCtx);
  }

  public Object getChild(String name) {
    return state.getSession().getChild(this, name);
  }

  public <T> Iterator<T> getChildren(Class<T> filterClass) {
    return state.getSession().getChildren(this, filterClass);
  }

  public Object getParent() {
    return state.getSession().getParent(this);
  }

  @Override
  public String toString() {
    return "ObjectContext[status=" + state + ",mapper=" + mapper + "]";
  }

  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    for (PropertyMapper propertyMapper : mapper.getPropertyMappers()) {
      PropertyInfo info = propertyMapper.getInfo();
      MethodInfo getter = info.getGetter();
      if (getter != null && method.equals(getter.getMethod())) {
        return propertyMapper.get(this);
      } else {
        MethodInfo setter = info.getSetter();
        if (setter != null && method.equals(info.getSetter().getMethod())) {
          propertyMapper.set(this, args[0]);
          return null;
        }
      }
    }

    //
    for (MethodMapper methodMapper : mapper.getMethodMappers()) {
      if (method.equals(methodMapper.getMethod())) {
        return methodMapper.invoke(this, args);
      }
    }

    //
    throw new AssertionError("Could not handle invocation of method " + method.getName());
  }
}
