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

import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;
import org.chromattic.metamodel.annotations.Skip;

import javax.jcr.PropertyType;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "property:b")
public abstract class TP_Undefined {

  @Property(name = "undefined_type", type = PropertyType.UNDEFINED)
  public abstract String getUndefinedType();

  public abstract void setUndefinedType(String undefinedType);

  @Skip
  @Property(name = "undefined_property", type = PropertyType.UNDEFINED)
  public abstract String getUndefinedProperty();

  public abstract void setUndefinedProperty(String undefinedProperty);

  @Skip
  @Property(name = "undefined_multivalued_property")
  public abstract List<String> getUndefinedMultivaluedProperty();

  public abstract void setUndefinedMultivaluedProperty(List<String> undefinedProperty);

}
