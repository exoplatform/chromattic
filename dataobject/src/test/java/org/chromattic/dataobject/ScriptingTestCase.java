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

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.ext.groovy.BaseResourceId;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.services.rest.tools.ResourceLauncher;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ScriptingTestCase extends AbstractDataObjectTestCase {


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
    super.setUp();

    //
    container = StandaloneContainer.getInstance();
    binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
    requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
    // reset providers to be sure it is clean
    ProviderBinder.setInstance(new ProviderBinder());
    providers = ProviderBinder.getInstance();
    ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers));
    binder.clear();
    groovyPublisher = (GroovyJaxrsPublisher)container.getComponentInstanceOfType(GroovyJaxrsPublisher.class);
    launcher = new ResourceLauncher(requestHandler);
  }

  private static final String script =
    "@javax.ws.rs.Path(\"a\")"
      + "class GroovyResource {"
      + "@javax.ws.rs.GET @javax.ws.rs.Path(\"{who}\")"
      + "def m0(@javax.ws.rs.PathParam(\"who\") String who) { return (\"hello \" + who)}"
      + "}";

  public void testLoad() throws Exception {
    saveService("script.groovy", script);

    //
    ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
    ContainerResponse resp = launcher.service("GET", "/a/groovy", "", null, null, writer, null);
    assertEquals(200, resp.getStatus());
    assertEquals("hello groovy", new String(writer.getBody()));
  }

  public void testFoo() throws Exception {
    groovyPublisher.publishSingleton(script, new BaseResourceId("g1"), null);

    //
    ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
    ContainerResponse resp = launcher.service("GET", "/a/groovy", "", null, null, writer, null);
    assertEquals(200, resp.getStatus());
    assertEquals("hello groovy", new String(writer.getBody()));
  }
}
