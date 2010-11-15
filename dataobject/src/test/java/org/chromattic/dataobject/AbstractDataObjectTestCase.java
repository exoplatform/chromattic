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

package org.chromattic.dataobject;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.ChromatticSession;
import org.chromattic.exo.RepositoryBootstrap;
import org.chromattic.ext.ntdef.NTFile;
import org.chromattic.ext.ntdef.NTFolder;
import org.chromattic.ext.ntdef.NTHierarchyNode;
import org.chromattic.ext.ntdef.NTResource;
import org.chromattic.ext.ntdef.Resource;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;

import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractDataObjectTestCase extends TestCase {


  /** . */
  protected Chromattic chromattic;

  /** . */
  protected RepositoryBootstrap bootstrap;

  /** . */
  protected StandaloneContainer container;

  /** . */
  protected ProviderBinder providers;

  /** . */
  protected ResourceBinder binder;

  /** . */
  protected RequestHandlerImpl requestHandler;

  /** . */
  protected GroovyJaxrsPublisher groovyPublisher;

  /** . */
  protected ResourceLauncher launcher;

  @Override
  protected void setUp() throws Exception {

    RepositoryBootstrap bootstrap = new RepositoryBootstrap();
    bootstrap.bootstrap(Thread.currentThread().getContextClassLoader().getResource("conf/dataobject/configuration.xml"));

    // Rest setup
    StandaloneContainer container = StandaloneContainer.getInstance();
    ResourceBinder binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
    RequestHandlerImpl requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
    // reset providers to be sure it is clean
    ProviderBinder.setInstance(new ProviderBinder());
    ProviderBinder providers = ProviderBinder.getInstance();
    ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers));
    binder.clear();
    GroovyJaxrsPublisher groovyPublisher = (GroovyJaxrsPublisher)container.getComponentInstanceOfType(GroovyJaxrsPublisher.class);
    ResourceLauncher launcher = new ResourceLauncher(requestHandler);

    //
    this.providers = providers;
    this.container = container;
    this.requestHandler = requestHandler;
    this.binder = binder;
    this.groovyPublisher  = groovyPublisher;
    this.launcher = launcher;

    //
    ChromatticBuilder builder = ChromatticBuilder.create();
    builder.add(NTFile.class);
    builder.add(NTFolder.class);
    builder.add(NTHierarchyNode.class);
    builder.add(NTResource.class);
    builder.add(GroovyResourceContainer.class);
    Chromattic chromattic = builder.build();

    //
    this.chromattic = chromattic;
    this.bootstrap = bootstrap;
  }

  protected final void saveService(String scriptName, String scriptText) throws UnsupportedEncodingException {
    saveScript(null, scriptName, "script/groovy", scriptText, true);
  }

  protected final void saveDataObject(String scriptName, String scriptText) throws UnsupportedEncodingException {
    saveScript("dependencies", scriptName, "application/x-chromattic+groovy", scriptText, false);
  }

  private void saveScript(
    String folderName,
    String scriptName,
    String scriptContentType,
    String scriptText,
    boolean autoload) throws UnsupportedEncodingException {
    ChromatticSession session = chromattic.openSession();
    try {

      //
      NTFolder parent = null;
      if (folderName != null) {
        parent = session.findByPath(NTFolder.class, folderName);
        if (parent == null) {
          parent = session.insert(NTFolder.class, folderName);
        }
      }

      // Remove any prior object
      NTFile file = session.findByPath(parent, NTFile.class, scriptName);
      if (file != null) {
        session.remove(file);
      }

      //
      file = session.insert(parent, NTFile.class, scriptName);
      GroovyResourceContainer resource = session.create(GroovyResourceContainer.class);
      file.setContent(resource);
      resource.setAutoLoad(autoload);
      resource.update(new Resource(scriptContentType, scriptText));
      session.save();
    }
    finally {
      session.close();
    }
  }
}
