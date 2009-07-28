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

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractTestCase extends TestCase {

  /** . */
  private static final String APT_INSTRUMENTOR = "org.chromattic.apt.InstrumentorImpl";

  /** . */
  private ChromatticBuilder builder;

  /** . */
  private Chromattic chromattic;

  /** . */
  private String intrumentorClassName;

  @Override
  protected void setUp() throws Exception {
    builder = ChromatticBuilder.create();
    builder.setOption(ChromatticBuilder.INSTRUMENTOR_CLASSNAME, intrumentorClassName);
    createDomain();
    chromattic = builder.build();
  }

  @Override
  protected void tearDown() throws Exception {
    builder = null;
    chromattic = null;
  }

  @Override
  public void run(TestResult result) {

    //
    boolean testWithAPT = false;
    try {
      Class<?> aptInstrumentorClass = Thread.currentThread().getContextClassLoader().loadClass(APT_INSTRUMENTOR);
      testWithAPT = true;
    }
    catch (ClassNotFoundException ignore) {
    }

    // Run test with apt
    if (testWithAPT) {
      intrumentorClassName = APT_INSTRUMENTOR;
      super.run(result);
    }

    // Run test with cglib
    intrumentorClassName = CGLibInstrumentor.class.getName();
    super.run(result);
  }

  public DomainSession login() throws RepositoryException {
    return (DomainSession)chromattic.openSession();
  }

  protected final void addClass(Class<?> clazz) {
    builder.add(clazz);
  }

  protected abstract void createDomain();
}