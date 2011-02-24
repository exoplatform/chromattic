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

package org.chromattic.dataobject.runtime;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.ComponentRequestLifecycle;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;

import javax.inject.Provider;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticSessionProvider implements Provider<ChromatticSession>, ComponentRequestLifecycle {

  /** . */
  private static final ThreadLocal<ChromatticSessionProvider> current = new ThreadLocal<ChromatticSessionProvider>();

  /** . */
  final RepositoryService repositoryService;

  static ChromatticSessionProvider getCurrent() {
    return current.get();
  }

  /** . */
  final String rootNodePath;

  /** . */
  final String workspaceName;

  /** . */
  final String rootNodeType;

  public ChromatticSessionProvider(InitParams params, RepositoryService repositoryService) {
    ValueParam rootNodePathVP = params.getValueParam("rootNodePath");
    String rootNodePath = rootNodePathVP != null ? rootNodePathVP.getValue() : "/";

    //
    ValueParam workspaceNameVP = params.getValueParam("workspaceName");
    String workspaceName = workspaceNameVP != null ? workspaceNameVP.getValue() : null;

    //
    ValueParam rootNodeTypeVP = params.getValueParam("rootNodeType");
    String rootNodeType = rootNodeTypeVP != null ? rootNodeTypeVP.getValue() : null;

    //
    this.repositoryService = repositoryService;
    this.rootNodePath = rootNodePath;
    this.workspaceName = workspaceName;
    this.rootNodeType = rootNodeType;
  }

  public ChromatticSession get() {
    return new DataObjectChromatticSession(this);
  }

  public void startRequest(ExoContainer container) {
    current.set(this);
  }

  public void endRequest(ExoContainer container) {
    current.set(null);
    DataObjectChromatticSession.cleanup();
  }
}
