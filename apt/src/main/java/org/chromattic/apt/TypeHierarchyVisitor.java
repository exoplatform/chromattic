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

package org.chromattic.apt;

/**
 * Visitor pattern for a java class/interface type hierarchy. The visitor strategy
 * for a specified type is recursive
 * <ol>
 *   <li>Invoke the method {@link #enter(Class)} with the type. When the method returns false it ends the visit.</li>
 *   <li>Continue the visit recursively on the type superclass when it does have one.</li>
 *   <li>Continue the visit recursively on each implemented interfaces.</li>
 *   <li>Invoke the method {@link #leave(Class)} to signal that visit of the type is terminated.</li>
 * </ol>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeHierarchyVisitor {

  public void accept(Class type) {
    _accept(type);
  }

  private boolean _accept(Class type) {
    if (enter(type)) {
      if (type.isInterface()) {
        for (Class superInterface : type.getInterfaces()) {
          if (!_accept(superInterface)) {
            return false;
          }
        }
      } else {
        Class superType = type.getSuperclass();
        if (superType != null) {
          if (!_accept(superType)) {
            return false;
          }
        }
        for (Class implementedInterface : type.getInterfaces()) {
          if (!_accept(implementedInterface)) {
            return false;
          }
        }
      }

      //
      leave(type);
    }
    return true;
  }

  /**
   * Subclass to control the visitor.
   *
   * @param type the visited type
   * @return true if the type shall be visited
   */
  protected boolean enter(Class type) {
    return true;
  }

  /**
   * Signal that the visit of the type is terminated.
   *
   * @param type the type
   */
  protected void leave(Class type) {
  }
}