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

package org.chromattic.core.api;

import org.chromattic.api.ChromatticSession;
import org.chromattic.api.PathBuilder;
import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.event.EventListener;
import org.chromattic.api.ChromatticException;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.core.Domain;
import org.chromattic.core.DomainSession;
import org.chromattic.core.EmbeddedContext;
import org.chromattic.core.EntityContext;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public final class ChromatticSessionImpl implements ChromatticSession {

  /** . */
  final DomainSession domainSession;

  public ChromatticSessionImpl(DomainSession domainSession) {
    this.domainSession = domainSession;
  }

  public final Domain getDomain() {
    return domainSession.getDomain();
  }

  public Session getJCRSession() {
    return domainSession.getJCRSession();
  }

  public String getId(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException("Cannot obtain id from null parameter");
    }

    //
    EntityContext ctx = domainSession.unwrapEntity(o);
    return ctx.getId();
  }

  public String getName(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = domainSession.unwrapEntity(o);

    //
    return domainSession.getName(ctx);
  }

  public void setName(Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = domainSession.unwrapEntity(o);

    //
    domainSession.setName(ctx, name);
  }

  public String getPath(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = domainSession.unwrapEntity(o);
    return ctx.getPath();
  }

  public <O> O create(Class<O> clazz) throws NullPointerException, IllegalArgumentException {
    return create(clazz, null);
  }

  public <O> O create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException {
    return domainSession.create(clazz, name);
  }

  public <O> O insert(Object parent, Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    O child = create(clazz);
    persist(parent, child, name);
    return child;
  }

  public <O> O insert(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, UndeclaredRepositoryException {
    return insert(null, clazz, name);
  }

  public String persist(Object parent, Object child, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext childCtx = domainSession.unwrapEntity(child);
    if (parent != null) {
      EntityContext parentCtx = domainSession.unwrapEntity(parent);
      domainSession.persist(parentCtx, childCtx, name);
    } else {
      domainSession.persist(childCtx, name);
    }
    return childCtx.getId();
  }

  public String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext childCtx = domainSession.unwrapEntity(child);
    String name = childCtx.getName();
    if (name == null) {
      String msg = "Attempt to persist non named object " + childCtx;
      throw new IllegalArgumentException(msg);
    }
    if (parent != null) {
      EntityContext parentCtx = domainSession.unwrapEntity(parent);
      domainSession.persist(parentCtx, childCtx, name);
    } else {
      domainSession.persist(childCtx, name);
    }
    return childCtx.getId();
  }

  public String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException {
    return persist(null, o);
  }

  public String persist(Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    return persist(null, o, name);
  }

  public <O> O copy(O o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext srcCtx = domainSession.unwrapEntity(o);
    EntityContext dstCtx = domainSession.copy(srcCtx, name);
    @SuppressWarnings("unchecked")
    O dst = (O)dstCtx.getObject();
    return dst;
  }

  public <O> O copy(Object parent, O o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext srcCtx = domainSession.unwrapEntity(o);
    EntityContext parentCtx = domainSession.unwrapEntity(parent);
    EntityContext dstCtx = domainSession.copy(parentCtx, srcCtx, name);
    @SuppressWarnings("unchecked")
    O dst = (O)dstCtx.getObject();
    return dst;
  }

  public <O> O findByNode(Class<O> clazz, Node node) throws UndeclaredRepositoryException {
    return domainSession.findByNode(clazz, node);
  }

  public <O> O findById(Class<O> clazz, String id) throws UndeclaredRepositoryException {
    return domainSession.findById(clazz, id);
  }

  public <O> O findByPath(Object origin, Class<O> clazz, String relPath) throws ChromatticException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (relPath == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = null;
    if (origin != null) {
      ctx =  domainSession.unwrapEntity(origin);
    }
    return domainSession.findByPath(ctx, clazz, relPath);
  }

  public <O> O findByPath(Class<O> clazz, String relPath) throws ChromatticException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (relPath == null) {
      throw new NullPointerException();
    }
    return domainSession.findByPath(null, clazz, relPath);
  }

  public <O> O findByPath(Class<O> clazz, String path, boolean absolute) throws NullPointerException, ClassCastException, ChromatticException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (path == null) {
      throw new NullPointerException();
    }
    if (absolute) {
      return domainSession.findByPath(clazz, path);
    } else {
      return domainSession.findByPath(null, clazz, path);
    }
  }

  public void save() throws UndeclaredRepositoryException {
    domainSession.save();
  }

  public Status getStatus(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = domainSession.unwrapEntity(o);
    return ctx.getStatus();
  }

  public void remove(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }
    EntityContext context = domainSession.unwrapEntity(o);
    domainSession.remove(context);
  }

  public <E> E getEmbedded(Object o, Class<E> embeddedType) throws NullPointerException, IllegalArgumentException, ChromatticException {
    if (o == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = domainSession.unwrapEntity(o);
    EmbeddedContext embeddedCtx = ctx.getEmbedded(embeddedType);
    if (embeddedCtx != null) {
      return embeddedType.cast(embeddedCtx.getObject());
    } else {
      return null;
    }
  }

  public <E> void setEmbedded(Object o, Class<E> embeddedType, E embedded) {
    if (o == null) {
      throw new NullPointerException();
    }
    if (embeddedType == null) {
      throw new NullPointerException();
    }
    if (embedded == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = domainSession.unwrapEntity(o);
    EmbeddedContext embeddedCtx = domainSession.unwrapMixin(embedded);
    ctx.addMixin(embeddedCtx);
  }

  public void close() {
    domainSession.close();
  }

  public boolean isClosed() {
    return domainSession.isClosed();
  }

  public <O> QueryBuilder<O> createQueryBuilder(Class<O> fromClass) throws NullPointerException, IllegalArgumentException, ChromatticException {
    return domainSession.createQueryBuilder(fromClass);
  }

  public void addEventListener(EventListener listener) {
    domainSession.addEventListener(listener);
  }

  public <O> PathBuilder<O> createPathBuilder(O o) {
    EntityContext ctx = domainSession.unwrapEntity(o);
    return new AbstractPathBuilder.Root<O>(ctx);
  }

  //

  public Node getRoot() {
    return domainSession.getRoot();
  }

  public Node getNode(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = domainSession.unwrapEntity(o);
    return ctx.getNode();
  }
}