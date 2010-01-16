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

import org.chromattic.api.event.EventListener;
import org.chromattic.api.query.QueryBuilder;

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
   * @throws IllegalArgumentException if the specified class is not a chromattic class
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
   * @throws IllegalArgumentException if the name format is not valid or the class is not a chromattic class
   * @throws ChromatticException any chromattic exception
   */
  <O> O create(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Creates a persistent instance of the specified object.
   *
   * @param clazz the object class
   * @param name the name under root node
   * @param <O> the object class parameter
   * @return the persistent object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the name is not valid or the class is not a chromattic class
   * @throws ChromatticException any chromattic exception
   */
  <O> O insert(Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Creates a persistent instance of the specified object.
   *
   * @param parent the parent object
   * @param clazz the object class
   * @param name the object name
   * @param <O> the object class parameter
   * @return the persistent object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the name is not valid or the class is not a chromattic class or the parent is
   * not a persistent object
   * @throws ChromatticException any chromattic exception
   */
  <O> O insert(Object parent, Class<O> clazz, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param o the object to persist
   * @param name the object relative path to the root
   * @return the object id
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the name is not valid or the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param parent the parent object
   * @param child the object to persist
   * @return the object id
   * @throws NullPointerException if any argument is not valid
   * @throws IllegalArgumentException if the parent is not a persistent object or the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object relative to the root node.
   *
   * @param o the object to persist
   * @return the object id
   * @throws NullPointerException if any argument is not valid
   * @throws IllegalArgumentException if the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object.
   *
   * @param parent the parent object
   * @param o the object to persist
   * @param name the object relative name to the parent
   * @return the object id
   * @throws NullPointerException if the parent or object argument is null
   * @throws IllegalArgumentException if the parent is not a persistent object or the name is not valid or the object
   * is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Finds an object with a path relative to a specified origin object. If the object is not found then the method returns null.
   *
   * @param origin the origin object
   * @param clazz the expected class
   * @param relPath the path relative to the origin
   * @param <O> the object type
   * @return the object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the origin object is not a chromattic object
   * @throws ClassCastException if the object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findByPath(Object origin, Class<O> clazz, String relPath) throws NullPointerException, IllegalArgumentException, ClassCastException, ChromatticException;

  /**
   * Finds an object with a path relative to a chromattic root object. If the object is not found then the method returns null.
   *
   * @param clazz the expected class
   * @param relPath the path relative to the chromattic root
   * @param <O> the object type
   * @return the object
   * @throws NullPointerException if any argument is null
   * @throws ClassCastException if the object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findByPath(Class<O> clazz, String relPath) throws NullPointerException, ClassCastException, ChromatticException;

  /**
   * Finds an object mapped to the provided node.
   *
   * @param clazz the expected class
   * @param node the node
   * @param <O> the object type
   * @return the object
   * @throws NullPointerException if any argument is null
   * @throws ClassCastException if the mapped object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findByNode(Class<O> clazz, Node node) throws NullPointerException, ClassCastException, ChromatticException;

  /**
   * Finds an object from its identifier or return null if no such object can be found.
   *
   * @param clazz the expected class
   * @param id the id
   * @param <O> the object type
   * @return the object
   * @throws NullPointerException if any argument is null
   * @throws ClassCastException if the mapped object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findById(Class<O> clazz, String id) throws NullPointerException, ClassCastException, ChromatticException;

  QueryBuilder<?> createQueryBuilder() throws ChromatticException;

  void remove(Object o) throws ChromatticException;

  Status getStatus(Object o) throws ChromatticException;

  String getId(Object o) throws ChromatticException;

  String getName(Object o) throws ChromatticException;

  String getPath(Object o) throws ChromatticException;

  void addEventListener(EventListener listener);

  void save() throws ChromatticException;

  void close();

  Session getJCRSession();

}
