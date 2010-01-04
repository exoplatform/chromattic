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

package org.chromattic.test.onetoone.mixin;

import org.chromattic.api.RelationshipType;
import org.chromattic.api.annotations.MappedBy;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.OneToOne;
import org.chromattic.api.annotations.Property;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@MixinType(names = "otom_c")
public abstract class C {

  @OneToOne(type = RelationshipType.EMBEDDED)
  abstract B getEntity();

  abstract void setEntity(B b);

  @Property(name = "foo")
  public abstract void setFoo(String foo);

  public abstract String getFoo();

  @OneToOne
  @MappedBy("b")
  public abstract B getB();

  public abstract void setB(B b);

}
