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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReferenceOneToManyTestCase extends OneToManyTestCase<TOTMR_A_3, TOTMR_B_3> {

  protected TOTMR_A_3 getOne(TOTMR_B_3 many) {
    return many.getA();
  }

  protected void setOne(TOTMR_B_3 many, TOTMR_A_3 one) {
    many.setA(one);
  }

  protected Collection<TOTMR_B_3> getMany(TOTMR_A_3 one) {
    return one.getBs();
  }

  protected Class<TOTMR_A_3> getOneSideClass() {
    return TOTMR_A_3.class;
  }

  protected Class<TOTMR_B_3> getManySideClass() {
    return TOTMR_B_3.class;
  }

  protected String getOneNodeType() {
    return "totmr_a";
  }

  protected String getManyNodeType() {
    return "totmr_b";
  }

  protected void createLink(Node referent, String propertyName, Node referenced) throws RepositoryException {
    referent.setProperty(propertyName, referenced);
  }
}
