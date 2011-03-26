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
package org.chromattic.core.bean;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class SimpleTypeKind<E, I> {
  
  private SimpleTypeKind() {

  }

  public abstract static class STRING<E> extends SimpleTypeKind<E, String> { }

  public abstract static class PATH<E> extends SimpleTypeKind<E, String> { }

  public abstract static class BOOLEAN<E> extends SimpleTypeKind<E, Boolean> { }

  public abstract static class LONG<E> extends SimpleTypeKind<E, Long> { }

  public abstract static class DATE<E> extends SimpleTypeKind<E, Date> { }

  public abstract static class DOUBLE<E> extends SimpleTypeKind<E, Double> { }

  public abstract static class STREAM<E> extends SimpleTypeKind<E, InputStream> { }

}
