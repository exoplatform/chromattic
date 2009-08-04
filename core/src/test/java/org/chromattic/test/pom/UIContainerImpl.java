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

package org.chromattic.test.pom;

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.OneToMany;
import org.chromattic.api.annotations.ManyToOne;
import org.chromattic.api.annotations.Create;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "exo:uicontainer")
public abstract class UIContainerImpl extends UIComponentImpl {

  @OneToMany
  public abstract Collection<UIComponentImpl> getChildren();

  @ManyToOne
  public abstract UIContainerImpl getParent();

  @Create
  public abstract UIContainerImpl createContainer(String name);

  @Create
  public abstract UIWindowImpl createWindow(String name);

  @Create
  public abstract UIInsertionImpl createInsertion(String name);
}
