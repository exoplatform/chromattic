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

package org.chromattic.metamodel.typegen;

import org.chromattic.metamodel.mapping.jcr.PropertyDefinitionMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyDefinition {

  PropertyDefinition(String name, boolean multiple, int type) {
    this.name = name;
    this.multiple = multiple;
    this.type = type;
    this.defaultValues = null;
    this.valueConstraints = null;
  }

  PropertyDefinition(PropertyDefinitionMapping<?> mapping, boolean multiple) {

    //
    List<String> defaultValues = mapping.getDefaultValue();
    if (defaultValues == null) {
      defaultValues = null;
    } else {
      defaultValues = new ArrayList<String>(defaultValues);
    }

    //
    this.multiple = multiple;
    this.name = mapping.getName();
    this.type = mapping.getMetaType().getCode();
    this.defaultValues = defaultValues;
    this.valueConstraints = null;
  }

  /** . */
  private final String name;

  /** . */
  private final boolean multiple;

  /** . */
  private final int type;

  /** . */
  private List<String> defaultValues;

  /** . */
  private List<String> valueConstraints;

  public String getName() {
    return name;
  }

  public boolean isMultiple() {
    return multiple;
  }

  public int getType() {
    return type;
  }

  public List<String> getDefaultValues() {
    return defaultValues;
  }

  public List<String> getValueConstraints() {
    return valueConstraints;
  }

  void addValueConstraint(String valueConstraint) {
    if (valueConstraints == null) {
      valueConstraints = new ArrayList<String>();
    }
    valueConstraints.add(valueConstraint);
  }
}
