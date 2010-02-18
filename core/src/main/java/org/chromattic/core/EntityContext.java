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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.chromattic.api.Status;
import org.chromattic.common.logging.Logger;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.metamodel.mapping.NodeAttributeType;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class EntityContext extends ObjectContext {

  /** The logger. */
  private static final Logger log = Logger.getLogger(EntityContext.class);

  /** The related type. */
  final ObjectMapper<EntityContext> mapper;

  /** The object instance. */
  final Object object;

  /** The property map. */
  final PropertyMap properties;

  /** The list of mixins. */
  final Map<ObjectMapper<EmbeddedContext>, EmbeddedContext> embeddeds;

  /** The related state. */
  EntityContextState state;

  EntityContext(ObjectMapper<EntityContext> mapper, EntityContextState state) throws RepositoryException {
    this.state = null;
    this.mapper = mapper;
    this.object = mapper.createObject(this);
    this.state = state;
    this.properties = new PropertyMap(this);
    this.embeddeds = new HashMap<ObjectMapper<EmbeddedContext>, EmbeddedContext>();
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

  public Object getObject() {
    return object;
  }

  @Override
  public EntityContext getEntity() {
    return this;
  }

  public NodeTypeInfo getTypeInfo() {
    EntityContextState state = getEntity().state;
    return state.getTypeInfo();
  }

  public void addMixin(EmbeddedContext mixinCtx) {
    state.getSession().addMixin(this, mixinCtx);
  }

  public EmbeddedContext getEmbedded(Class<?> embeddedClass) {
    return state.getSession().getEmbedded(this, embeddedClass);
  }

  public String getAttribute(NodeAttributeType type) {
    DomainSession session = state.getSession();
    switch (type) {
      case NAME:
        return session.getName(this);
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
    state.getSession().setName(this, name);
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

  public Map<String, Object> getPropertyMap() {
    return properties;
  }

  public void removeChild(String name) {
    if (getStatus() != Status.PERSISTENT) {
      throw new IllegalStateException("Can only insert/remove a child of a persistent object");
    }

    //
    state.getSession().removeChild(this, name);
  }

  public void orderBefore(EntityContext srcCtx, EntityContext dstCtx) {
    state.getSession().orderBefore(this, srcCtx, dstCtx);
  }

  public void addChild(EntityContext childCtx) {
    if (childCtx.getStatus() == Status.TRANSIENT || childCtx.getStatus() == Status.PERSISTENT) {
      String name = childCtx.getName();
      addChild(name, childCtx);
    } else {
      throw new IllegalArgumentException("The child does not have the good state to be added " + childCtx);
    }
  }

  public void addChild(String name, EntityContext childCtx) {
    if (childCtx.getStatus() == Status.PERSISTENT) {
      state.getSession().move(childCtx, this);
    } else {
      state.getSession().persist(this, childCtx, name);
    }
  }

  public EntityContext getChild(String name) {
    return state.getSession().getChild(this, name);
  }

  public <T> Iterator<T> getChildren(Class<T> filterClass) {
    return state.getSession().getChildren(this, filterClass);
  }

  public EntityContext getParent() {
    return state.getSession().getParent(this);
  }

  public Object invoke(Object o, Method method, Object[] args) throws Throwable {
    return mapper.invoke(this, method, args);
  }

  @Override
  public String toString() {
    return "EntityContext[state=" + state + ",mapper=" + mapper + "]";
  }
}
