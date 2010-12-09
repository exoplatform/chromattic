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

package org.chromattic.test.property.value.multi.array;

import org.chromattic.api.annotations.DefaultValue;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;
import org.chromattic.metamodel.annotations.Skip;

import javax.jcr.PropertyType;
import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "property_array:a1")
public abstract class A1_2 {

  @Property(name = "string_property")
  public abstract String getStringProperty();

  @Property(name = "primitive_int_property")
  public abstract int getIntProperty();

  @Property(name = "int_property")
  public abstract Integer getIntegerProperty();

  @Property(name = "primitive_long_property")
  public abstract long getPrimitiveLongProperty();

  @Property(name = "long_property")
  public abstract Long getLongProperty();

  @Property(name = "primitive_boolean_property")
  public abstract boolean getPrimitiveBooleanProperty();

  @Property(name = "boolean_property")
  public abstract Boolean getBooleanProperty();

  @Property(name = "date_property")
  public abstract Date getDateProperty();
}