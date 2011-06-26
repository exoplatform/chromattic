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

package org.chromattic.test.onetomany.hierarchical.map;

import org.chromattic.test.onetomany.hierarchical.AbstractToManyTestCase;
import org.chromattic.testgenerator.GroovyTestGeneration;

import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {B5.class, A5.class})
public class PrefixedOneToMany2TestCase extends AbstractToManyTestCase<A5, B5> {

  public Collection<B5> getMany(A5 many) {
    return many.getChildren().values();
  }

  @Override
  public void add(A5 totm_a_3, B5 totm_b_3) {
    totm_a_3.getChildren().put(totm_b_3.getName(), totm_b_3);
  }

  @Override
  public boolean supportsAddToCollection() {
    return false;
  }
}