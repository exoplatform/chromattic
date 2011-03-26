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

package org.chromattic.test.onetomany.hierarchical.generic.map;

import org.chromattic.test.onetomany.hierarchical.generic.AbstractOneToManyTestCase;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class OneToMany1TestCase extends AbstractOneToManyTestCase<A3, B3> {

  @Override
  public Collection<B3> getMany(final A3 one) {
    return new AbstractCollection<B3>() {
      @Override
      public boolean add(B3 b3) {
        if (b3 == null) {
          one.getChildren().put(null, null);
          return true;
        } else {
          String name = b3.getName();
          one.getChildren().put(name, b3);
          return true;
        }
      }
      @Override
      public Iterator<B3> iterator() {
        return one.getChildren().values().iterator();
      }
      @Override
      public int size() {
        return one.getChildren().size();
      }
    };
  }

  @Override
  public A3 getOne(B3 many) {
    return many.getParent();
  }

  @Override
  public void setOne(B3 many, A3 one) {
    many.setParent(one);
  }
}