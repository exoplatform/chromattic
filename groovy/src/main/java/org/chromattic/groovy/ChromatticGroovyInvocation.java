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

package org.chromattic.groovy;

import org.chromattic.spi.instrument.MethodHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticGroovyInvocation {
  public static Object getProperty(Object target, String m, MethodHandler handler) {
    return invokeMethod(target, GroovyUtils.getsetName(GroovyUtils.GetSet.GET, m), new Object[]{}, handler);
  }

  public static Object setProperty(Object target, String m, Object v, MethodHandler handler) {
    return invokeMethod(target, GroovyUtils.getsetName(GroovyUtils.GetSet.SET, m), new Object[]{v}, handler);
  }

  public static Object invokeMethod(Object target, String m, Object p, MethodHandler handler) {
    Method method;
    try {
      method = target.getClass().getMethod(m, args2Class(p));
    } catch (NoSuchMethodException _) {
      // If method cannot be found, the method is getter or setter and the field is public and not annoted by chrommatic.
      // We directly access to it.
      try {
        Field field = target.getClass().getField(GroovyUtils.fieldName(m));
        return field.get(target);
      } catch (Exception e) {
        throw new AssertionError(e);
      }
    }

    // method exist
    try {
      if (isChromatticAnnoted(method)) {
        return handler.invoke(target, method, (Object[]) p);
      }
      else
        return target.getClass().getMethod(m, args2Class(p)).invoke(target, (Object[]) p);
      } catch (Throwable t) {
        throw new AssertionError(t);
      }
    }

  private static Class[] args2Class (Object objs) {
    List<Class> classes = new ArrayList<Class>();
    for (Object o : (Object[]) objs) {
        classes.add(o.getClass());
    }
    return classes.toArray(new Class[]{});
  }

  public static boolean isChromatticAnnoted(Method method) {
    for (Annotation annotation : method.getAnnotations()) {
      if (annotation.toString().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE, 1)) return true;
    }
    return false;
  }
}
