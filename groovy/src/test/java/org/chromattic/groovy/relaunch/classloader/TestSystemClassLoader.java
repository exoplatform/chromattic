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

package org.chromattic.groovy.relaunch.classloader;

import groovy.lang.GroovyClassLoader;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import junit.framework.TestCase;
import org.chromattic.groovy.relaunch.annotations.FromClass;
import org.chromattic.groovy.relaunch.builder.GroovyFromJavaSourceTestBuilder;
import org.chromattic.groovy.relaunch.classloader.exceptions.LoaderException;
import org.chromattic.groovy.relaunch.sourceloader.JavaSourceLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class TestSystemClassLoader extends ClassLoader {
  private final static Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
  private final static Map<Class<? extends TestClassLoader>, TestClassLoader> loaderCache = new HashMap<Class<? extends TestClassLoader>, TestClassLoader>();
  private final static Map<String, Class<? extends TestClassLoader>> dependenciesLoaders = new HashMap<String, Class<? extends TestClassLoader>>();

  public TestSystemClassLoader(ClassLoader parent) {
    super(parent);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> clazz = super.loadClass(name, resolve);
    // Use all classloaders definied with @FromClass
    for (String importName : dependenciesLoaders.keySet()) {
      if (name.startsWith(importName)) {
        clazz = getSpecificClassLoader(dependenciesLoaders.get(importName)).loadClass(name, clazz);
      }
    }
    return clazz;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    // If clazz is in the cache, load from the cache.
    if (classCache.containsKey(name)) {
      return classCache.get(name);
    }

    //
    else {
      Class<?> clazz =  super.loadClass(name); // Initial implementation

      if (isDelegateTestCase(clazz)) {
        try {
          // Override test's implementation from source.
          List<String> imports = new ArrayList<String>();
          clazz = implementationFromSource(clazz, imports);
          for (String importName : imports) {
            dependenciesLoaders.put(importName, ChromatticTestClassLoader.class);
          }
        } catch (ParseException e) {
          throw new LoaderException("Unable to parse source code.", e);
        }
      }
      classCache.put(name, clazz);
      return clazz;
    }
  }

  private boolean isDelegateTestCase(Class<?> clazz) {
    return
      TestCase.class.equals(clazz.getSuperclass())
      && useCustomClassloader(clazz);
  }

  private boolean useCustomClassloader(Class<?> clazz) {
    return clazz.isAnnotationPresent(FromClass.class);
  }

  private Class<?> implementationFromSource(Class<?> clazz, List<String> imports) throws ParseException {
    FromClass fromClass = clazz.getAnnotation(FromClass.class);
    InputStream cuis = JavaSourceLoader.getSource(fromClass.sourceClass().getName());
    CompilationUnit unit = JavaParser.parse(cuis);
    GroovyFromJavaSourceTestBuilder groovyFromJavaSourceTestBuilder = new GroovyFromJavaSourceTestBuilder(clazz, unit);
    groovyFromJavaSourceTestBuilder.build(imports);
    return new GroovyClassLoader().parseClass(groovyFromJavaSourceTestBuilder.toString());
  }

  private TestClassLoader getSpecificClassLoader(Class<? extends TestClassLoader> classClassLoader) {
    if (loaderCache.containsKey(classClassLoader)) {
      return loaderCache.get(classClassLoader);
    } else {
      try {
        return classClassLoader.getConstructor(ClassLoader.class).newInstance(this);
      } catch (Exception e) {
        throw new LoaderException(e);
      }
    }
  }
}
