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
package org.chromattic.core;

import org.chromattic.common.TypeLiteral;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public abstract class ThrowableFactory<E extends Throwable> {

  public E newThrowable() {
    return newThrowable(null, null);
  }

  public E newThrowable(String msg) {
    return newThrowable(null, msg);
  }

  public E newThrowable(Throwable cause) {
    return newThrowable(cause, null);
  }

  public String toString() {
    return "ThrowableFactory[" + TypeLiteral.get(getClass(), 0).getSimpleName() + "]";
  }

  public abstract E newThrowable(Throwable cause, String msg);

  public static ThrowableFactory<NullPointerException> NPE = new ThrowableFactory<NullPointerException>() {
    @Override
    public NullPointerException newThrowable(Throwable cause, String msg) {
      NullPointerException iae = new NullPointerException(msg);
      iae.initCause(cause);
      return iae;
    }
  };

  public static ThrowableFactory<IllegalArgumentException> IAE = new ThrowableFactory<IllegalArgumentException>() {
    @Override
    public IllegalArgumentException newThrowable(Throwable cause, String msg) {
      IllegalArgumentException iae = new IllegalArgumentException(msg);
      iae.initCause(cause);
      return iae;
    }
  };

  public static ThrowableFactory<IllegalStateException> ISE = new ThrowableFactory<IllegalStateException>() {
    @Override
    public IllegalStateException newThrowable(Throwable cause, String msg) {
      IllegalStateException ise = new IllegalStateException(msg);
      ise.initCause(cause);
      return ise;
    }
  };

  public static ThrowableFactory<AssertionError> ASSERT = new ThrowableFactory<AssertionError>() {
    @Override
    public AssertionError newThrowable(Throwable cause, String msg) {
      AssertionError ae = new AssertionError(msg);
      ae.initCause(cause);
      return ae;
    }
  };

  public static ThrowableFactory<UnsupportedOperationException> UNSUPPORTED = new ThrowableFactory<UnsupportedOperationException>() {
    @Override
    public UnsupportedOperationException newThrowable(Throwable cause, String msg) {
      UnsupportedOperationException ae = new UnsupportedOperationException(msg);
      ae.initCause(cause);
      return ae;
    }
  };

  public static ThrowableFactory<UnsupportedOperationException> TODO = new ThrowableFactory<UnsupportedOperationException>() {
    @Override
    public UnsupportedOperationException newThrowable(Throwable cause, String msg) {
      UnsupportedOperationException ae = new UnsupportedOperationException(msg != null ? ("todo :" + msg) : "todo");
      ae.initCause(cause);
      return ae;
    }
  };
}
