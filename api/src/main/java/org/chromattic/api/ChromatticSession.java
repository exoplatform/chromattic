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

package org.chromattic.api;

import org.chromattic.api.query.QueryLanguage;
import org.chromattic.api.query.Query;

import javax.jcr.Session;
import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface ChromatticSession {

  /**
   * Creates a transient instance of the specified object.
   *
   * @param clazz the object class
   * @return the instance
   * @throws NullPointerException if the specified clazz is null
   * @throws IllegalArgumentException if the specified class does not have a declared mapping
   * @throws ChromatticException any chromattic exception
   */
  <O> O create(Class<O> clazz) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Creates a transient instance of the specified object. The name if it is not null will be used
   * later when the object is inserted in the JCR session. The clazz argument must be annotated class with
   * the <tt>NodeMapping</tt> annotation.
   *
   * @param clazz the object class
   * @param name the node name
   * @param <O> the object class parameter
   * @return the transient object
   * @throws NullPointerException if the clazz argument is null
   * @throws IllegalArgumentException if the name format is not valid
   * @throws ChromatticException any chromattic exception
   */
  <O> O create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Creates a persistent instance of the specified object.
   *
   * @param clazz the object class
   * @param relPath the path relative to the root node
   * @param <O> the object class parameter
   * @return the persistent object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  <O> O insert(Class<O> clazz, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Creates a persistent instance of the specified object.
   *
   * @param parent the parent object
   * @param clazz the object class
   * @param relPath the object path
   * @param <O> the object class parameter
   * @return the persistent object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  <O> O insert(Object parent, Class<O> clazz, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param o the object to persist
   * @param relPath the object relative path to the root
   * @return the object id
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param parent the parent object
   * @param child the object to persist
   * @return the object id
   * @throws NullPointerException if any argument is not valid
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object relative to the root node.
   *
   * @param o the object to persist
   * @return the object id
   * @throws NullPointerException if any argument is not valid
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param parent the parent object
   * @param o the object to persist
   * @param relPath the object relative path to the parent
   * @return the object id
   * @throws NullPointerException if the parent or object argument is null
   * @throws IllegalArgumentException if any argument is not valid
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object o, String relPath) throws NullPointerException, IllegalArgumentException, ChromatticException;

  <O> O findByPath(Object o, Class<O> clazz, String relPath) throws ChromatticException;

  <O> O findByPath(Class<O> clazz, String relPath) throws ChromatticException;

  <O> O findByNode(Class<O> clazz, Node node) throws ChromatticException;

  <O> O findById(Class<O> clazz, String id) throws ChromatticException;

  Query createQuery(QueryLanguage language, String statement) throws ChromatticException; 

  void remove(Object o) throws ChromatticException;

  Status getStatus(Object o) throws ChromatticException;

  String getId(Object o) throws ChromatticException;

  String getName(Object o) throws ChromatticException;

  String getPath(Object o) throws ChromatticException;

  /**
   * Returns a virtual object from the specified object
   *
   * @param o the real object
   * @param <O> the object type parameter
   * @return the corresponding virtual object
   * @throws ChromatticException any exception
   */
  // <O> O getVirtualObject(O o) throws ChromatticException;

  <O> void addLifeCycleListener(LifeCycleListener<O> listener);

  void save() throws ChromatticException;

  void close();

  Session getJCRSession();

}
