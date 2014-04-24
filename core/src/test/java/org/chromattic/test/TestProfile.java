/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.test;

/**
* @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
* @version $Revision$
*/
public enum TestProfile {

  BASE(AbstractTestCase.APT_INSTRUMENTOR, false, false, false, false),

  PROPERTY_CACHE(AbstractTestCase.APT_INSTRUMENTOR, true, false, false, false),

  PROPERTY_LOAD_GROUP(AbstractTestCase.APT_INSTRUMENTOR, true, true, false, false),

  HAS_NODE(AbstractTestCase.APT_INSTRUMENTOR, false, false, true, false),

  HAS_PROPERTY(AbstractTestCase.APT_INSTRUMENTOR, false, false, false, true)

  ;

  /** . */
  final String instrumentorClassName;

  /** . */
  final boolean propertyCacheEnabled;

  /** . */
  final boolean propertyLoadGroupEnabled;

  /** . */
  final boolean optimizeHasPropertyEnabled;

  /** . */
  final boolean optimizeHasNodeEnabled;

  TestProfile(
    String instrumentorClassName,
    boolean propertyCacheEnabled,
    boolean propertyLoadGroupEnabled,
    boolean optimizeHasPropertyEnabled,
    boolean optimizeHasNodeEnabled) {
    this.instrumentorClassName = instrumentorClassName;
    this.propertyLoadGroupEnabled = propertyLoadGroupEnabled;
    this.propertyCacheEnabled = propertyCacheEnabled;
    this.optimizeHasNodeEnabled = optimizeHasNodeEnabled;
    this.optimizeHasPropertyEnabled = optimizeHasPropertyEnabled;
  }

  public String getInstrumentorClassName() {
    return instrumentorClassName;
  }

  public boolean isPropertyCacheEnabled() {
    return propertyCacheEnabled;
  }

  public boolean isStateCacheDisabled() {
    return !propertyCacheEnabled;
  }

  @Override
  public String toString() {
    return "Config[instrumentorClassName=" + instrumentorClassName + ",stateCacheEnaled=" + propertyCacheEnabled + "" +
      ",optimizeHasNodeEnabled=" + optimizeHasNodeEnabled + ",optimizeHasPropertyEnabled=" + optimizeHasPropertyEnabled + "]";
  }
}
