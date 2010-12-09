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

import java.io.IOError;
import java.io.IOException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "foo.bar")
public abstract class B_1_1_X {

  public abstract void a1();

  public abstract void b1() throws IOException;

  public abstract void c1() throws Exception;

  public abstract void d1() throws RuntimeException;

  public abstract void e1() throws IndexOutOfBoundsException;

  public abstract void f1() throws IOError;

  public abstract void g1() throws Error;

  public abstract String a2();

  public abstract String b2() throws IOException;

  public abstract String c2() throws Exception;

  public abstract String d2() throws RuntimeException;

  public abstract String e2() throws IndexOutOfBoundsException;

  public abstract String f2() throws IOError;

  public abstract String g2() throws Error;

  public abstract void a1(Object o);

  public abstract void b1(Object o) throws IOException;

  public abstract void c1(Object o) throws Exception;

  public abstract void d1(Object o) throws RuntimeException;

  public abstract void e1(Object o) throws IndexOutOfBoundsException;

  public abstract void f1(Object o) throws IOError;

  public abstract void g1(Object o) throws Error;

  public abstract String a2(Object o);

  public abstract String b2(Object o) throws IOException;

  public abstract String c2(Object o) throws Exception;

  public abstract String d2(Object o) throws RuntimeException;

  public abstract String e2(Object o) throws IndexOutOfBoundsException;

  public abstract String f2(Object o) throws IOError;

  public abstract String g2(Object o) throws Error;

  public abstract void a1(Object o1, Object o2);

  public abstract void b1(Object o1, Object o2) throws IOException;

  public abstract void c1(Object o1, Object o2) throws Exception;

  public abstract void d1(Object o1, Object o2) throws RuntimeException;

  public abstract void e1(Object o1, Object o2) throws IndexOutOfBoundsException;

  public abstract void f1(Object o1, Object o2) throws IOError;

  public abstract void g1(Object o1, Object o2) throws Error;

  public abstract String a2(Object o1, Object o2);

  public abstract String b2(Object o1, Object o2) throws IOException;

  public abstract String c2(Object o1, Object o2) throws Exception;

  public abstract String d2(Object o1, Object o2) throws RuntimeException;

  public abstract String e2(Object o1, Object o2) throws IndexOutOfBoundsException;

  public abstract String f2(Object o1, Object o2) throws IOError;

  public abstract String g2(Object o1, Object o2) throws Error;
}
