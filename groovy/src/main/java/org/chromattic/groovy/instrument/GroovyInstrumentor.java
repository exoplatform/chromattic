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

package org.chromattic.groovy.instrument;

import org.chromattic.apt.Instrumented;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.spi.instrument.ProxyFactory;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyInstrumentor implements Instrumentor {
  public <O> ProxyFactory<O> getProxyClass(Class<O> clazz) {
    return new GroovyProxyFactory(clazz);
  }

  public MethodHandler getInvoker(Object proxy) {
    //if (proxy instanceof Instrumented) {
      try {
        Field f = proxy.getClass().getDeclaredField("chromatticInvoker");
        boolean initialIsAccessible = f.isAccessible();
        if (!initialIsAccessible) {
          f.setAccessible(true);
        }
        Object o = f.get(proxy);
        f.setAccessible(initialIsAccessible);
        return (MethodHandler) o;
      }
      catch (NoSuchFieldException e) {
        throw new AssertionError(e);
      }
      catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    /*} else {
      return null;
    }*/
  }
}
