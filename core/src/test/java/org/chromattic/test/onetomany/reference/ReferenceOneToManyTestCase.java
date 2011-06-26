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
package org.chromattic.test.onetomany.reference;

import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@GroovyTestGeneration(chromatticClasses = {C3.class, D3.class})
public class ReferenceOneToManyTestCase extends AbstractOneToManyTestCase<C3, D3> {

  protected C3 getOne(D3 many) {
    return many.getA();
  }

  protected void setOne(D3 many, C3 one) {
    many.setA(one);
  }

  protected Collection<D3> getMany(C3 one) {
    return one.getBs();
  }

  protected Class<C3> getOneSideClass() {
    return C3.class;
  }

  protected Class<D3> getManySideClass() {
    return D3.class;
  }

  protected void createLink(Node referent, String propertyName, Node referenced) throws RepositoryException {
    referent.setProperty(propertyName, referenced);
  }
}
