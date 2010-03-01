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

import org.chromattic.api.annotations.*;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "exo:navigation")
public abstract class NavigationImpl {

  public abstract SiteImpl getSite();

  @OneToMany
  public abstract Collection<NavigationImpl> getChildren();

  @ManyToOne
  public abstract NavigationImpl getParent();

  @OneToOne
  @Owner
  @MappedBy("target")
  public abstract NavigationTargetImpl getTarget();

  public abstract NavigationTargetImpl setTarget(NavigationTargetImpl target);

  public NavigationImpl addChild(String name) {
    NavigationImpl navigation = createNavigation(name);
    getChildren().add(navigation);
    return navigation;
  }

  @Create
  public abstract NavigationImpl createNavigation(String name);

  @Create
  public abstract URLNavigationTargetImpl createURLTarget();

  @Create
  public abstract PageNavigationTargetImpl createPageTarget();

}
