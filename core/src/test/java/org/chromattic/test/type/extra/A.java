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

package org.chromattic.test.type.extra;

import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

import javax.jcr.PropertyType;
import java.util.Calendar;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "tts_b")
public abstract class A {

  @Property(name = "bytes")
  public abstract byte[] getBytes();

  public abstract void setBytes(byte[] te);

  @Property(name = "calendar")
  public abstract Calendar getCalendar();

  public abstract void setCalendar(Calendar te);

  @Property(name = "timestamp", type = PropertyType.DATE)
  public abstract Long getTimestamp();

  public abstract void setTimestamp(Long te);

}