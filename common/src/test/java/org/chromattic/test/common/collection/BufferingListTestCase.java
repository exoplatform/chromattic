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
package org.chromattic.test.common.collection;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.chromattic.common.collection.ElementInsertion;
import org.chromattic.common.collection.ListModel;
import org.chromattic.common.collection.BufferingList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BufferingListTestCase extends TestCase {

  
  private static class ListModelImpl<E> implements ListModel<E> {

    /** . */
    private ArrayList<E> state;

    private ListModelImpl() {
      state = new ArrayList<E>();
    }

    public Iterator<E> iterator() {
      return state.iterator();
    }

    public void set(int index, E removedElement, E addedElement) {
      state.set(index, addedElement);
    }

    public void add(ElementInsertion<E> insertion) {
      state.add(insertion.getIndex(), insertion.getElement());
    }

    public void remove(int index, E removedElement) {
      state.remove(index);
    }

    public int size() {
      return state.size();
    }
  }

  public void testSimple() {

    List<Integer> tmp = new BufferingList<Integer>(new ListModelImpl<Integer>());

    tmp.add(10);
    tmp.add(11);
    tmp.add(12);

    assertEquals(Arrays.asList(10, 11 ,12), tmp);

    tmp.remove(1);

    assertEquals(Arrays.asList(10, 12), tmp);

    tmp.subList(0, 0).add(9);

    assertEquals(Arrays.asList(9, 10, 12), tmp);

  }
  
}
