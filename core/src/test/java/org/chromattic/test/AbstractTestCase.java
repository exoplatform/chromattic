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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.core.api.ChromatticSessionImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractTestCase extends TestCase {

  /** . */
  public static final String CHROMATTIC_TEST_PROFILE = "chromattic.test.profile";

  /** . */
  static final String APT_INSTRUMENTOR = "org.chromattic.apt.InstrumentorImpl";

  /** . */
  private static final ThreadLocal<TestProfile> PROFILE = new ThreadLocal<TestProfile>();

  /** . */
  private ChromatticBuilder builder;

  /** . */
  private Chromattic chromattic;

  /** . */
  private TestProfile profile;

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

  public TestProfile getProfile() {
    return profile;
  }

  @Override
  protected void setUp() throws Exception {
    String p1 = getClass().getName().replace('.', '_');
    String p2 = profile.name();
    String p3 = testName;

    //
    rootNodePath = "/" + p1 + "/" + p2 + "/" + p3;

    //
    builder = ChromatticBuilder.create();

    //
    createDomain();

    //
    boolean pingRootNode = pingRootNode();

    //
    builder.setOptionValue(ChromatticBuilder.ROOT_NODE_PATH, rootNodePath);
    builder.setOptionValue(ChromatticBuilder.ROOT_NODE_TYPE, "nt:unstructured");
    builder.setOptionValue(ChromatticBuilder.PROPERTY_CACHE_ENABLED, profile.propertyCacheEnabled);
    builder.setOptionValue(ChromatticBuilder.PROPERTY_LOAD_GROUP_ENABLED, profile.propertyLoadGroupEnabled);
    builder.setOptionValue(ChromatticBuilder.INSTRUMENTOR_CLASSNAME, profile.instrumentorClassName);
    builder.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_PROPERTY_ENABLED, profile.optimizeHasPropertyEnabled);
    builder.setOptionValue(ChromatticBuilder.JCR_OPTIMIZE_HAS_NODE_ENABLED, profile.optimizeHasNodeEnabled);

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
    PROFILE.set(profile);
  }

  @Override
  protected void tearDown() throws Exception {
    builder = null;
    chromattic = null;
    PROFILE.remove();
  }

  public static TestProfile getCurrentProfile() {
    return PROFILE.get();
  }

  @Override
  public final void run(TestResult result) {
    result.addListener(listener);

    //
    List<TestProfile> profiles = new LinkedList<TestProfile>();

    //
    String testProfile = System.getProperty(CHROMATTIC_TEST_PROFILE);

    //
    if (testProfile == null || testProfile.trim().length() == 0) {
      profiles.add(TestProfile.BASE);
      profiles.add(TestProfile.PROPERTY_CACHE);
      profiles.add(TestProfile.PROPERTY_LOAD_GROUP);
      profiles.add(TestProfile.HAS_NODE);
      profiles.add(TestProfile.HAS_PROPERTY);
    } else {
      try {
        profiles.add(TestProfile.valueOf(testProfile.trim().toUpperCase()));
      }
      catch (IllegalArgumentException e) {
        AssertionFailedError afe = new AssertionFailedError("Invalid test profile " + testProfile);
        afe.initCause(e);
        throw afe;
      }
    }

    //
    for (TestProfile profile : profiles) {
      this.profile = profile;
      try {
        super.run(result);
      }
      finally {
        ArrayList<ChromatticSessionImpl> copy = new ArrayList<ChromatticSessionImpl>(sessions);
        sessions.clear();
        for (ChromatticSession session : copy) {
          if (!session.isClosed()) {
            session.close();
          }
        }
      }
    }

    //
    result.removeListener(listener);
  }

  /** The session opened during the test. */
  private List<ChromatticSessionImpl> sessions = new ArrayList<ChromatticSessionImpl>();

  public final ChromatticSessionImpl login() {
    ChromatticSessionImpl session = (ChromatticSessionImpl)chromattic.openSession();
    sessions.add(session);
    return session;
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

  protected final String getNodeTypeName(Class<?> clazz) {
    PrimaryType primaryType = clazz.getAnnotation(PrimaryType.class);
    if (primaryType != null) {
      return primaryType.name();
    } else {
      MixinType mixinType = clazz.getAnnotation(MixinType.class);
      if (mixinType != null) {
        return mixinType.name();
      }
    }
    return null;
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

}