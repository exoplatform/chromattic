/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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

package org.chromattic.metamodel.bean;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ValueKind {

  private ValueKind() {
  }

  public static class Single extends ValueKind {
    private Single() {
    }
  }

  public static class Multi extends ValueKind {
    private Multi() {
    }
  }

  public static class Array extends Multi {
    private Array() {
    }
  }

  public static class Collection extends Multi {
    private Collection() {
    }
  }

  public static class List extends Multi {
    private List() {
    }
  }

  public static class Map extends Multi {
    private Map() {
    }
  }

  /** . */
  public static final Single SINGLE = new Single();

  /** . */
  public static final Array ARRAY = new Array();

  /** . */
  public static final Collection COLLECTION = new Collection();

  /** . */
  public static final List LIST = new List();

  /** . */
  public static final Map MAP = new Map();

}