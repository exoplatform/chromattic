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
import org.chromattic.metamodel.bean.SimpleValueInfo;
import org.chromattic.core.jcr.info.NodeTypeInfo;
import org.chromattic.core.jcr.info.PrimaryTypeInfo;
import org.chromattic.core.vt.ValueType;

import javax.jcr.Node;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
abstract class EntityContextState {

  abstract String getId();

  abstract String getName();

  abstract String getPath();

  abstract Node getNode();

  abstract DomainSession getSession();

  abstract Status getStatus();

  abstract PrimaryTypeInfo getTypeInfo();

  abstract <V> V getPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt);

  abstract <V> List<V> getPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, ListType listType);

  abstract <V> void setPropertyValue(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, V o);

  abstract <V> void setPropertyValues(NodeTypeInfo nodeTypeInfo, String propertyName, ValueType<V> vt, ListType listType, List<V> objects);

}
