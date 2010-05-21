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

import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.Chromattic;
import org.chromattic.cglib.CGLibInstrumentor;

import javax.jcr.SimpleCredentials;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestListener;
import junit.framework.Test;
import junit.framework.AssertionFailedError;

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
  public static final String MODE_HAS_NODE = "has_node";

  /** . */
  public static final String MODE_HAS_PROPERTY = "has_property";

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

  /** . */
  private String testName;

  /** . */
  private String rootNodePath;

  /** . */
  private final TestListener listener = new TestListener() {
    public void addError(Test test, Throwable throwable) {
    }
    public void addFailure(Test test, AssertionFailedError assertionFailedError) {
    }
    public void endTest(Test test) {
      testName = null;
    }
    public void startTest(Test test) {
      testName = ((AbstractTestCase)test).getName();
    }
  };

  public Config getConfig() {
    return config;
  }

  @Override
  protected void setUp() throws Exception {
    String p1 = getClass().getName().replace('.', '_');
    String p2 = config.propertyCacheEnabled ? "propertycached" : "propertynotcached";
    String p3 = config.optimizeHasNodeEnabled ? "hasnodeoptimized" : "hasnodenotoptimized";
    String p4 = config.optimizeHasPropertyEnabled ? "haspropertyoptimized" : "haspropertynotoptimized";
    String p5 = config.instrumentorClassName.lastIndexOf('.') == -1 ?
      config.instrumentorClassName :
      config.instrumentorClassName.substring(config.instrumentorClassName.lastIndexOf('.') + 1);
    String p6 = testName;

    //
    rootNodePath = "/" + p1 + "/" + p2 + "/" + p3 + "/" + p4  + "/" + p5 + "/" + p6;

    //
    builder = ChromatticBuilder.create();

    //
    createDomain();

    //
    boolean pingRootNode = pingRootNode();

    //
    builder.setOptionValue(ChromatticBuilder.ROOT_NODE_PATH, rootNodePath);
    builder.setOptionValue(ChromatticBuilder.PROPERTY_CACHE_ENABLED, config.propertyCacheEnabled);
    builder.setOptionValue(ChromatticBuilder.INSTRUMENTOR_CLASSNAME, config.instrumentorClassName);
    builder.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_PROPERTY_ENABLED, config.optimizeHasPropertyEnabled);
    builder.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_NODE_ENABLED, config.optimizeHasNodeEnabled);

    //
    if (pingRootNode) {
      builder.setOptionValue(ChromatticBuilder.CREATE_ROOT_NODE, true);
      builder.setOptionValue(ChromatticBuilder.LAZY_CREATE_ROOT_NODE, false);
    }

    //
    chromattic = builder.build();

    // Create virtual root node if required
    if (pingRootNode) {
      ChromatticSessionImpl sess = login();
      sess.getRoot();
      sess.save();
    }
  }

  @Override
  protected void tearDown() throws Exception {
    builder = null;
    chromattic = null;
  }

  @Override
  public final void run(TestResult result) {
    result.addListener(listener);

    //
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
        configs.add(new Config(APT_INSTRUMENTOR, false, false, false));
//        configs.add(new Config(APT_INSTRUMENTOR, true, false, false));
//        configs.add(new Config(APT_INSTRUMENTOR, false, true, false));
//        configs.add(new Config(APT_INSTRUMENTOR, false, false, true));
      }
      configs.add(new Config(CGLIB_INSTRUMENTOR, false, false, false));
      configs.add(new Config(CGLIB_INSTRUMENTOR, true, false, false));
      configs.add(new Config(CGLIB_INSTRUMENTOR, false, true, false));
      configs.add(new Config(CGLIB_INSTRUMENTOR, false, false, true));
    } else if (MODE_APT.equals(testMode)) {
      configs.add(new Config(APT_INSTRUMENTOR, false, false, false));
    } else if (MODE_CGLIB.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, false, false, false));
    } else if (MODE_CACHE.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, true, false, false));
    } else if (MODE_HAS_NODE.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, false, false, true));
    } else if (MODE_HAS_PROPERTY.equals(testMode)) {
      configs.add(new Config(CGLIB_INSTRUMENTOR, true, true, false));
    }

    //
    for (Config config : configs) {
      this.config = config;
      super.run(result);
    }

    //
    result.removeListener(listener);
  }

  public final ChromatticSessionImpl login() {
    SimpleCredentials credentials = new SimpleCredentials("exo", "exo".toCharArray());
    return (ChromatticSessionImpl)chromattic.openSession(credentials);
  }

  protected final <D> void setOptionValue(ChromatticBuilder.Option<D> option, D value) throws NullPointerException {
    builder.setOptionValue(option, value);
  }

  protected final ChromatticBuilder getBuilder() {
    return builder;
  }

  protected final String getRootNodePath() {
    return rootNodePath;
  }

  protected final void addClass(Class<?> clazz) {
    builder.add(clazz);
  }

  /**
   * Returns true if the root node should be created during the test bootstrap. This method can be overriden
   * by unit test to change the behavior.
   *
   * @return the root node ping
   */
  protected boolean pingRootNode() {
    return true;
  }

  protected abstract void createDomain();

  public static class Config {

    /** . */
    private final String instrumentorClassName;

    /** . */
    private final boolean propertyCacheEnabled;

    /** . */
    private final boolean optimizeHasPropertyEnabled;

    /** . */
    private final boolean optimizeHasNodeEnabled;

    public Config(
      String instrumentorClassName,
      boolean propertyCacheEnabled,
      boolean optimizeHasPropertyEnabled,
      boolean optimizeHasNodeEnabled) {
      this.instrumentorClassName = instrumentorClassName;
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
}