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
package org.chromattic.core.jcr.info;

import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeInfo {

  /** . */
  private final String name;

  /** . */
  private final Map<String, PropertyDefinitionInfo> propertyDefinitions;

  public TypeInfo(NodeType nodeType) {
    Map<String, PropertyDefinitionInfo> propertyDefinitions = new HashMap<String, PropertyDefinitionInfo>();
    for (PropertyDefinition propertyDefinition : nodeType.getPropertyDefinitions()) {
      PropertyDefinitionInfo propertyDefinitionInfo = new PropertyDefinitionInfo(propertyDefinition);
      propertyDefinitions.put(propertyDefinitionInfo.getName(), propertyDefinitionInfo);
    }

    //
    this.name = nodeType.getName();
    this.propertyDefinitions = propertyDefinitions;
  }

  public String getName() {
    return name;
  }

  public PropertyDefinitionInfo getPropertyDefinitionInfo(String name) {
    return propertyDefinitions.get(name);
  }
}
