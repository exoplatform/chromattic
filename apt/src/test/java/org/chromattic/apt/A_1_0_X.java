/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.apt;

import org.chromattic.api.annotations.PrimaryType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "foo.bar")
public abstract class A_1_0_X {

  public abstract void a();
  public abstract void a(Object o);
  public abstract void a(int i);
  public abstract void a(boolean b);
  public abstract void a(int i, boolean b);
  public abstract void a(Object o, long l);
  public abstract void a(Object[] o);
  public abstract void a(int[] o);

  public abstract Object b();
  public abstract Object b(Object o);
  public abstract Object b(int i);
  public abstract Object b(boolean b);
  public abstract Object b(int i, boolean b);
  public abstract Object b(Object o, long l);
  public abstract Object b(Object[] o);
  public abstract Object b(int[] o);

}
