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
package org.chromattic.core.jcr;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Encapsulate all the information required to create a JCR node. This class is immutable.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeDef {

  /** . */
  private final String primaryNodeTypeName;

  /** . */
  private final Set<String> mixinNames;

  public NodeDef(String primaryNodeTypeName, Set<String> mixinNames) throws NullPointerException {
    if (primaryNodeTypeName == null) {
      throw new NullPointerException();
    }
    if (mixinNames == null) {
      throw new NullPointerException();
    }

    //
    Set<String> tmp = new HashSet<String>();
    for (String mixinName : mixinNames) {
      if (mixinName == null) {
        throw new IllegalArgumentException();
      }
      tmp.add(mixinName);
    }

    //
    this.primaryNodeTypeName = primaryNodeTypeName;
    this.mixinNames = Collections.unmodifiableSet(tmp);
  }

  public String getPrimaryNodeTypeName() {
    return primaryNodeTypeName;
  }

  public Set<String> getMixinNames() {
    return mixinNames;
  }

  @Override
  public String toString() {
    return "NodeType[primaryNodeTypeName=" + primaryNodeTypeName + ",mixinNames=" + mixinNames + "]";
  }
}
