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

package org.chromattic.api.event;

/**
 * Enables to be aware of the life cycle of the object managed by chromattic with respect to the underlying
 * JCR session. Those life cycle callbacks does not guarantees that they will translate to operations
 * with the persitence storage as there are not guarantees that the session will be saved.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface LifeCycleListener extends EventListener {

  /**
   * An object is created.
   *
   * @param o the created object
   */
  void created(Object o);

  /**
   * An object is loaded from the session.
   *
   * @param id the id of the object
   * @param path the path of the object
   * @param name the name of the object
   * @param o the object
   */
  void loaded(String id, String path, String name, Object o);

  /**
   * An object is added to the session.
   *
   * @param id the id of the object
   * @param path the path of the object
   * @param name the name of the object
   * @param o the object
   */
  void added(String id, String path, String name, Object o);

  /**
   * An object is removed from the session.
   *
   * @param id the id of the object
   * @param path the path of the object
   * @param name the name of the object
   * @param o the object
   */
  void removed(String id ,String path, String name, Object o);

}
