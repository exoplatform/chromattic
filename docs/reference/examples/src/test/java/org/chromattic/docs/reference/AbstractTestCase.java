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

package org.chromattic.docs.reference;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;
import org.chromattic.core.api.ChromatticSessionImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractTestCase extends TestCase {

  /** . */
  private String testName;

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
      testName = ((TestCase)test).getName();
    }
  };

  /** . */
  private Chromattic chromattic;

  /** The session opened during the test. */
  private List<ChromatticSession> sessions = new ArrayList<ChromatticSession>();

  public final ChromatticSessionImpl login() {
    ChromatticSessionImpl session = (ChromatticSessionImpl)chromattic.openSession();
    sessions.add(session);
    return session;
  }

  @Override
  protected void setUp() throws Exception {
    ChromatticBuilder builder = ChromatticBuilder.create();

    //
    String path = "/" + getClass().getSimpleName() + "/" + testName;

    //
    builder.getConfiguration().setOptionValue(ChromatticBuilder.ROOT_NODE_PATH, path);
    builder.getConfiguration().setOptionValue(ChromatticBuilder.ROOT_NODE_TYPE, "nt:unstructured");
    builder.getConfiguration().setOptionValue(ChromatticBuilder.CREATE_ROOT_NODE, true);
    builder.getConfiguration().setOptionValue(ChromatticBuilder.LAZY_CREATE_ROOT_NODE, false);

    //
    for (Class<?> chromatticClass : classes()) {
      builder.add(chromatticClass);
    }

    //
    chromattic = builder.build();
  }

  protected Iterable<Class<?>> classes() {
    return Collections. emptyList();
  }

  public final void run(TestResult result) {
    result.addListener(listener);

    //
    try {
      super.run(result);
    } finally {
      ArrayList<ChromatticSession> copy = new ArrayList<ChromatticSession>(sessions);
      sessions.clear();
      for (ChromatticSession session : copy) {
        if (!session.isClosed()) {
          session.close();
        }
      }
    }

    //
    result.removeListener(listener);
  }


}
