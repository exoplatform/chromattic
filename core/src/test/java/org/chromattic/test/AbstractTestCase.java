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

package org.chromattic.test;

import org.chromattic.core.DomainSession;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.Chromattic;
import org.chromattic.cglib.CGLibInstrumentor;

import javax.jcr.RepositoryException;

import junit.framework.TestCase;
import junit.framework.TestResult;

import java.util.List;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractTestCase extends TestCase {

  /** . */
  public static final String CHROMATTIC_TEST_MODE = "chromattic.test.mode";

  /** . */
  public static final String MODE_CGLIB = "cglib";

  /** . */
  public static final String MODE_APT = "apt";

  /** . */
  public static final String MODE_CACHE = "cache";

  /** . */
  public static final String MODE_ALL = "all";

  /** . */
  private static final String APT_INSTRUMENTOR = "org.chromattic.apt.InstrumentorImpl";

  /** . */
  private static final String CGLIB_INSTRUMENTOR = CGLibInstrumentor.class.getName();

  /** . */
  private ChromatticBuilder builder;

  /** . */
  private Chromattic chromattic;

  /** . */
  private Config config;

  public Config getConfig() {
    return config;
  }

  @Override
  protected void setUp() throws Exception {
    builder = ChromatticBuilder.create();
    builder.setOption(ChromatticBuilder.STATE_CACHE_ENABLED, config.stateCacheEnabled);
    builder.setOption(ChromatticBuilder.INSTRUMENTOR_CLASSNAME, config.instrumentorClassName);
    createDomain();
    chromattic = builder.build();
  }

  @Override
  protected void tearDown() throws Exception {
    builder = null;
    chromattic = null;
  }

  @Override
  public final void run(TestResult result) {

    List<Config> configs = new LinkedList<Config>();


    //
    boolean aptEnabled = false;
    try {
      Thread.currentThread().getContextClassLoader().loadClass(APT_INSTRUMENTOR);
      aptEnabled = true;
    }
    catch (ClassNotFoundException ignore) {
    }

    //
    String testMode = System.getProperty(CHROMATTIC_TEST_MODE);
    if (testMode == null) {
      testMode = MODE_ALL;
    }

    //
    if (MODE_ALL.equals(testMode)) {
      if (aptEnabled) {
        configs.add(new Config(APT_INSTRUMENTOR, false));
      }
      configs.add(new Config(CGLIB_INSTRUMENTOR, false));
      configs.add(new Config(CGLIB_INSTRUMENTOR, true));
    } else if (MODE_APT.equals(testMode)) {
      configs.add(new Config(APT_INSTRUMENTOR, false));
    } else if (MODE_CGLIB.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, false));
    } else if (MODE_CACHE.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, true));
    }

    //
    for (Config config : configs) {
      this.config = config;
      super.run(result);
    }
  }

  public final DomainSession login() {
    return (DomainSession)chromattic.openSession();
  }

  protected final ChromatticBuilder getBuilder() {
    return builder;
  }

  protected final void addClass(Class<?> clazz) {
    builder.add(clazz);
  }

  protected abstract void createDomain();

  public static class Config {

    /** . */
    private final String instrumentorClassName;

    /** . */
    private final boolean stateCacheEnabled;

    public Config(String instrumentorClassName, boolean stateCacheEnabled) {
      this.instrumentorClassName = instrumentorClassName;
      this.stateCacheEnabled = stateCacheEnabled;
    }

    public String getInstrumentorClassName() {
      return instrumentorClassName;
    }

    public boolean isStateCacheEnabled() {
      return stateCacheEnabled;
    }

    public boolean isStateCacheDisabled() {
      return !stateCacheEnabled;
    }
  }
}