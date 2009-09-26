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
import org.chromattic.core.query.QueryManager;
import org.chromattic.core.query.ObjectQueryBuilderImpl;
import org.chromattic.common.JCR;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import javax.jcr.Property;
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
  private final QueryManager queryManager;

  protected DomainSession(Domain domain) {
    this.domain = domain;
    this.broadcaster = new EventBroadcaster();
    this.queryManager = new QueryManager(this);
  }

  protected abstract String _persist(ObjectContext ctx, String relPath) throws RepositoryException;

  protected abstract String _persist(ObjectContext parentCtx, String name, ObjectContext childCtx) throws RepositoryException;

  protected abstract <O> O _create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, RepositoryException;

  protected abstract <O> O _findById(Class<O> clazz, String id) throws RepositoryException;

  protected abstract void _save() throws RepositoryException;

  protected abstract void _remove(Object o) throws RepositoryException;

  protected abstract void _remove(ObjectContext context) throws RepositoryException;

  protected abstract Object _getReferenced(ObjectContext referentCtx, String name, LinkType linkType) throws RepositoryException;

  protected abstract boolean _setReferenced(ObjectContext referentCtx, String name, ObjectContext referencedCtx, LinkType linkType) throws RepositoryException;

  protected abstract <T> Iterator<T> _getReferents(ObjectContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws RepositoryException;

  protected abstract void _removeChild(ObjectContext ctx, String name) throws RepositoryException;

  protected abstract Object _getChild(ObjectContext ctx, String name) throws RepositoryException;

  protected abstract <T> Iterator<T> _getChildren(ObjectContext ctx, Class<T> filterClass) throws RepositoryException;

  protected abstract Object _getParent(ObjectContext ctx) throws RepositoryException;

  protected abstract <O> O _findByPath(Object o, Class<O> clazz, String relPath) throws RepositoryException;

  protected abstract void _orderBefore(ObjectContext parentCtx, ObjectContext srcCtx, ObjectContext dstCtx) throws RepositoryException;

  protected abstract Node _getRoot() throws RepositoryException;

  public final String getId(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    ObjectContext ctx = unwrap(o);
    return ctx.getId();
  }

  public final String getName(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    ObjectContext ctx = unwrap(o);
    return getName(ctx);
  }

  public final String getPath(Object o) throws UndeclaredRepositoryException {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    ObjectContext ctx = unwrap(o);
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
    ObjectContext parentCtx = unwrap(parent);
    O child = create(clazz);
    ObjectContext childtx = unwrap(child);
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
      ObjectContext parentCtx = unwrap(parent);
      ObjectContext childCtx = unwrap(child);
      return _persist(parentCtx, relPath, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException();
    }
  }

  public final String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException {
    ObjectContext parentCtx = unwrap(parent);
    ObjectContext childCtx = unwrap(child);
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
      ObjectContext ctx = unwrap(o);
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
      ObjectContext ctx = unwrap(o);
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
    try {
      return _findByPath(o, clazz, relPath);
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
    ObjectContext ctx = unwrap(o);
    return ctx.getStatus();
  }

  public final void remove(Object o) throws UndeclaredRepositoryException {
    try {
      _remove(o);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public ObjectQueryBuilder<?> createQueryBuilder() throws ChromatticException {
    return new ObjectQueryBuilderImpl(domain, queryManager);
  }

  public final Node getNode(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }

    //
    ObjectContext ctx = unwrap(o);
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

  public final String getName(ObjectContext ctx) throws UndeclaredRepositoryException {
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

  public final void setName(ObjectContext ctx, String name) throws UndeclaredRepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    name = encodeName(name);

    //
    ctx.state.setName(name);
  }

  public final String persist(ObjectContext relatativeCtx, String name, ObjectContext siblingCtx) throws UndeclaredRepositoryException {
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

  public final void orderBefore(ObjectContext parentCtx, ObjectContext srcCtx, ObjectContext dstCtx) {
    try {
      _orderBefore(parentCtx, srcCtx, dstCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final String persistWithName(ObjectContext parentCtx, String name, ObjectContext childCtx) throws UndeclaredRepositoryException {
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

  public final String persistWithRelativePath(ObjectContext parentCtx, String relPath, ObjectContext childCtx) throws UndeclaredRepositoryException {
    try {
      return _persist(parentCtx, relPath, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void remove(ObjectContext context) throws UndeclaredRepositoryException {
    try {
      _remove(context);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Object getReferenced(ObjectContext referentCtx, String name, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _getReferenced(referentCtx, name, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final boolean setReferenced(ObjectContext referentCtx, String name, ObjectContext referencedCtx, LinkType linkType) throws UndeclaredRepositoryException  {
    try {
      return _setReferenced(referentCtx, name, referencedCtx, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final void removeChild(ObjectContext ctx, String name) throws UndeclaredRepositoryException {
    try {
      _removeChild(ctx, name);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final Object getChild(ObjectContext ctx, String name) throws UndeclaredRepositoryException {
    try {
      return _getChild(ctx, name);
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

  public final Object getParent(ObjectContext ctx) throws UndeclaredRepositoryException {
    try {
      return _getParent(ctx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final <T> Iterator<T> getReferents(ObjectContext referencedCtx, String name, Class<T> filterClass, LinkType linkType) throws UndeclaredRepositoryException {
    try {
      return _getReferents(referencedCtx, name, filterClass, linkType);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public final ObjectContext unwrap(Object o) {
    if (o == null) {
      throw new NullPointerException("Cannot unwrap null object");
    }
    return (ObjectContext)domain.getInstrumentor().getInvoker(o);
  }

  public final Node getRoot() {
    try {
      return _getRoot();
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException(e);
    }
  }

  public void addEventListener(EventListener listener) {
    broadcaster.addLifeCycleListener(listener);
  }
}