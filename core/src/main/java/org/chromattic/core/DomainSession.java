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

import org.chromattic.api.ChromatticException;
import org.chromattic.api.UndeclaredRepositoryException;
import org.chromattic.api.event.EventListener;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.common.logging.Logger;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.spi.instrument.MethodHandler;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class DomainSession {

  /** . */
  protected static final Logger log = Logger.getLogger(DomainSession.class);

  /** . */
  protected final EventBroadcaster broadcaster;

  /** . */
  final Domain domain;

  /** . */
  protected final SessionWrapper sessionWrapper;

  public DomainSession(Domain domain, SessionWrapper sessionWrapper) {
    this.domain = domain;
    this.broadcaster = new EventBroadcaster();
    this.sessionWrapper = sessionWrapper;
  }

  protected abstract void _setLocalName(EntityContext ctx, String localName) throws RepositoryException;

  protected abstract <T1 extends Throwable> void _persist(
      ThrowableFactory<T1> nullLocaleNameTF,
      EntityContext ctx,
      String prefix,
      String localName) throws T1, RepositoryException;

  protected abstract <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> void _persist(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      ThrowableFactory<T3> nullLocaleNameTF,
      ObjectContext srcCtx,
      String prefix,
      String localName,
      EntityContext dstCtx) throws
      T1, T2, T3, NullPointerException, RepositoryException;

  protected abstract <T1 extends Throwable, T2 extends Throwable> void _move(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      EntityContext srcCtx,
      ObjectContext dstCtx,
      String dstPrefix,
      String dstLocalName) throws
    T1, T2, NullPointerException, RepositoryException;

  protected abstract EntityContext _copy(EntityContext srcCtx, String prefix, String localName) throws RepositoryException;

  protected abstract EntityContext _copy(EntityContext parentCtx, EntityContext srcCtx, String prefix, String localName) throws RepositoryException;

  protected abstract ObjectContext _create(Class<?> clazz, String localName) throws NullPointerException, IllegalArgumentException, RepositoryException;

  protected abstract <E> E _findById(Class<E> clazz, String id) throws RepositoryException;

  protected abstract <E> E _findByNode(Class<E> clazz, Node node) throws RepositoryException;

  protected abstract void _save() throws RepositoryException;

  protected abstract void _remove(EntityContext context) throws RepositoryException;

  protected abstract EntityContext _getReferenced(ObjectContext referentCtx, String name, LinkType linkType) throws RepositoryException;

  protected abstract boolean _setReferenced(ObjectContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws RepositoryException;

  protected abstract <T> Iterator<T> _getReferents(EntityContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws RepositoryException;

  protected abstract void _removeChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException;

  protected abstract EntityContext _getChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException;

  protected abstract boolean _hasChild(ObjectContext ctx, String prefix, String localName) throws RepositoryException;

  protected abstract <T> Iterator<T> _getChildren(ObjectContext ctx, Class<T> filterClass) throws RepositoryException;

  protected abstract boolean _hasChildren(ObjectContext ctx) throws RepositoryException;
  
  protected abstract EntityContext _getParent(EntityContext ctx) throws RepositoryException;

  protected abstract <E> E _findByPath(EntityContext ctx, Class<E> clazz, String relPath) throws RepositoryException;

  protected abstract <E> E _findByPath(Class<E> clazz, String path) throws RepositoryException;

  protected abstract void _orderBefore(ObjectContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) throws RepositoryException;

  protected abstract Node _getRoot() throws RepositoryException;

  protected abstract void _addMixin(EntityContext entityCtx, EmbeddedContext mixinCtx) throws RepositoryException;

  protected abstract void _removeMixin(EntityContext entityCtx, Class<?> mixinType) throws RepositoryException;

  protected abstract EmbeddedContext _getEmbedded(EntityContext ctx, Class<?> embeddedType) throws RepositoryException;

  protected abstract EntityContext _getEntity(Node node) throws RepositoryException;

  protected abstract String _getLocalName(EntityContext ctx) throws RepositoryException;

  protected abstract void _close() throws RepositoryException;

  public Domain getDomain() {
    return domain;
  }

  public Session getJCRSession() {
    return sessionWrapper.getSession();
  }

  public <O> QueryBuilder<O> createQueryBuilder(Class<O> fromClass) throws ChromatticException {
    return domain.queryManager.createQueryBuilder(this, fromClass);
  }

  public void addEventListener(EventListener listener) throws NullPointerException {
    broadcaster.addLifeCycleListener(listener);
  }

  public void close() {
    try {
      _close();
    }
    catch (RepositoryException e) {
      log.error("A repository exception happened when the session was closed", e);
    }
  }

  public boolean isClosed()
  {
    return sessionWrapper.isClosed();
  }

  public EmbeddedContext getEmbedded(EntityContext ctx, Class<?> embeddedClass) throws UndeclaredRepositoryException {
    try {
      return _getEmbedded(ctx, embeddedClass);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public void save() throws UndeclaredRepositoryException {
    try {
      _save();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <E> E findById(Class<E> clazz, String id) throws UndeclaredRepositoryException {
    try {
      return _findById(clazz, id);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <E> E findByPath(EntityContext ctx, Class<E> clazz, String relPath) throws UndeclaredRepositoryException {
    try {
      return _findByPath(ctx, clazz, relPath);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <E> E findByPath(Class<E> clazz, String path) throws UndeclaredRepositoryException {
    try {
      return _findByPath(clazz, path);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <E> E findByNode(Class<E> clazz, Node node) throws UndeclaredRepositoryException {
    try {
      return _findByNode(clazz, node);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <T1 extends Throwable> void persist(ThrowableFactory<T1> nullLocaleNameTF, EntityContext ctx, String prefix, String localName) throws T1, UndeclaredRepositoryException {
    try {
      _persist(nullLocaleNameTF, ctx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public EntityContext copy(EntityContext srcCtx, String prefix, String localName) throws UndeclaredRepositoryException {
    try {
      return _copy(srcCtx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public EntityContext copy(EntityContext parentCtx, EntityContext srcCtx, String prefix, String localName) throws UndeclaredRepositoryException {
    try {
      return _copy(parentCtx, srcCtx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ObjectContext create(Class<?> clazz, String localName) throws NullPointerException, IllegalArgumentException, UndeclaredRepositoryException {
    try {
      return _create(clazz, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public String getLocalName(EntityContext ctx) throws UndeclaredRepositoryException {
    try {
      return _getLocalName(ctx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public void addMixin(EntityContext ctx, EmbeddedContext mixinCtx) throws ChromatticException {
    try {
      _addMixin(ctx, mixinCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public void removeMixin(EntityContext ctx, Class<?> mixinType) throws ChromatticException {
    try {
      _removeMixin(ctx, mixinType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void setLocalName(EntityContext ctx, String localName) throws UndeclaredRepositoryException {
    try {
      _setLocalName(ctx, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void orderBefore(ObjectContext parentCtx, EntityContext srcCtx, EntityContext dstCtx) {
    try {
      _orderBefore(parentCtx, srcCtx, dstCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public <T1 extends Throwable, T2 extends Throwable> void move(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      EntityContext srcCtx, ObjectContext dstCtx, String dstPrefix, String dstLocalName) throws T1, T2, UndeclaredRepositoryException {
    try {
      _move(srcStateTF, dstStateTF, srcCtx, dstCtx, dstPrefix, dstLocalName);
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

  public final EntityContext getReferenced(ObjectContext referentCtx, String name, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _getReferenced(referentCtx, name, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final boolean setReferenced(ObjectContext referentCtx, String name, EntityContext referencedCtx, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _setReferenced(referentCtx, name, referencedCtx, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void removeChild(ObjectContext ctx, String prefix, String localName) throws UndeclaredRepositoryException {
    try {
      _removeChild(ctx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final EntityContext getEntity(Node node) throws UndeclaredRepositoryException {
    try {
      return _getEntity(node);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final EntityContext getChild(ObjectContext ctx, String prefix, String localName) throws UndeclaredRepositoryException {
    try {
      return _getChild(ctx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final boolean hasChild(ObjectContext ctx, String prefix, String localName) throws UndeclaredRepositoryException {
    try {
      return _hasChild(ctx, prefix, localName);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <T> Iterator<T> getChildren(ObjectContext ctx, Class<T> filterClass) throws UndeclaredRepositoryException {
    try {
      return _getChildren(ctx, filterClass);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final boolean hasChildren(ObjectContext ctx) throws UndeclaredRepositoryException {
    try {
      return _hasChildren(ctx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final EntityContext getParent(EntityContext ctx) throws UndeclaredRepositoryException {
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

  /**
   * Unwraps the object to an object context
   *
   * @param o the object to unwrap
   * @return the related object context
   * @throws NullPointerException if the object is null
   * @throws IllegalArgumentException if the object is not a proxy
   */
  public final ObjectContext unwrapObject(Object o) throws NullPointerException, IllegalArgumentException {
    return unwrap(o, ObjectContext.class);
  }

  /**
   * Unwraps the object to an entity context
   *
   * @param o the object to unwrap
   * @return the related entity context
   * @throws NullPointerException if the object is null
   * @throws IllegalArgumentException if the object is not a proxy
   */
  public final EntityContext unwrapEntity(Object o) throws NullPointerException, IllegalArgumentException {
    return unwrap(o, EntityContext.class);
  }

  /**
   * Unwraps the object to an embedded context
   *
   * @param o the object to unwrap
   * @return the related embedded context
   * @throws NullPointerException if the object is null
   * @throws IllegalArgumentException if the object is not a proxy
   */
  public final EmbeddedContext unwrapMixin(Object o) {
    return unwrap(o, EmbeddedContext.class);
  }

  private <T> T unwrap(Object o, Class<T> expectedClass) {
    if (o == null) {
      throw new NullPointerException("Cannot unwrap null object");
    }
    if (expectedClass == null) {
      throw new NullPointerException();
    }
    MethodHandler handler = domain.getHandler(o);
    if (handler == null) {
      throw new IllegalArgumentException("The object with class " + o.getClass().getName() + " is not instrumented");
    }
    if (expectedClass.isInstance(handler)) {
      return expectedClass.cast(handler);
    } else {
      throw new AssertionError("The proxy " + o + " handler is not of the expected type " + expectedClass.getName());
    }
  }

  public final <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> void persist(
      ThrowableFactory<T1> srcStateTF,
      ThrowableFactory<T2> dstStateTF,
      ThrowableFactory<T3> nullLocaleNameTF,
      ObjectContext srcCtx,
      EntityContext dstCtx,
      String prefix,
      String localName) throws T1, T2, T3, UndeclaredRepositoryException {
    try {
      _persist(srcStateTF, dstStateTF, nullLocaleNameTF, srcCtx, prefix, localName, dstCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
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
}
