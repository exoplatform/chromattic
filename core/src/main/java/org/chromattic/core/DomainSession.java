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

import org.chromattic.api.ChromatticSession;
import org.chromattic.api.Status;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.event.EventListener;
import org.chromattic.api.ChromatticException;
import org.chromattic.api.query.ObjectQueryBuilder;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.core.query.ObjectQueryBuilderImpl;
import org.chromattic.common.JCR;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Iterator;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class DomainSession implements ChromatticSession {

  /** . */
  protected final EventBroadcaster broadcaster;

  /** . */
  final Domain domain;

  /** . */
  protected final SessionWrapper sessionWrapper;

  protected DomainSession(Domain domain, SessionWrapper sessionWrapper) {
    this.domain = domain;
    this.broadcaster = new EventBroadcaster();
    this.sessionWrapper = sessionWrapper;
  }

  protected abstract String _persist(EntityContext ctx, String relPath) throws RepositoryException;

  protected abstract String _persist(EntityContext parentCtx, String name, EntityContext childCtx) throws RepositoryException;

  protected abstract <O> O _create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, RepositoryException;

  protected abstract <O> O _findById(Class<O> clazz, String id) throws RepositoryException;

  protected abstract void _save() throws RepositoryException;

  protected abstract void _remove(EntityContext context) throws RepositoryException;

  protected abstract Object _getReferenced(EntityContext referentCtx, String name, LinkType linkType) throws RepositoryException;

  protected abstract boolean _setReferenced(EntityContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws RepositoryException;

  protected abstract <T> Iterator<T> _getReferents(EntityContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws RepositoryException;

  protected abstract void _removeChild(EntityContext ctx, String name) throws RepositoryException;

  protected abstract Object _getChild(EntityContext ctx, String name) throws RepositoryException;

  protected abstract <T> Iterator<T> _getChildren(EntityContext ctx, Class<T> filterClass) throws RepositoryException;

  protected abstract Object _getParent(EntityContext ctx) throws RepositoryException;

  protected abstract <O> O _findByPath(EntityContext o, Class<O> clazz, String relPath) throws RepositoryException;

  protected abstract void _orderBefore(EntityContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) throws RepositoryException;

  protected abstract Node _getRoot() throws RepositoryException;

  public final Domain getDomain() {
    return domain;
  }

  public final Session getJCRSession() {
    return sessionWrapper.getSession();
  }

  public final String getId(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = unwrap(o);
    return ctx.getId();
  }

  public final String getName(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = unwrap(o);
    return getName(ctx);
  }

  public final String getPath(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = unwrap(o);
    return ctx.getPath();
  }

  public final <O> O create(Class<O> clazz) throws NullPointerException, IllegalArgumentException {
    return create(clazz, null);
  }

  public final <O> O create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException {
    try {
      return _create(clazz, name);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <O> O insert(Object parent, Class<O> clazz, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext parentCtx = unwrap(parent);
    O child = create(clazz);
    EntityContext childtx = unwrap(child);
    persistWithRelativePath(parentCtx, relPath, childtx);
    return child;
  }

  public final <O> O insert(Class<O> clazz, String relPath) throws NullPointerException, IllegalArgumentException, UndeclaredRepositoryException {
    O child = create(clazz);
    persist(child, relPath);
    return child;
  }

  public final String persist(Object parent, Object child, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException {
    try {
      EntityContext parentCtx = unwrap(parent);
      EntityContext childCtx = unwrap(child);
      return _persist(parentCtx, relPath, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException();
    }
  }

  public final String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException {
    EntityContext parentCtx = unwrap(parent);
    EntityContext childCtx = unwrap(child);
    String name = childCtx.state.getName();
    if (name == null) {
      String msg = "Attempt to persist non named object " + childCtx;
      throw new IllegalArgumentException(msg);
    }
    try {
      return _persist(parentCtx, name, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException {
    try {
      EntityContext ctx = unwrap(o);
      String name = ctx.state.getName();
      if (name == null) {
        String msg = "Attempt to persist non named object " + ctx;
        throw new IllegalArgumentException(msg);
      }
      return _persist(ctx, name);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final String persist(Object o, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException {
    try {
      EntityContext ctx = unwrap(o);
      return _persist(ctx, relPath);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }


  public final <O> O findByNode(Class<O> clazz, Node node) throws UndeclaredRepositoryException {
    if (node == null) {
      throw new NullPointerException();
    }

    //
    try {
      if (domain.getTypeMapper(node.getPrimaryNodeType().getName()) != null) {
        return findById(clazz, node.getUUID());
      } else {
        return null;
      }
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <O> O findById(Class<O> clazz, String id) throws UndeclaredRepositoryException {
    try {
      return _findById(clazz, id);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <O> O findByPath(Object o, Class<O> clazz, String relPath) throws ChromatticException {
    if (o == null) {
      throw new NullPointerException();
    }
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (relPath == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = unwrap(o);
    try {
      return _findByPath(ctx, clazz, relPath);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <O> O findByPath(Class<O> clazz, String relPath) throws ChromatticException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    if (relPath == null) {
      throw new NullPointerException();
    }
    try {
      return _findByPath(null, clazz, relPath);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void save() throws UndeclaredRepositoryException {
    try {
      _save();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Status getStatus(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }
    EntityContext ctx = unwrap(o);
    return ctx.getStatus();
  }

  public final void remove(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }
    try {
      EntityContext context = unwrap(o);
      _remove(context);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ObjectQueryBuilder<?> createQueryBuilder() throws ChromatticException {
    return new ObjectQueryBuilderImpl(this);
  }

  public final Node getNode(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    EntityContext ctx = unwrap(o);
    return ctx.state.getNode();
  }

  public final String encodeName(String external) {
    if (external == null) {
      throw new NullPointerException("No null name accepted");
    }
    String internal;
    try {
      internal = domain.objectFormatter.encodeNodeName(null, external);
    }
    catch (Exception e) {
      if (e instanceof NullPointerException) {
        throw (NullPointerException)e;
      }
      if (e instanceof IllegalArgumentException) {
        throw (IllegalArgumentException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (internal == null) {
      throw new IllegalArgumentException("Name " + external + " was converted to null");
    }
    JCR.validateName(internal);
    return internal;
  }

  public final String decodeName(String internal) {
    String external;
    try {
      external = domain.objectFormatter.decodeNodeName(null, internal);
    }
    catch (Exception e) {
      if (e instanceof IllegalStateException) {
        throw (IllegalStateException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (external == null) {
      throw new IllegalStateException("Null name returned by decoder");
    }
    return external;
  }

  public final String getName(EntityContext ctx) throws UndeclaredRepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    String name = ctx.state.getName();
    if (name != null) {
      name = decodeName(name);
    }

    //
    return name;
  }

  public final void setName(EntityContext ctx, String name) throws UndeclaredRepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    name = encodeName(name);

    //
    ctx.state.setName(name);
  }

  public final String persist(EntityContext relatativeCtx, String name, EntityContext siblingCtx) throws UndeclaredRepositoryException {
    try {
      name = encodeName(name);

      // Just to symbolise we convert the name to a path
      String path = name;

      //
      return _persist(relatativeCtx, path, siblingCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void orderBefore(EntityContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) {
    try {
      _orderBefore(parentCtx, srcCtx, dstCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final String persistWithName(EntityContext parentCtx, String name, EntityContext childCtx) throws UndeclaredRepositoryException {
    try {
      name = encodeName(name);

      // Just to symbolise we convert the name to a path
      String path = name;

      //
      return _persist(parentCtx, path, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final String persistWithRelativePath(EntityContext parentCtx, String relPath, EntityContext childCtx) throws UndeclaredRepositoryException {
    try {
      return _persist(parentCtx, relPath, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void remove(EntityContext context) throws UndeclaredRepositoryException {
    try {
      _remove(context);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Object getReferenced(EntityContext referentCtx, String name, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _getReferenced(referentCtx, name, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final boolean setReferenced(EntityContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws UndeclaredRepositoryException  {
    try {
      return _setReferenced(referentCtx, name, referencedCtx, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void removeChild(EntityContext ctx, String name) throws UndeclaredRepositoryException {
    try {
      _removeChild(ctx, name);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Object getChild(EntityContext ctx, String name) throws UndeclaredRepositoryException {
    try {
      return _getChild(ctx, name);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <T> Iterator<T> getChildren(EntityContext ctx, Class<T> filterClass) throws UndeclaredRepositoryException {
    try {
      return _getChildren(ctx, filterClass);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Object getParent(EntityContext ctx) throws UndeclaredRepositoryException {
    try {
      return _getParent(ctx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <T> Iterator<T> getReferents(EntityContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _getReferents(referencedCtx, name, filterClass, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final EntityContext unwrap(Object o) {
    if (o == null) {
      throw new NullPointerException("Cannot unwrap null object");
    }
    return (EntityContext)domain.getInstrumentor().getInvoker(o);
  }

  public final Node getRoot() {
    try {
      return _getRoot();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public SessionWrapper getSessionWrapper() {
    return sessionWrapper;
  }

  public void addEventListener(EventListener listener) {
    broadcaster.addLifeCycleListener(listener);
  }
}