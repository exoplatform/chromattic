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
package org.chromattic.core.mapper;

import org.chromattic.core.ObjectContext;
import org.reflext.api.ClassTypeInfo;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MethodMapper {

  /** . */
  TypeMapper mapper;

  /** . */
  private final Method method;

  public MethodMapper(Method method) {
    this.method = method;
  }

  public Object invoke(ObjectContext context, Object[] args) throws Throwable {
    throw new UnsupportedOperationException();
  }

  public Method getMethod() {
    return method;
  }

  @Override
  public String toString() {
    return "MethodMapper[" + method.getDeclaringClass().getName() + "#" + method.getName() + "]";
  }

  public static class Create extends MethodMapper {

    /** . */
    TypeMapper type;

    public Create(Method method) {
      super(method);
    }

    @Override
    public Object invoke(ObjectContext context, Object[] args) {

      //
      String name = null;
      if (args.length == 1 && args[0] != null && args[0] instanceof String) {
         name = (String)args[0];
      }

      //
      if (name == null) {
        return context.getSession().create(type.getObjectClass());
      } else {
        return context.getSession().create(type.getObjectClass(), name);
      }
    }
  }

  public static class FindById extends MethodMapper {

    /** . */
    private final ClassTypeInfo type;

    /** . */
    private final Class<?> typeClass;

    public FindById(Method method, ClassTypeInfo type) throws ClassNotFoundException {
      super(method);

      //
      this.type = type;
      this.typeClass = Thread.currentThread().getContextClassLoader().loadClass(type.getName());
    }

    @Override
    public Object invoke(ObjectContext context, Object[] args) throws Throwable {

      //
      String id = (String)args[0];

      //
      Object o = context.getSession().findById(Object.class, id);

      //
      if (typeClass.isInstance(o)) {
        return o;
      }

      //
      return null;
    }
  }

  public static class Destroy extends MethodMapper {

    public Destroy(Method method) {
      super(method);
    }

    @Override
    public Object invoke(ObjectContext context, Object[] args) throws Throwable {
      context.remove();
      return null;
    }
  }
}
