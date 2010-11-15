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
 * The session manages Chromattic objects at runtime, it is obtained from a {@link org.chromattic.api.Chromattic}
 * instance.  A session is meant to be used by one thread and not shared among threads. 
 *
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
   * Persists a transient object with the specified name. The parent of the persisted object will implicitely be the
   * root node of the session.
   *
   * @param o the object to persist
   * @param name the object relative path to the root
   * @return the id of the inserted object
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the name is not valid or the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object as a child of the specified parent. The parent argument is optional and null can be
   * passed, in that case it is equivalent to call the {@link #persist(Object)} method. Since no name is provided, this
   * method implicitely expects a name associated with the object.
   *
   * @param parent the parent object
   * @param child the object to persist
   * @return the id of the inserted object
   * @throws NullPointerException if the child argument is null
   * @throws IllegalArgumentException if the parent is not a persistent object or the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object child) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object relative to the root node. The parent of the persisted object will implicitely be the
   * root node of the session. Since no name is provided, this method implicitely expects a name associated with the object.
   *
   * @param o the object to persist
   * @return the id of the inserted object
   * @throws NullPointerException if any argument is not valid
   * @throws IllegalArgumentException if the object is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Persists a transient object as a child of the specified parent with the specified name. The parent argument is
   * optional and null can be passed, in that case it is equivalent to call the {@link #persist(Object, String)} method.
   *
   * @param parent the parent object
   * @param o the object to persist
   * @param name the object relative name to the parent
   * @return the id of the inserted object
   * @throws NullPointerException if the object argument is null
   * @throws IllegalArgumentException if the parent is not a persistent object or the name is not valid or the object
   * is not a chromattic transient object
   * @throws ChromatticException any chromattic exception
   */
  String persist(Object parent, Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Clones a persistent object into a new persistent object.
   *
   * @param parent the parent object
   * @param o the object to clone
   * @param name the object name
   * @param <O> the object type
   * @return the cloned object
   * @throws NullPointerException if the parent or object argument is null
   * @throws IllegalArgumentException if the parent is not a persistent object or the name is not valid or the object
   * is not a chromattic persistent object
   * @throws ChromatticException any chromattic exception
   */
  <O> O copy(Object parent, O o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Clones a persistent object into a new persistent object.
   *
   * @param o the object to clone
   * @param name the object name
   * @param <O> the object type
   * @return the cloned object
   * @throws NullPointerException if the parent or object argument is null
   * @throws IllegalArgumentException if the parent is not a persistent object or the name is not valid or the object
   * is not a chromattic persistent object
   * @throws ChromatticException any chromattic exception
   */
  <O> O copy(O o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Finds an object with a path relative to a specified origin object. If the object is not found then the method returns null.
   * The origin argument is optional and null can be passed, in that case it is equivalent to call the {@link #findById(Class, String)}
   * method.
   *
   * @param origin the origin object
   * @param clazz the expected class
   * @param relPath the path relative to the origin
   * @param <O> the object type
   * @return the object
   * @throws IllegalArgumentException if the origin object is not a chromattic object
   * @throws NullPointerException if any argument except the origin is null
   * @throws ClassCastException if the object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findByPath(Object origin, Class<O> clazz, String relPath) throws IllegalArgumentException, NullPointerException, IllegalArgumentException, ClassCastException, ChromatticException;

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
   * Finds an object given its specified path. The provided path can either be absolute or relative to the
   * root node of the session according to the value of the <code>absolute</code> method parameter. When the object
   * is not found the method returns null.
   *
   * @param clazz the expected class
   * @param path the path relative to the chromattic root
   * @param <O> the object type
   * @param absolute true when an absolute path must be provided
   * @return the object
   * @throws NullPointerException if any argument is null
   * @throws ClassCastException if the object cannot be cast to the specified class
   * @throws ChromatticException any chromattic exception
   */
  <O> O findByPath(Class<O> clazz, String path, boolean absolute) throws NullPointerException, ClassCastException, ChromatticException;

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

  /**
   * Create a query builder.
   *
   * @param fromClass the node type of the from clause
   * @param <O> the object type parameter
   * @return a query builder
   * @throws NullPointerException if the argument is null
   * @throws IllegalArgumentException if the from class cannot be mapped to a node type
   * @throws ChromatticException any chromattic exception
   */
  <O> QueryBuilder<O> createQueryBuilder(Class<O> fromClass) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Removes a specified entity.
   *
   * @param o the entity to remove
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   */
  void remove(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Returns the status of a specified entity.
   *
   * @param o the entity to get the status
   * @return the entity status
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   */
  Status getStatus(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Returns the id of a specified entity.
   *
   * @param o the entity to get the id
   * @return the entity id
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   */
  String getId(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Returns the name of a specified entity.
   *
   * @param o the entity to get the name
   * @return the entity status
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   */
  String getName(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Rename a chromattic object, the behavior of this method depends on the current object status:
   *
   * <ul>
   * <li>{@link Status#TRANSIENT}: the object is merely associated with the name until it is persisted.</li>
   * <li>{@link Status#PERSISTENT}: the object will be renamed with the new name.</li>
   * <li>{@link Status#REMOVED}: An illegal state exception is thrown.</li>
   * <lI>
   *
   * @param o the entity to get the name
   * @param name the new entity name
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object or has been destroyed.
   */
  void setName(Object o, String name) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Returns the path of a specified entity.
   *
   * @param o the entity to get the path
   * @return the entity path
   * @throws ChromatticException any chromattic exception
   * @throws NullPointerException if the specified object is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   */
  String getPath(Object o) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Returns a specified embedded object.
   *
   * @param o the object to get the embedded from
   * @param embeddedType the embedded type class
   * @param <E> the embedded type
   * @return the related embedded
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the specified object is not a chromattic object
   * @throws ChromatticException any chromattic exception
   */
  <E> E getEmbedded(Object o, Class<E> embeddedType) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Attach the specified embedded object on the specified object when the embedded argument.
   *
   * @param o the object
   * @param embeddedType the embedded type class
   * @param embedded the embedded
   * @param <E> the embedded type
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if the object or the embedded are not chromattic objects
   * @throws ChromatticException any chromattic exception
   */
  <E> void setEmbedded(Object o, Class<E> embeddedType, E embedded) throws NullPointerException, IllegalArgumentException, ChromatticException;

  /**
   * Adds an event listener to this session.
   *
   * @param listener the listener to add
   * @throws NullPointerException if the provided listener is null
   */
  void addEventListener(EventListener listener) throws NullPointerException;

  /**
   * Save the transient changes.
   *
   * @throws ChromatticException any chromattic exception
   */
  void save() throws ChromatticException;

  /**
   * Close and dispose the session.
   */
  void close();

  boolean isClosed();

  /**
   * Returns the underlying JCR session or null if the session is closed.
   *
   * @return the JCR session.
   */
  Session getJCRSession();

}
