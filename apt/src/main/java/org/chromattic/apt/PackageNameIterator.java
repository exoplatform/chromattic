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

package org.chromattic.apt;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PackageNameIterator implements Iterator<String> {

  public static Iterable<String> with(final String packageName) {
    if (packageName == null) {
      throw new NullPointerException();
    }
    return new Iterable<String>() {
      public Iterator<String> iterator() {
        return new PackageNameIterator(packageName);
      }
    };
  }

  /** . */
  private String packageName;

  PackageNameIterator(String packageName) {
    if (packageName == null) {
      throw new NullPointerException();
    }
    this.packageName = packageName;
  }

  public boolean hasNext() {
    return packageName.lastIndexOf('.') != -1;
  }

  public String next() {
    String next = packageName;
    int pos = packageName.lastIndexOf('.');
    if (pos == -1) {
      throw new NoSuchElementException();
    }
    packageName = packageName.substring(0, pos);
    return next;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
