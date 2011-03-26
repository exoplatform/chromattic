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
package org.chromattic.common;

import org.chromattic.api.BuilderException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ObjectInstantiator {

  /**
   * Intantiates the object with the specified class name. The object class must have a public no argument constructor.
   * 
   * @param className the class name
   * @param expectedClass the expected class
   * @param <T> the type of the class name
   * @return the object instance
   * @throws BuilderException if anything goes wrong
   */
  public static <T> T newInstance(String className, Class<T> expectedClass) throws BuilderException {
    if (className == null) {
      throw new NullPointerException("No null class name expected");
    }
    if (expectedClass == null) {
      throw new NullPointerException("No null expected class provided");
    }
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Class<?> loadedClass = classLoader.loadClass(className);
      if (expectedClass.isAssignableFrom(loadedClass)) {
        Class<? extends T> expectedSubclass = loadedClass.asSubclass(expectedClass);
        return newInstance(expectedSubclass);
      } else {
        throw new BuilderException("Class " + className + " does not implement the " +
          expectedClass.getName() + " interface");
      }
    }
    catch (ClassNotFoundException e) {
      throw new BuilderException("Could not load class " + className, e);
    }
  }

  /**
   * Intantiates the object with the specified class. The object class must have a public no argument constructor.
   * 
   * @param objectClass the objct class
   * @param <T> the type of the class name
   * @return the object instance
   * @throws BuilderException if anything goes wrong
   */
  public static <T> T newInstance(Class<T> objectClass) throws BuilderException {
    if (objectClass == null) {
      throw new NullPointerException("No null object class provided");
    }
    try {
      if (!Modifier.isPublic(objectClass.getModifiers())) {
        throw new BuilderException("The class " + objectClass.getName() + " must be public");
      }
      if (Modifier.isAbstract(objectClass.getModifiers())) {
        throw new BuilderException("The class " + objectClass.getName() + " must not be abstract");
      }
      Constructor<? extends T> ctor = objectClass.getConstructor();
      if (!Modifier.isPublic(ctor.getModifiers())) {
        throw new BuilderException("The class " + objectClass.getName() + " no arg constructor is not public");
      }
      return ctor.newInstance();
    }
    catch (InstantiationException e) {
      throw new BuilderException("Could not instanciate class " + objectClass.getName(), e);
    }
    catch (IllegalAccessException e) {
      throw new BuilderException("Could not instanciate class " + objectClass.getName(), e);
    }
    catch (NoSuchMethodException e) {
      throw new BuilderException("The class " + objectClass.getName() + " does not have a no argument constructor", e);
    }
    catch (InvocationTargetException e) {
      throw new BuilderException("The class " + objectClass.getName() + " construction threw an exception", e.getCause());
    }
  }
}
