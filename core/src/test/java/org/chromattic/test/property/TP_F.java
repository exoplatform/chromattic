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
package org.chromattic.test.property;

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.Property;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "tp_c")
public abstract class TP_F {

  @Property(name = "string_array_property")
  public abstract List<String> getString();

  public abstract void setString(List<String> s);

  @Property(name = "int_array_property")
  public abstract List<Integer> getInt();

  public abstract void setInt(List<Integer> s);

  @Property(name = "long_array_property")
  public abstract List<Long> getLong();

  public abstract void setLong(List<Long> s);

  @Property(name = "boolean_array_property")
  public abstract List<Boolean> getBoolean();

  public abstract void setBoolean(List<Boolean> s);

  @Property(name = "date_array_property")
  public abstract List<Date> getDate();

  public abstract void setDate(List<Date> s);

}