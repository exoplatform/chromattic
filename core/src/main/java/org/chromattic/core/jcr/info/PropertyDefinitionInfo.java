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

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyDefinitionInfo {

  /** . */
  private final String name;

  /** . */
  private final int type;

  /** . */
  private final boolean multiple;

  public PropertyDefinitionInfo(PropertyDefinition propertyDefinition) {
    this.name = propertyDefinition.getName();
    this.type = propertyDefinition.getRequiredType();
    this.multiple = propertyDefinition.isMultiple();
  }

  public PropertyDefinitionInfo(String name, int type, boolean multiple) {
    this.name = name;
    this.type = type;
    this.multiple = multiple;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public Value getValue(Property property) throws RepositoryException {
    if (multiple) {
      Value[] values = property.getValues();
      if (values.length == 0) {
        return null;
      } else {
        return values[0];
      }
    } else {
      return property.getValue();
    }
  }
}
