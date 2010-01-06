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
package org.chromattic.test.format;

import org.chromattic.api.format.FormatterContext;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class FooPrefixerFormatter extends AbstractObjectFormatter {

  public String decodeNodeName(FormatterContext context, String internalName) {
    String externalName = decodeName(internalName);
    if (externalName == null) {
      throw new IllegalStateException();
    }
    return externalName;
  }

  public String encodeNodeName(FormatterContext context, String externalName) {
    return encodeName(externalName);
  }

  @Override
  public String decodePropertyName(FormatterContext context, String internalName) {
    return decodeName(internalName);
  }

  @Override
  public String encodePropertyName(FormatterContext context, String externalName) {
    return encodeName(externalName);
  }

  private String encodeName(String externalName) {
    if (externalName.length() == 1) {
      return "foo_" + externalName;
    } else {
      throw new IllegalArgumentException();
    }
  }

  private String decodeName(String internalName) {
    if (internalName.startsWith("foo_")) {
      return internalName.substring(4);
    } else {
      return null;
    }
  }
}
