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

package org.chromattic.core.api;

import org.chromattic.api.ChromatticBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticBuilderConfigurationFactoryImpl extends ChromatticBuilder.Configuration.Factory {

  /**
   * Options configurable via system properties.
   */
  private final static Set<ChromatticBuilder.Option> SYSTEM_OPTIONS = Collections.unmodifiableSet(new HashSet<ChromatticBuilder.Option>(Arrays.asList(
    ChromatticBuilder.PROPERTY_CACHE_ENABLED,
    ChromatticBuilder.PROPERTY_LOAD_GROUP_ENABLED,
    ChromatticBuilder.JCR_OPTIMIZE_ENABLED,
    ChromatticBuilder.JCR_OPTIMIZE_HAS_PROPERTY_ENABLED,
    ChromatticBuilder.JCR_OPTIMIZE_HAS_NODE_ENABLED,
    ChromatticBuilder.SESSION_LIFECYCLE_CLASSNAME
  )));

  /** . */
  private static final ChromatticBuilder.Configuration DEFAULT_CONFIG;

  static {

    //
    ChromatticBuilder.Configuration config = new ChromatticBuilder.Configuration();

    // Configure system properties options
    if (!Boolean.FALSE.equals(config.getOptionValue(ChromatticBuilder.USE_SYSTEM_PROPERTIES))) {
      for (ChromatticBuilder.Option<?> option : SYSTEM_OPTIONS) {
        String value = System.getProperty(option.getName());
        if (value != null) {
          _set(config, option, value, false);
        }
      }
    }

    config.setOptionValue(ChromatticBuilder.INSTRUMENTOR_CLASSNAME, "org.chromattic.apt.InstrumentorImpl", false);
    config.setOptionValue(ChromatticBuilder.SESSION_LIFECYCLE_CLASSNAME, "org.chromattic.exo.ExoSessionLifeCycle", false);
    config.setOptionValue(ChromatticBuilder.PROPERTY_CACHE_ENABLED, false, false);
    config.setOptionValue(ChromatticBuilder.PROPERTY_LOAD_GROUP_ENABLED, false, false);
    config.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_PROPERTY_ENABLED, false, false);
    config.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_NODE_ENABLED, false, false);
    config.setOptionValue(ChromatticBuilder.ROOT_NODE_PATH, "/", false);
    config.setOptionValue(ChromatticBuilder.CREATE_ROOT_NODE, false, false);
    config.setOptionValue(ChromatticBuilder.LAZY_CREATE_ROOT_NODE, false, false);

    //
    DEFAULT_CONFIG = config;
  }

  // Just for type safety
  private static <D> void _set(ChromatticBuilder.Configuration config, ChromatticBuilder.Option<D> option, String value, boolean overwrite) throws NullPointerException {
    config.setOptionValue(option, option.getType().parse(value), overwrite);
  }

  @Override
  public ChromatticBuilder.Configuration create() {
    return new ChromatticBuilder.Configuration(DEFAULT_CONFIG);
  }
}
