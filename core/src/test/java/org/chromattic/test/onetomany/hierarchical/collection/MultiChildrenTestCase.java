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
package org.chromattic.test.onetomany.hierarchical.collection;

import org.chromattic.test.onetomany.hierarchical.AbstractMultiChildrenTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {M1.class, M2.class, M3.class, M4.class})
public class MultiChildrenTestCase extends AbstractMultiChildrenTestCase<M1, M2, M3, M4> {

  @Override
  public <T extends M2> Collection<T> getMany(M1 one, Class<T> manySide) {
    if (manySide == M2.class) {
      return (Collection<T>)one.getBs();
    } else if (manySide == M3.class) {
      return (Collection<T>)one.getCs();
    } else if (manySide == M4.class) {
      return (Collection<T>)one.getDs();
    } else {
      throw new AssertionError();
    }
  }
}
