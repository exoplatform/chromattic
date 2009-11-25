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

package org.chromattic.test.onetomany.hierarchical.map;

import org.chromattic.test.onetomany.hierarchical.AbstractMultiChildrenTestCase;
import org.chromattic.test.support.AbstractMapToCollectionAdapter;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MultiChildrenTestCase extends AbstractMultiChildrenTestCase<M1, M2, M3, M4> {

  @Override
  public <M extends M2> Collection<M> getMany(M1 one, Class<M> manySide) {
    Map<String, M> c;
    if (manySide == M2.class) {
      c = (Map<String, M>)one.getBs();
    } else if (manySide == M3.class) {
      c = (Map<String, M>)one.getCs();
    } else if (manySide == M4.class) {
      c = (Map<String, M>)one.getDs();
    } else {
      throw new AssertionError();
    }
    return new AbstractMapToCollectionAdapter<M>(c) {
      @Override
      public String getKey(M m) {
        return m.getName();
      }
    };
  }
}