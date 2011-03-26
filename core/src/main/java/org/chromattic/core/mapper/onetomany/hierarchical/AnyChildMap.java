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

package org.chromattic.core.mapper.onetomany.hierarchical;

import org.chromattic.core.ObjectContext;
import org.chromattic.api.format.CodecFormat;

import java.util.AbstractMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class AnyChildMap extends AbstractMap<String, Object> {

  /** . */
  final CodecFormat<String, String> keyFormat;

  /** . */
  final ObjectContext parentCtx;

  /** . */
  final Class<?> relatedClass;

  /** . */
  private final AnyChildEntrySet entries;

  public AnyChildMap(
    CodecFormat<String, String> keyFormat,
    ObjectContext parentCtx,
    Class<?> relatedClass) {
    this.keyFormat = keyFormat;
    this.relatedClass = relatedClass;
    this.entries = new AnyChildEntrySet(this);
    this.parentCtx = parentCtx;
  }

  @Override
  public Object put(String key, Object value) {
    if (keyFormat != null) {
      key = keyFormat.encode(key);
    }

    //
    parentCtx.addChild(key, value);

    // We don't support replacement yet
    return null;
  }

  public Set<Entry<String, Object>> entrySet() {
    return entries;
  }
}
