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

package org.chromattic.dataobject.runtime;

import groovy.lang.GroovyClassLoader;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.PrimaryType;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticInjector {

  public static void contextualize(Object obj, Object injected) {
    if (obj instanceof ChromatticSessionProxy) {
      // The delegating chromattic session
      ChromatticSessionProxy session = (ChromatticSessionProxy)obj;

      // Do we need to contextualize ?
      if (session.builder == null) {

        // Get injected type
        Class<?> injectedType = injected.getClass();
        GroovyClassLoader.InnerLoader cl = (GroovyClassLoader.InnerLoader)injectedType.getClassLoader();
        List<Class> classes = Arrays.asList(cl.getLoadedClasses());

        //
        try {
          ChromatticBuilder builder = ChromatticBuilder.create();

          // Add all chromattic type we can find in the classloader
          for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(PrimaryType.class) || clazz.isAnnotationPresent(MixinType.class)) {
              builder.add(clazz);
            }
          }

          // Configure default options
          builder.setOptionValue(ChromatticBuilder.CREATE_ROOT_NODE, true);
          builder.setOptionValue(ChromatticBuilder.LAZY_CREATE_ROOT_NODE, true);
          builder.setOptionValue(ChromatticBuilder.SESSION_LIFECYCLE_CLASSNAME, DataObjectSessionLifeCycle.class.getName());

          // Initialize the builder
          builder.init();

          //
          session.builder = builder;
        }
        catch (Exception e) {
          throw new AssertionError(e);
        }
      }
    }
  }
}
