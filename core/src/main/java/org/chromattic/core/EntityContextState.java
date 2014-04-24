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

import org.chromattic.api.Status;
import org.chromattic.core.jcr.type.NodeTypeInfo;
import org.chromattic.core.jcr.type.PrimaryTypeInfo;
import org.chromattic.core.vt2.ValueDefinition;

import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class EntityContextState {

  abstract String getId();

  abstract String getLocalName();

  abstract String getPath();

  abstract Node getNode();

  abstract DomainSession getSession();

  abstract Status getStatus();

  abstract PrimaryTypeInfo getTypeInfo();

  abstract <V> boolean hasProperty(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt);

  abstract <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt);

  abstract <L, V> L getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType);

  abstract <V> void setPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, V o);

  abstract <L, V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueDefinition<?, V> vt, ArrayType<L, V> arrayType, L propertyValues);

}
