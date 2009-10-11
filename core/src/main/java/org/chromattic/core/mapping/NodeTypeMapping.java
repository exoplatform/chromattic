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
package org.chromattic.core.mapping;

import org.chromattic.api.NameConflictResolution;
import org.reflext.api.ClassTypeInfo;

import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeMapping extends TypeMapping {

  /** . */
  private final String nodeTypeName;

  /** . */
  private final Set<String> mixinNames;

  public NodeTypeMapping(
    ClassTypeInfo objectClass,
    Set<PropertyMapping> propertyMappings,
    Set<MethodMapping> methodMappings,
    NameConflictResolution onDuplicate,
    String nodeTypeName,
    Set<String> mixinNames) {
    super(
      objectClass,
      propertyMappings,
      methodMappings,
      onDuplicate);

    //
    this.nodeTypeName = nodeTypeName;
    this.mixinNames = mixinNames;
  }

  public Set<String> getMixinNames() {
    return mixinNames;
  }

  public String getNodeTypeName() {
    return nodeTypeName;
  }

  @Override
  public String toString() {
    return "JavaTypeInfo[objectClass=" + objectClass.getName() + ",nodeTypeName=" + nodeTypeName + "]";
  }
}
