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

package org.chromattic.api.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Defines a creator method to create transient instances of a Chromattic entity.
 *
 * The annotated method can have the following arguments:
 * <ul>
 * <li>No arguments that returns a transient entity.</li>
 * <li>A single <code>String</code> argument that returns a transient entity. That entity has a name
 * that is equals to the argument value when the method is invoked.</li>
 * </ul>
 *
 * The type of the of the returned entity is defined by the return type of the method that must be the type
 * of a registered instantiatable Chromattic entity.
 *
 * After the invocation of the method, the status of the entity will be equals to {@link org.chromattic.api.Status#TRANSIENT}.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Create {
}
