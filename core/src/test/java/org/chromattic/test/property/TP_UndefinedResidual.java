/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.chromattic.test.property;

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.Properties;
import org.chromattic.api.annotations.Property;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "tp_d")
public abstract class TP_UndefinedResidual {

  @Properties
  public abstract Map<String, Object> getProperties();

  @Property(name = "string_property")
  public abstract String getString();

  public abstract void setString(String s);

}
