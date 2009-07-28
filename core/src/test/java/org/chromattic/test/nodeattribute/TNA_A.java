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

package org.chromattic.test.nodeattribute;

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.Name;
import org.chromattic.api.annotations.Path;
import org.chromattic.api.annotations.Id;
import org.chromattic.api.annotations.WorkspaceName;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "tna_a")
public abstract class TNA_A {

  @Name
  public abstract String getName();

  public abstract void setName(String name);

  @Id
  public abstract String getId();

  public abstract void setId(String id);

  @Path
  public abstract String getPath();

  public abstract void setPath(String path);

  @WorkspaceName
  public abstract String getWorkspace();

  public abstract void setWorkspace(String workspace);

}
