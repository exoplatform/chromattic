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

package org.chromattic.common.collection.wrapped;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class LongWrappedArrayList extends PrimitiveWrappedArrayList<Long, long[]> {

  public LongWrappedArrayList(int size) {
    this(new long[size]);
  }

  public LongWrappedArrayList(long[] array) {
    super(array);
  }

  @Override
  protected Long get(long[] array, int index) {
    return array[index];
  }

  @Override
  protected int size(long[] array) {
    return array.length;
  }

  @Override
  protected void set(long[] array, int index, Long element) {
    array[index] = element;
  }
}