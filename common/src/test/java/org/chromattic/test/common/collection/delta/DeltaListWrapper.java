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
package org.chromattic.test.common.collection.delta;

import junit.framework.Assert;
import org.chromattic.common.collection.delta.DeltaList;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DeltaListWrapper {

  /** . */
  private final List<Integer> list;

  /** . */
  private final List<Integer> deltaList;

  /** . */
  private final List<Integer> shadow;

  public DeltaListWrapper(List<Integer> list) {
    this.list = list;
    this.deltaList = DeltaList.create(Collections.unmodifiableList(list));
    this.shadow = new ArrayList<Integer>(list);
  }

  public void add(int index, int element) {
    shadow.add(index, element);
    deltaList.add(index, element);
  }

  public void remove(int index) {
    shadow.remove(index);
    deltaList.remove(index);
  }

  public void performOperation(Random rnd) {
    switch (rnd.nextInt(2)) {
      case 0: {
        // Add
        int index = rnd.nextInt(shadow.size() + 1);
        int value = rnd.nextInt(100);
//        System.out.println("Adding " + value + " at index "+ index);
        add(index, value);
        break;
      }
      case 1: {
        if (shadow.size() > 0) {
          int index = rnd.nextInt(shadow.size());
//          System.out.println("Removing value at index "+ index);
          remove(index);
        }
        break;
      }
    }
    checkSame();
  }

  public void checkSame() {
    int size = shadow.size();
    Assert.assertEquals(size, deltaList.size());
    Iterator<Integer> iterator = deltaList.iterator();
    for (int i = 0;i < size;i++) {
      int shadowElement = shadow.get(i);
      int element = deltaList.get(i);
      Assert.assertEquals(shadowElement, element);
      Assert.assertTrue(iterator.hasNext());
      int itElt = iterator.next();
      Assert.assertEquals(shadowElement, itElt);
    }
  }
}
