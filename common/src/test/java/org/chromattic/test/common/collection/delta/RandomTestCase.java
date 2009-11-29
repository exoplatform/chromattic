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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class RandomTestCase extends TestCase {

  /** . */
  private final int numOps = 1000;

  /** . */
  private final Random rnd = new Random(2);

  public void testFoo() {
    for (int j = 0;j < 10;j++) {
      rnd.setSeed(j);
      List<Integer> list = createRandomList(100);
      DeltaListWrapper wrapper = new DeltaListWrapper(list);
      for (int i = 0;i < numOps;i++) {
        wrapper.performOperation(rnd);
      }
    }
  }

  private List<Integer> createRandomList(int size) {
    ArrayList<Integer> list = new ArrayList<Integer>(3);
    for (int i = 0;i < size;i++) {
      list.add(i);
    }
    Collections.shuffle(list, rnd);
    return list;
  }
}
