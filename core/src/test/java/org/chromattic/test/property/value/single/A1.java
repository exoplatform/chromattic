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

package org.chromattic.test.property.value.single;

import org.chromattic.api.annotations.Path;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

import javax.jcr.PropertyType;
import java.util.Date;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "property_single:a")
public abstract class A1 {

  @Property(name = "string_property")
  public abstract String getStringProperty();

  public abstract void setStringProperty(String s);

  @Property(name = "path_property", type = PropertyType.PATH)
  public abstract String getPathProperty();

  public abstract void setPathProperty(String s);

  @Property(name = "primitive_int_property")
  public abstract int getIntProperty();

  public abstract void setIntProperty(int s);

  @Property(name = "int_property")
  public abstract Integer getIntegerProperty();

  public abstract void setIntegerProperty(Integer s);

  @Property(name = "primitive_long_property")
  public abstract long getPrimitiveLongProperty();

  public abstract void setPrimitiveLongProperty(long s);

  @Property(name = "long_property")
  public abstract Long getLongProperty();

  public abstract void setLongProperty(Long s);

  @Property(name = "primitive_boolean_property")
  public abstract boolean getPrimitiveBooleanProperty();

  public abstract void setPrimitiveBooleanProperty(boolean s);

  @Property(name = "boolean_property")
  public abstract Boolean getBooleanProperty();

  public abstract void setBooleanProperty(Boolean s);

  @Property(name = "primitive_float_property")
  public abstract float getPrimitiveFloatProperty();

  public abstract void setPrimitiveFloatProperty(float s);

  @Property(name = "float_property")
  public abstract Float getFloatProperty();

  public abstract void setFloatProperty(Float s);

  @Property(name = "primitive_double_property")
  public abstract double getPrimitiveDoubleProperty();

  public abstract void setPrimitiveDoubleProperty(double s);

  @Property(name = "double_property")
  public abstract Double getDoubleProperty();

  public abstract void setDoubleProperty(Double s);

  @Property(name = "date_property")
  public abstract Date getDateProperty();

  public abstract void setDateProperty(Date s);

  @Property(name = "bytes_property")
  public abstract InputStream getBytesProperty();

  public abstract void setBytesProperty(InputStream s);

  @Property(name = "missing_property")
  public abstract String getMissingProperty();

  public abstract void setMissingProperty(String s);

}
