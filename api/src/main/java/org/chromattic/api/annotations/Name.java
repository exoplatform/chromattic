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
 * Annotates a bean property to map it against the current name of the related jcr node.
 * <ul>
 * <li>When the entity is {@link org.chromattic.api.Status#TRANSIENT} the name is saved along with the entity
 * until it is persisted. When the entity is persisted, the name is used when no additional name is provided.</li>
 * <li>When the entity is {@link org.chromattic.api.Status#PERSISTENT} the name is associated with the current JCR
 * node name. A property read returns the current node name and a property write performs a JCR move operation to
 * rename the entity with the new name.</li>
 * <li>When the entity is {@link org.chromattic.api.Status#REMOVED} any property access throws an
 * {@link IllegalStateException}.</li>
 * </ul>
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Name {
}
