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
package org.chromattic.api.format;

/**
 * The object formatter defines an interface used to filter the naming of the jcr nodes and properties.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface ObjectFormatter {

  /**
   * Converts a jcr node name to an entity name.
   *
   * @param context the context
   * @param internalName the jcr node name
   * @return the entity name
   */
  String decodeNodeName(FormatterContext context, String internalName);

  /**
   * Converts an entity name to a jcr node name.
   *
   * @param context the context
   * @param externalName the entity name
   * @return the jcr node name
   * @throws IllegalArgumentException if the name cannot be converted due to its nature
   * @throws NullPointerException if the name would convert to a value meaning nullity
   */
  String encodeNodeName(FormatterContext context, String externalName) throws IllegalArgumentException, NullPointerException;

  /**
   * Converts a jcr property name to an entity property name. If the property cannot be decoded
   * then null must be returned.
   *
   * @param context the context
   * @param internalName the jcr property name
   * @return the entity property name
   */
  // String decodePropertyName(FormatterContext context, String internalName);

  /**
   * Converts an entity property name to a jcr property name.
   *
   * @param context the context
   * @param externalName the entity property name
   * @return the jcr property name
   * @throws IllegalArgumentException if the name cannot be converted due to its nature
   * @throws NullPointerException if the name would convert to a value meaning nullity
   */
  // String encodePropertyName(FormatterContext context, String externalName) throws IllegalArgumentException, NullPointerException;

}
