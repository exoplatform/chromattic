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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.chromattic.api.Status;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.jcr.type.PrimaryTypeInfo;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.metamodel.mapping.NodeAttributeType;
import org.chromattic.spi.instrument.ProxyType;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class EntityContext extends ObjectContext<EntityContext> {

  /** The related type. */
  final ObjectMapper<EntityContext> mapper;

  /** The object instance. */
  final Object object;

  /** The related state. */
  EntityContextState state;

  /** The attributes. */
  private Map<Object, Object> attributes;

  EntityContext(ObjectMapper<EntityContext> mapper, DomainSession session, Node node) throws RepositoryException {
    this(mapper, new PersistentEntityContextState(mapper, node, session));
  }

  EntityContext(ObjectMapper<EntityContext> mapper, DomainSession session) throws RepositoryException {
    this(mapper, new TransientEntityContextState(session));
  }

  private EntityContext(ObjectMapper<EntityContext> mapper, EntityContextState state) throws RepositoryException {

    // Create our proxy
    ProxyType pt = state.getSession().domain.getProxyType(mapper.getObjectClass());
    Object object = pt.createProxy(this);

    //
    this.state = null;
    this.mapper = mapper;
    this.object = object;
    this.state = state;
    this.attributes = null;
  }

  public Object getAttribute(Object key) {
    if (key == null) {
      throw new AssertionError("Should not provide a null key");
    }
    if (attributes == null) {
      return null;
    } else {
      return attributes.get(key);
    }
  }

  public Object setAttribute(Object key, Object value) {
    if (key == null) {
      throw new AssertionError("Should not provide a null key");
    }
    if (value == null) {
      if (attributes != null) {
        return attributes.remove(key);
      } else {
        return null;
      }
    } else {
      if (attributes == null) {
        attributes = new HashMap<Object, Object>();
      }
      return attributes.put(key, value);
    }
  }

  public Node getNode() {
    return state.getNode();
  }

  public DomainSession getSession() {
    return state.getSession();
  }

  public Status getStatus() {
    return state.getStatus();
  }

  @Override
  public ObjectMapper<EntityContext> getMapper() {
    return mapper;
  }

  public Object getObject() {
    return object;
  }

  @Override
  public EntityContext getEntity() {
    return this;
  }

  public PrimaryTypeInfo getTypeInfo() {
    EntityContextState state = getEntity().state;
    return state.getTypeInfo();
  }

  /**
   * Adapts the current object held by this context to the specified type.
   * If the current object is an instance of the specified class then this
   * object is returned otherwise an attempt to find an embedded object of
   * the specified type is performed.
   *
   * @param adaptedClass the class to adapt to
   * @param <T> the parameter type of the adapted class
   * @return the adapted object or null
   */
  public <T> T adapt(Class<T> adaptedClass) {
    // If it fits the current object we use it
    if (adaptedClass.isInstance(object)) {
      return adaptedClass.cast(object);
    } else {
      // Here we are trying to see if the parent has something embedded we could return
      // that is would be of the provided related class
      EmbeddedContext embeddedCtx = getEmbedded(adaptedClass);
      if (embeddedCtx != null) {
        return adaptedClass.cast(embeddedCtx.getObject());
      }
    }

    //
    return null;
  }

  public void addMixin(EmbeddedContext mixinCtx) {
    state.getSession().addMixin(this, mixinCtx);
  }

  public void removeMixin(Class<?> mixinType) {
    state.getSession().removeMixin(this, mixinType);
  }

  public EmbeddedContext getEmbedded(Class<?> embeddedType) {
    return state.getSession().getEmbedded(this, embeddedType);
  }

  public String getAttribute(NodeAttributeType type) {
    DomainSession session = state.getSession();
    switch (type) {
      case NAME:
        return session.getLocalName(this);
      case ID:
        return state.getId();
      case PATH:
        return state.getPath();
      case WORKSPACE_NAME:
        return session.sessionWrapper.getSession().getWorkspace().getName();
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

  public String getLocalName() {
    return getAttribute(NodeAttributeType.NAME);
  }

  public String getId() {
    return getAttribute(NodeAttributeType.ID);
  }

  public String getPath() {
    return getAttribute(NodeAttributeType.PATH);
  }

  public void setLocalName(String name) {
    state.getSession().setLocalName(this, name);
  }

  public EntityContext getReferenced(String name, LinkType linkType) {
    return state.getSession().getReferenced(this, name, linkType);
  }

  public void setReferenced(String name, EntityContext referencedCtx, LinkType linkType) {
    DomainSession session = state.getSession();
    session.setReferenced(this, name, referencedCtx, linkType);
  }

  public boolean addReference(String name, EntityContext referentCtx, LinkType linkType) {
    DomainSession session = state.getSession();
    return session.setReferenced(referentCtx, name, this, linkType);
  }

//  public void removeChild(String name) {
//    if (getStatus() != Status.PERSISTENT) {
//      throw new IllegalStateException("Can only insert/remove a child of a persistent object");
//    }
//
//    //
//    state.getSession().removeChild(this, name);
//  }
//
//  public void orderBefore(EntityContext srcCtx, EntityContext dstCtx) {
//    state.getSession().orderBefore(this, srcCtx, dstCtx);
//  }
//
//  public void addChild(EntityContext childCtx) {
//    if (childCtx.getStatus() == Status.TRANSIENT || childCtx.getStatus() == Status.PERSISTENT) {
//      String name = childCtx.getName();
//      addChild(name, childCtx);
//    } else {
//      throw new IllegalArgumentException("The child does not have the good state to be added " + childCtx);
//    }
//  }
//
//  public void addChild(String name, EntityContext childCtx) {
//    if (childCtx.getStatus() == Status.PERSISTENT) {
//      state.getSession().move(childCtx, this, name);
//    } else {
//      state.getSession().persist(this, childCtx, name);
//    }
//  }
//
//  public EntityContext getChild(String name) {
//    return state.getSession().getChild(this, name);
//  }
//
//  public <T> Iterator<T> getChildren(Class<T> filterClass) {
//    return state.getSession().getChildren(this, filterClass);
//  }

  public EntityContext getParent() {
    return state.getSession().getParent(this);
  }

  @Override
  public String toString() {
    return "EntityContext[state=" + state + ",mapper=" + mapper + "]";
  }
}
