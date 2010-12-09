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

import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@Skip
@PrimaryType(name = "property_array:a1")
public abstract class A1_1 {

  @Property(name = "string_property")
  @DefaultValue
  public abstract String[] getStringProperty();

  public abstract void setStringProperty(String[] s);

  @Property(name = "primitive_int_property")
  @DefaultValue
  public abstract int[] getIntProperty();

  public abstract void setIntProperty(int[] s);

  @Property(name = "int_property")
  @DefaultValue
  public abstract Integer[] getIntegerProperty();

  public abstract void setIntegerProperty(Integer[] s);

  @Property(name = "primitive_long_property")
  @DefaultValue
  public abstract long[] getPrimitiveLongProperty();

  public abstract void setPrimitiveLongProperty(long[] s);

  @Property(name = "long_property")
  @DefaultValue
  public abstract Long[] getLongProperty();

  public abstract void setLongProperty(Long[] s);

  @Property(name = "primitive_boolean_property")
  @DefaultValue
  public abstract boolean[] getPrimitiveBooleanProperty();

  public abstract void setPrimitiveBooleanProperty(boolean[] s);

  @Property(name = "boolean_property")
  @DefaultValue
  public abstract Boolean[] getBooleanProperty();

  public abstract void setBooleanProperty(Boolean[] s);

  @Property(name = "date_property")
  @DefaultValue
  public abstract Date[] getDateProperty();

  public abstract void setDateProperty(Date[] s);

}