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
import org.chromattic.api.LifeCycleListener;
import org.chromattic.api.ChromatticException;
import org.chromattic.core.jcr.LinkType;
import org.chromattic.common.JCR;

import javax.jcr.RepositoryException;
import javax.jcr.Node;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class DomainSession implements ChromatticSession {

  /** . */
  private List<LifeCycleBroadcaster<?>> listeners;

  /** . */
  private final Domain domain;

  protected DomainSession(Domain domain) {
    this.domain = domain;
    this.listeners = null;
  }

  protected abstract String _persist(ObjectContext ctx, String relPath) throws RepositoryException;

  protected abstract String _persist(ObjectContext parentCtx, String relPath, ObjectContext childCtx) throws RepositoryException;

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

  public final String encodeName(String external) {
    String internal = domain.objectFormatter.encodeNodeName(null, external);
    JCR.validateName(internal);
    return internal;
  }

  public final String decodeName(String internal) {
    return domain.objectFormatter.decodeNodeName(null, internal);
  }

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

  public <O> O insert(Object parent, Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException {
    ObjectContext parentCtx = unwrap(parent);
    O child = create(clazz);
    ObjectContext childtx = unwrap(child);
    insertWithRelativePath(parentCtx, name, childtx);
    return child;
  }

  public final <O> O insert(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, UndeclaredRepositoryException {
    O child = create(clazz);
    persist(child, name);
    return child;
  }

  public String persist(Object parent, Object child, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException {
    try {
      ObjectContext parentCtx = unwrap(parent);
      ObjectContext childCtx = unwrap(child);
      return _persist(parentCtx, relPath, childCtx);
    }
    catch (RepositoryException e) {
      throw new UndeclaredRepositoryException();
    }
  }

  public String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException {
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

  public String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException {
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

  public String persist(Object o, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException {
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
      return findById(clazz, node.getUUID());
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

  public <O> O findByPath(Object o, Class<O> clazz, String relPath) throws ChromatticException {
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

  public <O> O findByPath(Class<O> clazz, String relPath) throws ChromatticException {
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

  public final String getName(ObjectContext ctx) throws UndeclaredRepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }

    //
    String name = ctx.state.getName();
    name = decodeName(name);

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

  public final String insertWithName(ObjectContext parentCtx, String name, ObjectContext childCtx) throws UndeclaredRepositoryException {
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

  public final String insertWithRelativePath(ObjectContext parentCtx, String relPath, ObjectContext childCtx) throws UndeclaredRepositoryException {
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

  public ObjectContext unwrap(Object o) {
    if (o == null) {
      throw new NullPointerException("Cannot unwrap null object");
    }
    return (ObjectContext)domain.getInstrumentor().getInvoker(o);
  }

  public final <O> void addLifeCycleListener(LifeCycleListener<O> listener) {
    if (listeners == null) {
      listeners = new ArrayList<LifeCycleBroadcaster<?>>();
    }
    listeners.add(new LifeCycleBroadcaster<O>(listener));
  }

  protected final void fireEvent(LifeCycleType eventType, ObjectContext ctx) {
    if (listeners != null) {
      Object o = ctx.getObject();
      for (LifeCycleBroadcaster listener : listeners) {
        listener.fireEvent(eventType, o);
      }
    }
  }
}