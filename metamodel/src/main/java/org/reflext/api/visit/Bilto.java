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

package org.reflext.api.visit;

import org.chromattic.api.annotations.PrimaryType;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.annotation.AnnotationType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Bilto {

  public static HierarchyVisitorStrategy get() {
    return new HierarchyVisitorStrategy() {
      boolean done = false;
      @Override
      boolean accept(ClassTypeInfo type) {
        PrimaryType pt = type.getDeclaredAnnotation(AnnotationType.get(PrimaryType.class));
        if (!done) {
          done = true;
          return true;
        } else {
          return pt == null || pt.abstract_();
        }
      }
    };
  }

}
