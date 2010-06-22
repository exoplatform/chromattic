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

package org.chromattic.metamodel.mapping.jcr;

import org.chromattic.metamodel.mapping.NodeTypeKind;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class NodeTypeMapping {

  /** . */
  private final String typeName;

  /** . */
  private final NodeTypeKind kind;

  /** . */
  private final boolean orderable;

  /** . */
  private final boolean _abstract;

  public NodeTypeMapping(String typeName, NodeTypeKind kind, boolean orderable, boolean _abstract) {
    if (typeName == null) {
      throw new NullPointerException();
    }
    if (kind == null) {
      throw new NullPointerException();
    }
    
    //
    this.typeName = typeName;
    this.kind = kind;
    this.orderable = orderable;
    this._abstract = _abstract;
  }

  public String getTypeName() {
    return typeName;
  }

  public NodeTypeKind getKind() {
    return kind;
  }

  public boolean isOrderable() {
    return orderable;
  }

  public boolean isAbstract() {
    return _abstract;
  }
}
